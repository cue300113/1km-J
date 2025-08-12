package com.sky.mapper;

import com.sky.dto.MessageDto;
import com.sky.dto.UserRLDto;
import com.sky.enti.Message;
import com.sky.enti.User;
import com.sky.result.Result;
import com.sky.vo.MessageVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User getUserByname(String username);


    void insert(UserRLDto userrldto);

    @Insert("insert into `1kmdb`.message(user_id, room_id, content,create_time) values (#{userid}, #{roomId}, #{content},now())")
    void sendMessage(Long userid, Long roomId, String content);

    @Select("select * from `1kmdb`.message where room_id = #{roomId}")
    List<Message> getMessages(Long roomId);
}
