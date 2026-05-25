package com.zju.lease.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zju.lease.model.entity.ChatConversationRead;

public interface ChatConversationReadService extends IService<ChatConversationRead> {

    void incrementUnreadAsync(Long userId, Long conversationId);

    void markAsRead(Long userId, Long conversationId);

    /** 记录用户正在查看某个会话（TTL 5 分钟自动过期） */
    void markAsViewingConversation(Long userId, Long conversationId);

    /** 用户离开会话页面 */
    void leaveConversation(Long userId);

    /** 判断用户是否正在查看某个会话 */
    boolean isViewingConversation(Long userId, Long conversationId);
}
