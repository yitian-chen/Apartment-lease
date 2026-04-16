package com.zju.lease.upms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zju.lease.model.entity.SystemUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SystemUserMapper extends BaseMapper<SystemUser> {

    @Select("SELECT id, username, password, name, type, phone, avatar_url, additional_info, post_id, status FROM system_user WHERE is_deleted = 0 AND username = #{username} LIMIT 1")
    SystemUser selectOneByUsername(@Param("username") String username);
}
