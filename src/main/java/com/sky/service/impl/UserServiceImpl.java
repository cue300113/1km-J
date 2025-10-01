package com.sky.service.impl;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmailDto;
import com.sky.dto.MessageDto;
import com.sky.dto.RoomDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.FileRedisData;
import com.sky.enti.Message;
import com.sky.enti.Room;
import com.sky.enti.User;
import com.sky.mapper.RoomMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.RedisService;
import com.sky.service.UserService;
import com.sky.utils.AliOssUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.FileVo;
import com.sky.vo.MessageVo;
import com.sky.vo.RoomVo;
import com.sky.vo.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private FileRedisData fileRedisData;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


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
    public void sendMessage(Long userid,MessageDto messageDto) {
        /*Long id=userMapper.sendMessage(userid, messageDto.getRoomId(), messageDto.getContent(),messageDto.getType(),messageDto.getFileName(),messageDto.getFileSize(),messageDto.getFileUrl());*/
        Message messagex = new Message();
        Message message = messagex.builder().roomId(messageDto.getRoomId()).userId(userid).content(messageDto.getContent()).createTime(LocalDateTime.now())
                .type(messageDto.getType()).fileName(messageDto.getFileName()).fileSize(messageDto.getFileSize()).fileUrl(messageDto.getFileUrl()).build();
        userMapper.sendMessage(message);
        String key = "message:room:" + message.getRoomId();
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    @Override
    public List<Message> getMessages(Long roomId) {
        /*List<Message> messages = userMapper.getMessages(roomId);*/
        String key = "message:room:"+roomId;
        List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
        List<Message> messages = list.stream()
                .map(obj -> (Message) obj)
                .collect(Collectors.toList());

        /*List<MessageVo> messageVos = messages.stream().map(message -> {
            try {
                return MessageVo.builder()
                        .id(message.getId())
                        .userId(message.getUserId())
                        .content(message.getContent())
                        .createTime(message.getCreateTime())
                        .build();
            } catch (Exception e) {
                // 处理异常情况，返回空内容的消息
                return MessageVo.builder()
                        .id(message.getId())
                        .userId(message.getUserId())
                        .content("")
                        .createTime(message.getCreateTime())
                        .build();
            }
        }).collect(Collectors.toList());
        log.info("消息列表:{}", messageVos);*/
        log.info("消息列表:{}", messages);
        return messages;
    }
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public FileVo uploadFile(MultipartFile file, Long roomId,Long userId) {


        //原始文件名
        String originalFilename = file.getOriginalFilename();

        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成文件名
        String objectName= UUID.randomUUID().toString()+substring;
        try {
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            FileVo fileVo = new FileVo(url,objectName,file.getSize(),file.getContentType());
            fileRedisData
                    .builder()
                    .fileId(UUID.randomUUID().toString())
                    .fileName(originalFilename)
                    .fileUrl(url)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .roomId(roomId)
                    .userId(userId)
                    .uploadTime(LocalDateTime.now());
            redisService.set(fileVo.getFileName(),fileRedisData);
            return fileVo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRoomUser(Long userId, Long roomId) {
        if(userMapper.getRoomUserId(userId)==null)
        {
            userMapper.setRoomUser(userId, roomId);
        }


    }

    @Override
    public void removeUser(Long userId) {
        userMapper.deleteRoomUser(userId);
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
