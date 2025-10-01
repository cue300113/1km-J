package com.sky.service;

import com.sky.dto.EmailDto;
import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.Message;
import com.sky.enti.User;
import com.sky.result.Result;
import com.sky.vo.FileVo;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void register(UserRLDto userrldto);

    User getUserByname(String username);

    RoomVo Inroom(RoomDto roomDto);

    void sendMessage(Long userid,MessageDto messageDto);

    List<Message>getMessages(Long roomId);

    // 更新用户信息
    void updateUser(User user);

    FileVo uploadFile(MultipartFile file, Long roomId,Long userId);

    void setRoomUser(Long UserId, Long roomId);

    void removeUser(Long userId);
}
