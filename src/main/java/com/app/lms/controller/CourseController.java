package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.service.CourseService;
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
@RequestMapping("/api/course")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseController {
    final CourseService courseService;

    @PostMapping(value = "/createCourse", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CourseResponse> createCourse(
            @Valid @RequestPart("course") CourseCreateRequest request,
            @RequestPart("file") MultipartFile file,
            @CurrentUser UserTokenInfo currentUser) {

        // Set teacherId từ current user nếu là LECTURER
        if (currentUser.getUserType().toString().equals("LECTURER")) {
            request.setTeacherId(currentUser.getUserId());
        }

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.createCourse(request, file));
        return apiResponse;
    }

    @GetMapping("/{courseId}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.isStudentEnrolledInCourse(#courseId, authentication.name)")
    public ApiResponse<CourseResponse> getCourse(@Valid @PathVariable("courseId") Long courseId) {
        // Student chỉ xem được course đã enroll, Lecturer/Admin xem tất cả
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getCourseById(courseId));
        return apiResponse;
    }

    @GetMapping
    // Public - tất cả user đều xem được danh sách course (để browse)
    public ApiResponse<List<CourseResponse>> getAllCourse() {
        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getAllCourses());
        return apiResponse;
    }

    @PutMapping("/updateCourse/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('LECTURER') and @authorizationService.isLecturerOwnsCourse(#courseId, authentication.name))")
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        // Lecturer chỉ edit course của mình, Admin edit tất cả
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.updateCourse(courseId, request));
        return apiResponse;
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCourse(@PathVariable Long courseId) {
        // Chỉ Admin mới xóa course
        courseService.deleteCourse(courseId);
        return ApiResponse.<String>builder()
                .result("Course deleted successfully")
                .build();
    }

}