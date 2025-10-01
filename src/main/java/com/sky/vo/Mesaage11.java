package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mesaage11 {
    private Long roomId;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private String type;        // 消息类型 (text/image/video)
    private String fileUrl;     // 文件URL
    private String fileName;    // 文件名
    private Long fileSize;      // 文件大小
}
