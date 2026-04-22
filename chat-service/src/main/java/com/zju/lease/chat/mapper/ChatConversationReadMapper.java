package com.zju.lease.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zju.lease.model.entity.ChatConversationRead;
import org.apache.ibatis.annotations.Param;

public interface ChatConversationReadMapper extends BaseMapper<ChatConversationRead> {

    ChatConversationRead selectByUserIdAndConversationId(@Param("userId") Long userId, @Param("conversationId") Long conversationId);
}
