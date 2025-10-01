package com.sky.service;

import com.sky.dto.UserRLDto;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileInfo uploadFile(MultipartFile file, Long roomId,Long userId);
}
