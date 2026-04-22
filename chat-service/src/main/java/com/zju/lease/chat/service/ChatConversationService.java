package com.zju.lease.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zju.lease.model.entity.ChatConversation;
import com.zju.lease.chat.vo.chat.ChatConversationVo;

import java.util.List;

public interface ChatConversationService extends IService<ChatConversation> {

    List<ChatConversation> listConversationsByUserId(Long userId);

    ChatConversation getConversationByTwoUsers(Long userId1, Long userId2);

    ChatConversation getOrCreateConversation(Long userId1, Long userId2);

    List<ChatConversationVo> listConversationVosByUserId(Long userId);
}
