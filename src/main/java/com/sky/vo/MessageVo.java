package com.sky.vo;


import com.sky.enti.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createTime;


}
