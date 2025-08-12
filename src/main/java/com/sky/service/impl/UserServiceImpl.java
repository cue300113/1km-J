package com.sky.service.impl;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.Message;
import com.sky.enti.Room;
import com.sky.enti.User;
import com.sky.mapper.RoomMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoomMapper roomMapper;


    @Override
    public void register(UserRLDto userrldto) {
        userMapper.insert(userrldto);
    }

    @Override
    public User getUserByname(String username) {
        return userMapper.getUserByname(username);
    }

    @Override
    public RoomVo Inroom(RoomDto roomDto) {
        List<Room> allRooms = roomMapper.selectAllRooms();
        if(allRooms.isEmpty()){
            Room newRoom = new Room();
            newRoom.setCenterLat(roomDto.getLatitude());
            newRoom.setCenterLng(roomDto.getLongitude());
            newRoom.setCreateTime(LocalDateTime.now());
            roomMapper.insert(newRoom);
            RoomVo roomVo = RoomVo.builder().roomId(newRoom.getId()).build();
            return roomVo;
        }
        Room nearestRoom = null;
        double minDistance = Double.MAX_VALUE;
        for (Room room : allRooms) {
            double distance = getRoomPrice(roomDto.getLatitude(), roomDto.getLongitude(), room.getCenterLat(), room.getCenterLng());
            if (distance <= 1.0 && distance < minDistance) { // 1km以内且最近
                minDistance = distance;
                nearestRoom = room;
            }
        }
        if (nearestRoom != null) {
            RoomVo roomVo = RoomVo.builder().roomId(nearestRoom.getId()).build();
            return roomVo;
        } else {
            // 新建房间
            Room newRoom = new Room();
            newRoom.setCenterLat(roomDto.getLatitude());
            newRoom.setCenterLng(roomDto.getLongitude());
            newRoom.setCreateTime(LocalDateTime.now());
            roomMapper.insert(newRoom);
            RoomVo roomVo = RoomVo.builder().roomId(newRoom.getId()).build();
            return roomVo;
        }
    }

    @Override
    public void sendMessage(MessageDto messageDto) {
        Long userid = BaseContext.getCurrentId();
        userMapper.sendMessage(userid, messageDto.getRoomId(), messageDto.getContent());
    }

    @Override
    public List<MessageVo> getMessages(Long roomId) {
        List<Message> messages = userMapper.getMessages(roomId);
        List<MessageVo> messageVos = messages.stream().map(message -> {
            return MessageVo.builder()
                    .id(message.getId())
                    .content(message.getContent().toString())
                    .createTime(message.getCreateTime())
                    .build();
        }).collect(Collectors.toList());
        return messageVos;
    }


    public double getRoomPrice(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // 地球半径，单位：km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

}
