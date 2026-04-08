package com.zju.lease.web.app.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "房东信息VO(用于联系房东)")
public class LandlordInfoVo {

    @Schema(description = "用户ID(用于发起聊天)")
    private Long userId;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;
}
