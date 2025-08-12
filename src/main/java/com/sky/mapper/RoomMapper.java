package com.sky.mapper;

import com.sky.enti.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoomMapper {

    @Select("SELECT * FROM room")
    List<Room> selectAllRooms();

    void insert(Room newRoom);
}
