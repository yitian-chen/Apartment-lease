package com.zju.lease.web.app.service;

import com.zju.lease.web.app.vo.user.LoginVo;

public interface LoginService {
    void getCode(String phone) throws Exception;

    String login(LoginVo loginVo);
}
