package com.app.lms.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private final Path fileStorageLocation;

    public FileController() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        System.out.println("FileController initialized. Serving files from: " + this.fileStorageLocation);
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        try {
            // Lấy request URI: /uploads/courses/xyz.jpg
            String requestUri = request.getRequestURI();

            // Cắt bỏ phần /uploads/ để lấy relative path: courses/xyz.jpg
            // Tuy nhiên, context path có thể thay đổi, nên lấy substring sau "/uploads/"
            int index = requestUri.indexOf("/uploads/");
            if (index == -1) {
                return ResponseEntity.badRequest().build();
            }

            String relativePath = requestUri.substring(index + 9); // length of "/uploads/" is 9

            // Fix double uploads issue (e.g. if path is uploads/uploads/courses/...)
            if (relativePath.startsWith("uploads/") || relativePath.startsWith("uploads\\")) {
                relativePath = relativePath.substring(8); // remove "uploads/"
            }

            // Resolve file
            Path filePath = fileStorageLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Xác định content type
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                System.out.println("File not found or not readable: " + filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
