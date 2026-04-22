package com.zju.lease.chat.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "聊天历史")
public class ChatHistoryVo {

    @Schema(description = "消息列表")
    private List<ChatMessageVo> messages;

    @Schema(description = "用户头像映射")
    private Map<Long, String> userAvatars;
}
