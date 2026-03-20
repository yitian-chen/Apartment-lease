package com.zju.lease.web.app.service.impl;

import com.zju.lease.common.constant.RedisConstant;
import com.zju.lease.common.exception.LeaseException;
import com.zju.lease.common.result.ResultCodeEnum;
import com.zju.lease.web.app.service.LoginService;
import com.zju.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void getCode(String phone) throws Exception {
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;

        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - ttl < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC) {
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        String code = smsService.sendCode(phone);

        redisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);
    }
}
