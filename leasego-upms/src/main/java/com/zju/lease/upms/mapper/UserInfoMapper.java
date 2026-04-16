package com.zju.lease.upms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zju.lease.model.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Select("SELECT id, phone, password, avatar_url, nickname, status FROM user_info WHERE phone = #{phone} AND is_deleted = 0 LIMIT 1")
    UserInfo selectOneByPhone(@Param("phone") String phone);
}
