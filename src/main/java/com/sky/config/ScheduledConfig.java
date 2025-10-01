package com.sky.config;

import com.sky.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConfig {

    @Autowired
    private UserMapper userMapper;

    /**
     * 每天凌晨2点删除1天前的消息
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteOldMessages() {
        try {
            log.info("开始清理过期消息...");
            userMapper.deleteOldMessages();
            log.info("过期消息清理完成");
        } catch (Exception e) {
            log.error("清理过期消息失败: {}", e.getMessage());
        }
    }
}
