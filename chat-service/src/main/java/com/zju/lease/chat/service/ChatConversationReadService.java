package com.zju.lease.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zju.lease.model.entity.ChatConversationRead;

public interface ChatConversationReadService extends IService<ChatConversationRead> {

    void incrementUnreadAsync(Long userId, Long conversationId);

    void markAsRead(Long userId, Long conversationId);
}
