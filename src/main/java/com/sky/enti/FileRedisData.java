package com.sky.enti;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class FileRedisData {
    private String fileId;           // 文件唯一ID
    private String fileName;         // 原始文件名
    private String fileUrl;          // OSS文件URL
    private Long fileSize;           // 文件大小
    private String fileType;         // 文件类型
    private Long roomId;           // 房间ID
    private Long userId;             // 用户ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime; // 上传时间
}
