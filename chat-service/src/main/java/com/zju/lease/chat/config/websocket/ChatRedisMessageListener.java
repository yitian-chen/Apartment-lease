package com.zju.lease.chat.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.lease.model.entity.ChatConversation;
import com.zju.lease.model.entity.RedisChatMessage;
import com.zju.lease.model.entity.ChatResponseMessage;
import com.zju.lease.chat.service.ChatConversationReadService;
import com.zju.lease.chat.service.ChatConversationService;
import com.zju.lease.chat.service.ChatMessageService;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChatRedisMessageListener implements MessageListener {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChatConversationService chatConversationService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatConversationReadService chatConversationReadService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());

        try {
            if ("chat_msg_channel".equals(channel)) {
                RedisChatMessage redisMsg = objectMapper.readValue(message.getBody(), RedisChatMessage.class);
                Long toId = redisMsg.getToId();
                Long fromId = redisMsg.getFromId();
                String msgText = redisMsg.getMessage();

                ChatConversation conversation = chatConversationService.getOrCreateConversation(fromId, toId);

                chatMessageService.saveMessageAsync(conversation.getId(), fromId, msgText);

                chatConversationReadService.incrementUnreadAsync(toId, conversation.getId());

                Session targetSession = ChatEndpoint.onlineUsers.get(toId);
                if (targetSession != null && targetSession.isOpen()) {
                    String responseJson = objectMapper.writeValueAsString(new ChatResponseMessage(
                            false,
                            fromId,
                            redisMsg.getFromName(),
                            msgText
                    ));
                    try {
                        targetSession.getBasicRemote().sendText(responseJson);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if ("chat_sys_channel".equals(channel)) {
                String body = new String(message.getBody());
                ChatEndpoint.onlineUsers.values().forEach(session -> {
                    if (session.isOpen()) {
                        try {
                            session.getBasicRemote().sendText(body);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
