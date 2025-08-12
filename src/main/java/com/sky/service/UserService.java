package com.sky.service;

import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.User;
import com.sky.result.Result;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;

import java.util.List;

public interface UserService {
    void register(UserRLDto userrldto);
    User getUserByname(String username);
    RoomVo Inroom(RoomDto roomDto);

    void sendMessage(MessageDto messageDto);

    List<MessageVo>getMessages(Long roomId);
}
