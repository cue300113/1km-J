package com.sky.controller.user;


import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmailDto;
import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.enti.FileRedisData;
import com.sky.enti.Message;
import com.sky.enti.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.dto.UserRLDto;
import com.sky.service.EmailService;
import com.sky.service.FileService;
import com.sky.service.UserService;
import com.sky.service.VerificationCodeService;
import com.sky.utils.AliOssUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.FileVo;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "https://1kmchat.xin"})
public class UserController extends BaseContext {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;


    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private FileRedisData fileRedisData;
    // 发送验证码接口
    @PostMapping("/send-verification-code")
    public Result sendVerificationCode(@RequestBody EmailDto emailDto) {
        String email = emailDto.getEmail();

        try {
            // 验证邮箱格式
            if (!isValidEmail(email)) {
                return Result.error("邮箱格式不正确");
            }

            // 生成验证码
            String verificationCode = verificationCodeService.generateAndStoreCode(email);

            // 发送邮件
            emailService.sendVerificationCode(email, verificationCode);

            return Result.success();


        } catch (Exception e) {
            return Result.error("发送验证码失败");
        }
    }
    @PostMapping("/logout")
    public Result logout() {
        userService.removeUser(BaseContext.getCurrentId());
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody UserRLDto userrldto) {
        if (!verificationCodeService.verifyCode(userrldto.getEmail(), userrldto.getVerificationCode())) {
            return Result.error("验证码错误");
        }
        User user = userService.getUserByname(userrldto.getUsername());
        user.setPassword(userrldto.getPassword());
        userService.updateUser(user);
        return Result.success();
    }

    // 验证邮箱格式
    private boolean isValidEmail(String email) {
        String emailRegex = "^[1-9]\\d{4,10}@qq\\.com$";
        return email.matches(emailRegex);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserRLDto userrldto) {
        log.info("用户注册:{}", userrldto);
        if(userService.getUserByname(userrldto.getUsername()) != null)
        {
            return Result.error("用户已存在，注册失败");
        }
        if (!verificationCodeService.verifyCode(userrldto.getEmail(), userrldto.getVerificationCode())) {
            return Result.error("验证码错误");
        }
        userService.register(userrldto);
        return Result.success();
    }
    @PostMapping("/check-username")
    public Result checkUser(@RequestBody UserRLDto userrldto) {
        log.info("检查用户名是否存在", userrldto);
        if(userService.getUserByname(userrldto.getUsername()) != null)
        {
            return Result.error("用户已存在");
        }
        return Result.success();
    }


    @PostMapping("/login")
    public Result<Token> login(@RequestBody UserRLDto userrldto, HttpSession session) {
        log.info("用户登录:{}", userrldto);
        User user = userService.getUserByname(userrldto.getUsername());
        if(user==null){
            return Result.error("用户不存在，登录失败");
        }
        if(!user.getPassword().equals(userrldto.getPassword())){
            return Result.error("密码错误，登录失败");
        }

        session.setAttribute("userId", user.getId());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        Token token1 = Token.builder().token(token).build();
        return Result.success(token1);
    }


    @PostMapping("/enterRoom")
    public Result<RoomVo> enterRoom(@RequestBody RoomDto roomDto,HttpSession session) {
        log.info("用户进入房间:{}", roomDto);
        RoomVo roomVo= userService.Inroom(roomDto);
        userService.setRoomUser(getCurrentId(),roomVo.getRoomId());
        session.setAttribute("roomId", roomVo.getRoomId());
        return Result.success(roomVo);
    }

    @GetMapping("/messages")
    public Result<List<Message>> getMessages(@RequestParam Long roomId) {
        // 查询并返回消息列表
        return Result.success(userService.getMessages(roomId));
    }

    @PostMapping("/sendMessage")
    public Result sendMessage(@RequestBody MessageDto messageDto) {
        log.info("#####用户发送消息:{}", messageDto);
        Long userid = getCurrentId();
        userService.sendMessage(userid,messageDto);
        return Result.success();
    }


    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam Long roomId) {
        // 2. 验证文件
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 3. 检查文件类型
        if (!isAllowedFileType(file)) {
            return Result.error("类型错误");
        }
        // 4. 检查文件大小
        if (!isValidFileSize(file)) {
            return Result.error("大小错误");
        }
        FileVo fileVo = userService.uploadFile(file, roomId,getCurrentId());
        log.info("用户id:{}", getCurrentId());
        log.info(Result.success(fileVo).toString());
        return Result.success(fileVo);
    }
    private boolean isAllowedFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.startsWith("image/") ||
                        contentType.startsWith("video/")
        );
    }

    private boolean isValidFileSize(MultipartFile file) {
        long size = file.getSize();
        String contentType = file.getContentType();

        if (contentType.startsWith("image/")) {
            return size <= 10 * 1024 * 1024; // 10MB
        } else if (contentType.startsWith("video/")) {
            return size <= 50 * 1024 * 1024; // 50MB
        }

        return false;
    }




}

