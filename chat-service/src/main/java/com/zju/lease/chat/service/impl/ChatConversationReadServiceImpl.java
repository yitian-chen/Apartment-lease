package com.zju.lease.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.lease.model.entity.ChatConversationRead;
import com.zju.lease.chat.mapper.ChatConversationReadMapper;
import com.zju.lease.chat.service.ChatConversationReadService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChatConversationReadServiceImpl extends ServiceImpl<ChatConversationReadMapper, ChatConversationRead>
    implements ChatConversationReadService {

    @Override
    @Async
    public void incrementUnreadAsync(Long userId, Long conversationId) {
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
}
