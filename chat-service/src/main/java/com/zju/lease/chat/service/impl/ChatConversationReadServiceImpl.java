package com.zju.lease.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.lease.model.entity.ChatConversationRead;
import com.zju.lease.chat.mapper.ChatConversationReadMapper;
import com.zju.lease.chat.service.ChatConversationReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatConversationReadServiceImpl extends ServiceImpl<ChatConversationReadMapper, ChatConversationRead>
    implements ChatConversationReadService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String VIEWING_PREFIX = "chat:viewing:";
    private static final long VIEWING_TTL_MINUTES = 5;

    @Override
    @Async
    public void incrementUnreadAsync(Long userId, Long conversationId) {
        // 如果用户正在查看这个会话，直接标记已读，不增加未读数
        if (isViewingConversation(userId, conversationId)) {
            markAsRead(userId, conversationId);
            return;
        }

        ChatConversationRead record = baseMapper.selectByUserIdAndConversationId(userId, conversationId);
        if (record == null) {
            record = new ChatConversationRead();
            record.setUserId(userId);
            record.setConversationId(conversationId);
            record.setUnreadCount(1);
            record.setLastReadTime(new Date());
            baseMapper.insert(record);
        } else {
            record.setUnreadCount(record.getUnreadCount() + 1);
            baseMapper.updateById(record);
        }
    }

    @Override
    public void markAsRead(Long userId, Long conversationId) {
        ChatConversationRead record = baseMapper.selectByUserIdAndConversationId(userId, conversationId);
        if (record != null) {
            record.setUnreadCount(0);
            record.setLastReadTime(new Date());
            baseMapper.updateById(record);
        } else {
            record = new ChatConversationRead();
            record.setUserId(userId);
            record.setConversationId(conversationId);
            record.setUnreadCount(0);
            record.setLastReadTime(new Date());
            baseMapper.insert(record);
        }
    }

    @Override
    public void markAsViewingConversation(Long userId, Long conversationId) {
        stringRedisTemplate.opsForValue().set(VIEWING_PREFIX + userId,
                conversationId.toString(), VIEWING_TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void leaveConversation(Long userId) {
        stringRedisTemplate.delete(VIEWING_PREFIX + userId);
    }

    @Override
    public boolean isViewingConversation(Long userId, Long conversationId) {
        String viewingId = stringRedisTemplate.opsForValue().get(VIEWING_PREFIX + userId);
        return viewingId != null && viewingId.equals(conversationId.toString());
    }
}
