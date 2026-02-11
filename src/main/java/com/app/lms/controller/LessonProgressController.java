package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.lessonProgressRequest.LessonProgressRequest;
import com.app.lms.dto.response.CourseProgressResponse;
import com.app.lms.dto.response.LessonProgressResponse;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.LessonProgressService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonProgressController {
    LessonProgressService lessonProgressService;

    @PutMapping("/save")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<LessonProgressResponse> saveProgress(
            @Valid @RequestBody LessonProgressRequest request,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<LessonProgressResponse> response = new ApiResponse<>();
        response.setResult(lessonProgressService.saveProgress(currentUserId, request));
        return response;
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<LessonProgressResponse> getLessonProgress(
            @PathVariable Long lessonId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<LessonProgressResponse> response = new ApiResponse<>();
        response.setResult(lessonProgressService.getProgressByLesson(currentUserId, lessonId));
        return response;
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<CourseProgressResponse> getCourseProgress(
            @PathVariable Long courseId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<CourseProgressResponse> response = new ApiResponse<>();
        response.setResult(lessonProgressService.getCourseProgress(currentUserId, courseId));
        return response;
    }
}
