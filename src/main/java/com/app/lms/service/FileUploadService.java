package com.app.lms.service;

import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileUploadService {
    private final String COURSE_UPLOAD_DIR = "uploads/courses/";
    private final String LESSON_UPLOAD_DIR = "uploads/lessons/videos/";

    public String saveCourseFile(MultipartFile file) throws IOException {
        return saveFile(file, COURSE_UPLOAD_DIR);
    }

    public String saveLessonVideo(MultipartFile file) throws IOException {
        return saveFile(file, LESSON_UPLOAD_DIR);
    }

    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new AppException(ErroCode.FILE_EXISTED);
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uploadDir + fileName; // path lưu trong DB
    }
}


