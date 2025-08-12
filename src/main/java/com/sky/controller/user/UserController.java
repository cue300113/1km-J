package com.sky.controller.user;


import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.enti.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.dto.UserRLDto;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/register")
    public Result register(@RequestBody UserRLDto userrldto) {
        log.info("用户注册:{}", userrldto);
        if(userService.getUserByname(userrldto.getUsername()) != null)
        {
            return Result.error("用户已存在，注册失败");
        }
        userService.register(userrldto);
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
        session.setAttribute("roomId", roomVo.getRoomId());
        return Result.success(roomVo);
    }

    @GetMapping("/messages")
    public Result<List<MessageVo>> getMessages(@RequestParam Long roomId) {
        // 查询并返回消息列表
        return Result.success(userService.getMessages(roomId));
    }

    @PostMapping("/sendMessage")
    public Result sendMessage(@RequestBody MessageDto messageDto) {
        log.info("用户发送消息:{}", messageDto);
        userService.sendMessage(messageDto);
        return Result.success();
    }


}

