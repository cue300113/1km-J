package com.sky.controller.user;


import com.alibaba.fastjson2.JSON;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.MessageDto;
import com.sky.properties.JwtProperties;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.Mesaage11;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat")
@Component
@Slf4j
public class ChatEndpint implements ApplicationContextAware {
    // 房间ID -> 该房间所有连接
    private static final Map<Long, Set<Session>> roomSessions = new ConcurrentHashMap<>();

    // 注入JWT配置（静态注入）
    private static JwtProperties jwtProperties;
    private static ApplicationContext applicationContext;

   /* @Autowired
    public void setJwtProperties(JwtProperties jwtProperties) {
        ChatEndpint.jwtProperties = jwtProperties;
    }
    @Autowired
    private UserService userService;*/

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ChatEndpint.applicationContext = applicationContext;
        ChatEndpint.jwtProperties = applicationContext.getBean(JwtProperties.class);
    }
    private static UserService getUserService() {
        return applicationContext.getBean(UserService.class);
    }
    @OnOpen
    public void onOpen(Session session) {
        try {
            // 从URL参数获取token和roomId
            Map<String, List<String>> params = session.getRequestParameterMap();
            String token = params.get("token").get(0);
            Long roomId = Long.valueOf(params.get("roomId").get(0));

            // 解析token获取userId
            Long userId = getUserIdFromToken(token);

            // 将userId存储到session属性中，方便后续使用
            session.getUserProperties().put("userId", userId);
            session.getUserProperties().put("roomId", roomId);

            // 将session加入房间
            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

            System.out.println("用户 " + userId + " 进入房间 " + roomId);

        } catch (Exception e) {
            System.err.println("WebSocket连接失败: " + e.getMessage());
            try {
                session.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("收到消息: {}", message);
            MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
            Long roomId = messageDto.getRoomId();
            String content = messageDto.getContent();
            String type = messageDto.getType();
            String fileName = messageDto.getFileName();
            String fileUrl = messageDto.getFileUrl();
            Long fileSize = messageDto.getFileSize();

            // 从session属性获取userId
            Long userId = getUserIdFromSession(session);

            Mesaage11 mesaage11 = Mesaage11.builder()
                    .content(content)
                    .roomId(roomId)
                    .userId(userId)
                    .createTime(java.time.LocalDateTime.now())
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .fileSize(fileSize)
                    .type(type)
                    .build();

            String resp = JSON.toJSONString(mesaage11);

            // 广播到房间所有用户
            Set<Session> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                for (Session s : sessions) {
                    if (s.isOpen()) {
                        try {
                            s.getBasicRemote().sendText(resp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("处理消息失败: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 获取用户信息
        Long userId = getUserIdFromSession(session);
        Long roomId = (Long) session.getUserProperties().get("roomId");

        // 从房间移除session
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            // 如果房间空了，可以清理房间
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        // 使用静态方法获取UserService
        if (userId != null) {
            UserService userService = getUserService();
            if (userService != null) {
                userService.removeUser(userId);
            } else {
                log.warn("无法获取UserService，跳过移除用户操作");
            }
        }
        System.out.println("用户 " + userId + " 离开房间 " + roomId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket错误: " + error.getMessage());
    }

    // 解析token获取userId
    private Long getUserIdFromToken(String token) {
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            return Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
        } catch (Exception e) {
            throw new RuntimeException("Token解析失败", e);
        }
    }

    // 从session属性获取userId
    private Long getUserIdFromSession(Session session) {
        return (Long) session.getUserProperties().get("userId");
    }


}
