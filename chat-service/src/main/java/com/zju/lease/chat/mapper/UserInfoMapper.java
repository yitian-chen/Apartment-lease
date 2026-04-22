package com.zju.lease.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zju.lease.model.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    UserInfo selectOneByPhone(String phone);

    UserInfo selectByPhone(@Param("phone") String phone);

    List<UserInfo> selectByNicknameLike(@Param("nickname") String nickname);
}
