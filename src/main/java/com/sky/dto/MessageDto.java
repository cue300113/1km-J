package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long roomId;
    private String content;
    private String type;        // 消息类型 (text/image/video)
    private String fileUrl;     // 文件URL
    private String fileName;    // 文件名
    private Long fileSize;      // 文件大小
}
