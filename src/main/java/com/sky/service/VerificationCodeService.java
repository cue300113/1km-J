package com.sky.service;

import com.sky.enti.VerificationCodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private RedisService redisService;

    // 存储验证码的使用Redis
    @Autowired
    private VerificationCodeInfo verificationCodeInfo;


    // 验证码有效期（5分钟）


    public String generateAndStoreCode(String email) {
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 存储验证码信息
        verificationCodeInfo.setCode(code);
        verificationCodeInfo.setTimestamp(System.currentTimeMillis());
        verificationCodeInfo.setUsed(false);
        verificationCodeInfo.setEmail(email);

        // 存储验证码信息，使用Redis等缓存技术
        redisService.set(email, verificationCodeInfo,5, TimeUnit.MINUTES);

        return code;
    }

    public boolean verifyCode(String email, String inputCode) {
        VerificationCodeInfo codeInfo = (VerificationCodeInfo) redisService.get(email);
        if (codeInfo == null) {
            return false;
        }

        // 验证码正确
        if (codeInfo.getCode().equals(inputCode)) {
            //验证码成功后立即删除
            redisService.delete(email);
            return true;
        }
        return false;
    }
    }