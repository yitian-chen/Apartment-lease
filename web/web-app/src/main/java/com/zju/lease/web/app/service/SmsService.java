package com.zju.lease.web.app.service;

public interface SmsService {

    String sendCode(String phone) throws Exception;
}
