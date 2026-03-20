package com.zju.lease.web.app.service.impl;

import com.aliyun.tea.*;
import com.zju.lease.common.sms.AliyunSMSProperties;
import com.zju.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private com.aliyun.dypnsapi20170525.Client client;

    @Override
    public String sendCode(String phone) throws Exception {
        com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest()
                .setSignName("速通互联验证码")
                .setTemplateCode("100001")
                .setPhoneNumber(phone)
                .setReturnVerifyCode(true)
                .setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse resp = client.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest, runtime);
        System.out.println(new com.google.gson.Gson().toJson(resp));
        return resp.getBody().getModel().getVerifyCode();
    }
}
