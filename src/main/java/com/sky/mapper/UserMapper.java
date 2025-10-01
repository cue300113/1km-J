package com.sky.mapper;

import com.sky.dto.MessageDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.Message;
import com.sky.enti.RoomUser;
import com.sky.enti.User;
import com.sky.result.Result;
import com.sky.vo.MessageVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User getUserByname(String username);


    void insert(UserRLDto userrldto);

   /* @Insert("insert into `1kmdb`.message(user_id, room_id, content,create_time) values (#{userid}, #{roomId}, #{content},now())")
    void sendMessage(Long userid, Long roomId, String content);*/

    @Select("select * from `1kmdb`.message where room_id = #{roomId}")
    List<Message> getMessages(Long roomId);

    @Update("update `1kmdb`.user set password = #{password} where id = #{id}")
    void updateUser(User user);

    /**
     * 删除1天前的消息
     */
    @Delete("DELETE FROM `1kmdb`.message WHERE create_time < DATE_SUB(NOW(), INTERVAL 1 DAY)")
    void deleteOldMessages();


    @Insert("insert into `1kmdb`.room_user(room_id, user_id,join_time) values (#{roomId}, #{userId},now())")
    void setRoomUser(Long userId, Long roomId);

    @Select("select * from `1kmdb`.room_user where user_id = #{userId}")
    RoomUser getRoomUserId(Long userId);

    @Delete("delete from `1kmdb`.room_user where user_id = #{userId}")
    void deleteRoomUser(Long userId);

    @Options(useGeneratedKeys = true, keyProperty = "message.id")
    @Insert("insert into `1kmdb`.message(user_id, room_id, content,create_time,type,file_name,file_size,file_url) values (#{message.userId}, #{message.roomId}, #{message.content},now(),#{message.type},#{message.fileName},#{message.fileSize},#{message.fileUrl})")
    Long sendMessage(@Param("message")Message message);

    /*@Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into `1kmdb`.message(user_id, room_id, content,create_time,type,file_name,file_size,file_url) values (#{userid}, #{roomId}, #{content},now(),#{type},#{fileName},#{fileSize},#{fileUrl})")
    Long sendMessage(Long userid, Long roomId, String content, String type, String fileName, Long fileSize, String fileUrl);*/
}
