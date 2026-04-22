package com.zju.lease.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zju.lease.model.entity.ChatMessage;
import com.zju.lease.chat.vo.chat.ChatHistoryVo;

public interface ChatMessageService extends IService<ChatMessage> {

    void saveMessageAsync(Long conversationId, Long fromId, String message);

    ChatHistoryVo listMessagesByUsers(Long userId1, Long userId2);
}
