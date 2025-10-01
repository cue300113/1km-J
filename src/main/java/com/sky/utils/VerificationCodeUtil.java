package com.sky.utils;

import java.util.Random;

/**
 * 验证码工具类
 */
public class VerificationCodeUtil {
    
    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    
    /**
     * 生成6位数字验证码
     * @return 验证码字符串
     */
    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        return code.toString();
    }
    
    /**
     * 验证验证码格式是否正确
     * @param code 验证码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            return false;
        }
        
        for (char c : code.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        
        return true;
    }
}

