package com.zju.lease.web.admin.service;

import com.zju.lease.web.admin.vo.login.CaptchaVo;
import com.zju.lease.web.admin.vo.login.LoginVo;
import com.zju.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getLoginUserInfoById(Long userId);
}
