package com.sky.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    //发送邮箱验证码
    public void sendVerificationCode(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("1km聊天室 - 邮箱验证码");
            String template = "<html>\n" +
    "<body>\n" +
    "    <h2>1km聊天室</h2>\n" +
    "    <p>您的验证码是：<strong style=\"color: #1890ff; font-size: 24px;\">%s</strong></p>\n" +
    "    <p>验证码有效期为5分钟，请及时使用。</p>\n" +
    "    <p>如果这不是您的操作，请忽略此邮件。</p>\n" +
    "    <hr>\n" +
    "    <p style=\"color: #999; font-size: 12px;\">此邮件由系统自动发送，请勿回复。</p>\n" +
    "</body>\n" +
    "</html>";
            String content = String.format(template, verificationCode);

            helper.setText(content, true);

            mailSender.send(message);
            System.out.println("验证码邮件发送成功: " + toEmail);

        } catch (Exception e) {
            System.err.println("验证码邮件发送失败: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈信息
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }
}
