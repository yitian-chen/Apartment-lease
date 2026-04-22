package com.zju.lease.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.lease.model.entity.ChatConversation;
import com.zju.lease.model.entity.ChatMessage;
import com.zju.lease.model.entity.UserInfo;
import com.zju.lease.chat.mapper.ChatConversationMapper;
import com.zju.lease.chat.mapper.ChatMessageMapper;
import com.zju.lease.chat.mapper.UserInfoMapper;
import com.zju.lease.chat.service.ChatMessageService;
import com.zju.lease.chat.vo.chat.ChatHistoryVo;
import com.zju.lease.chat.vo.chat.ChatMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatConversationMapper chatConversationMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Async
    public void saveMessageAsync(Long conversationId, Long fromId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversationId(conversationId);
        chatMessage.setFromId(fromId);
        chatMessage.setMessage(message);
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public ChatHistoryVo listMessagesByUsers(Long userId1, Long userId2) {
        ChatConversation conversation = chatConversationMapper.selectByTwoUsers(userId1, userId2);
        if (conversation == null) {
            return new ChatHistoryVo();
        }

        List<ChatMessage> messages = chatMessageMapper.selectByConversationId(conversation.getId());

        Map<Long, String> userAvatars = new HashMap<>();
        userAvatars.put(userId1, null);
        for (ChatMessage msg : messages) {
            userAvatars.putIfAbsent(msg.getFromId(), null);
        }

        for (Long userId : userAvatars.keySet()) {
            UserInfo user = userInfoMapper.selectById(userId);
            if (user != null) {
                userAvatars.put(userId, user.getAvatarUrl());
            }
        }

        List<ChatMessageVo> messageList = new ArrayList<>();
        for (ChatMessage msg : messages) {
            ChatMessageVo vo = new ChatMessageVo();
            vo.setId(msg.getId());
            vo.setFromId(msg.getFromId());
            UserInfo fromUser = userInfoMapper.selectById(msg.getFromId());
            vo.setFromName(fromUser != null ? fromUser.getNickname() : "未知用户");
            vo.setMessage(msg.getMessage());
            vo.setCreateTime(msg.getCreateTime());
            vo.setFromMe(msg.getFromId().equals(userId1));
            messageList.add(vo);
        }

        ChatHistoryVo result = new ChatHistoryVo();
        result.setMessages(messageList);
        result.setUserAvatars(userAvatars);
        return result;
    }
}
