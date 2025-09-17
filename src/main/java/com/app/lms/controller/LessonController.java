package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.lessonRequest.LessonCreateRequest;
import com.app.lms.dto.request.lessonRequest.LessonUpdateRequest;
import com.app.lms.dto.response.LessonResponse;
import com.app.lms.service.LessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonController {
    LessonService lessonService;

    @PostMapping(value = "/createLesson", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('LECTURER') and @authorizationService.canLecturerCreateLessonInCourse(#request.courseId, authentication.name))")
    public ApiResponse<LessonResponse> createLesson(
            @RequestPart("lesson") @Valid LessonCreateRequest request,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @CurrentUser UserTokenInfo currentUser) {

        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.createLesson(request, video));
        return apiResponse;
    }

    @GetMapping("/{lessonId}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.canStudentAccessLesson(#lessonId, authentication.name)")
    public ApiResponse<LessonResponse> getLesson(@Valid @PathVariable("lessonId") Long lessonId) {
        // Student chỉ xem được lesson của course đã enroll
        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.getLessonById(lessonId));
        return apiResponse;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<List<LessonResponse>> getAllLesson() {
        // Chỉ Admin/Lecturer mới xem tất cả lessons
        ApiResponse<List<LessonResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.getAllLessons());
        return apiResponse;
    }

    @PutMapping("/updateLesson/{lessonId}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('LECTURER') and @authorizationService.canLecturerEditLesson(#lessonId, authentication.name))")
    public ApiResponse<LessonResponse> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonUpdateRequest request) {

        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.updateLesson(lessonId, request));
        return apiResponse;
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ApiResponse.<String>builder()
                .result("Lesson deleted successfully")
                .build();
    }
}
