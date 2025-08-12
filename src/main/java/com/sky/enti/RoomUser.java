package com.sky.enti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUser {
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime joinTime;
}
