package com.sky.enti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 验证码信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VerificationCodeInfo {


    private String email;

    /**
     * 验证码
     */
    private String code;
    
    /**
     * 生成时间戳
     */
    private Long timestamp;
    
    /**
     * 是否已使用
     */
    @Builder.Default
    private Boolean used = false;
}