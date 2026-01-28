package com.app.lms.service;

import com.app.lms.dto.request.notificationRequest.NotificationPayload;
import com.app.lms.dto.request.notificationRequest.NotificationRequest;
import com.app.lms.dto.response.UserInfoResponse;
import com.app.lms.entity.Course;
import com.app.lms.service.client.IdentityClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class NotificationService {
    
    final WebClient webClient;
    final IdentityClient identityClient;
    
    @Value("${notification.course.review.base-url:http://localhost:3000/admin/courses/approve}")
    String courseReviewBaseUrl;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public NotificationService(
            @Value("${notification.service.url}") String notificationServiceUrl,
            IdentityClient identityClient
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
        this.identityClient = identityClient;
    }
    
    /**
     * Gửi thông báo khi giảng viên tạo khóa học mới
     */
    @Async
    public void sendCourseCreatedNotification(Course course, String lecturerName) {
        try {
            log.info("=== STARTING sendCourseCreatedNotification ===");
            log.info("Course ID: {}, Title: {}, Lecturer: {}", course.getId(), course.getTitle(), lecturerName);
            
            NotificationPayload payload = NotificationPayload.builder()
                    .admin_id(1L) // Có thể lấy dynamic từ config hoặc database
                    .admin_type("lecturer")
                    .lecturer_id(course.getTeacherId())
                    .lecturer_name(lecturerName)
                    .title(course.getTitle())
                    .course_review_url(courseReviewBaseUrl + "/" + course.getId())
                    .date(OffsetDateTime.now().format(DATE_FORMATTER))
                    .build();
            
            NotificationRequest request = NotificationRequest.builder()
                    .topic("lecturer.create.course")
                    .payload(payload)
                    .priority("medium")
                    .key("course_created_" + course.getId())
                    .build();
            
            log.info("Sending notification to System-Management...");
            log.info("Request: {}", request);
            
            // Gửi request với WebClient
            String response = webClient.post()
                    .uri("/api/v1/events/publish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("Response from System-Management: {}", res))
                    .doOnError(error -> log.error("Error from System-Management: {}", error.getMessage()))
                    .onErrorResume(error -> {
                        log.error("Failed to connect to System-Management", error);
                        return Mono.empty();
                    })
                    .block(); // Block để chờ response (vì đã có @Async rồi)
            
            log.info("✅ Successfully sent course creation notification for course: {} to System-Management", course.getTitle());
            
        } catch (Exception e) {
            log.error("Failed to send course creation notification for course: {}", course.getTitle(), e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }
    
    /**
     * Gửi thông báo khi admin phê duyệt khóa học
     */
    @Async
    public void sendCourseApprovedNotification(Course course, Long adminId) {
        try {
            // Lấy thông tin lecturer từ Identity Service
            String lecturerName = getLecturerName(course.getTeacherId());
            
            NotificationPayload payload = NotificationPayload.builder()
                    .admin_id(adminId)
                    .admin_type("admin")
                    .lecturer_id(course.getTeacherId())
                    .lecturer_name(lecturerName)
                    .title(course.getTitle())
                    .course_review_url(courseReviewBaseUrl + "/" + course.getId())
                    .date(course.getApprovedAt() != null ? 
                          course.getApprovedAt().format(DATE_FORMATTER) : 
                          OffsetDateTime.now().format(DATE_FORMATTER))
                    .build();
            
            NotificationRequest request = NotificationRequest.builder()
                    .topic("admin.approve.course")
                    .payload(payload)
                    .priority("high")
                    .key("course_approved_" + course.getId())
                    .build();
            
            // Gửi request với WebClient
            webClient.post()
                    .uri("/api/v1/events/publish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("✅ Sent course approval notification: {}", res))
                    .onErrorResume(error -> {
                        log.error("Failed to send approval notification", error);
                        return Mono.empty();
                    })
                    .block();
            
        } catch (Exception e) {
            log.error("Failed to send course approval notification for course: {}", course.getTitle(), e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }
    
    /**
     * Gửi thông báo khi admin từ chối khóa học
     */
    @Async
    public void sendCourseRejectedNotification(Course course, Long adminId) {
        try {
            // Lấy thông tin lecturer từ Identity Service
            String lecturerName = getLecturerName(course.getTeacherId());
            
            NotificationPayload payload = NotificationPayload.builder()
                    .admin_id(adminId)
                    .admin_type("admin")
                    .lecturer_id(course.getTeacherId())
                    .lecturer_name(lecturerName)
                    .title(course.getTitle())
                    .course_review_url(courseReviewBaseUrl + "/" + course.getId())
                    .date(course.getApprovedAt() != null ? 
                          course.getApprovedAt().format(DATE_FORMATTER) : 
                          OffsetDateTime.now().format(DATE_FORMATTER))
                    .build();
            
            NotificationRequest request = NotificationRequest.builder()
                    .topic("admin.reject.course")
                    .payload(payload)
                    .priority("high")
                    .key("course_rejected_" + course.getId())
                    .build();
            
            // Gửi request với WebClient
            webClient.post()
                    .uri("/api/v1/events/publish")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("✅ Sent course rejection notification: {}", res))
                    .onErrorResume(error -> {
                        log.error("Failed to send rejection notification", error);
                        return Mono.empty();
                    })
                    .block();
            
        } catch (Exception e) {
            log.error("Failed to send course rejection notification for course: {}", course.getTitle(), e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }
    }
    
    /**
     * Lấy lecturer name từ Identity Service
     */
    private String getLecturerName(Long lecturerId) {
        try {
            UserInfoResponse lecturerInfo = identityClient.getLecturerInfo(lecturerId);
            return lecturerInfo.getFullName();
        } catch (Exception e) {
            log.warn("Failed to fetch lecturer info for ID: {}, using default", lecturerId);
            return "Unknown Lecturer";
        }
    }
}

