package com.app.lms.controller;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentUpdateRequest;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.EnrollmentService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrollmentController {
    final EnrollmentService enrollmentService;

    // Student tự đăng ký khóa học (secure endpoint)
    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<EnrollmentResponse> enrollCourse(
            @Valid @RequestBody EnrollmentCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        // Chỉ student mới được đăng ký khóa học
        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        // Set studentId từ token, không trust client
        request.setStudentId(currentUser.getUserId());

        log.info("Student {} ({}) is enrolling in course {}",
                currentUser.getFullName(), currentUser.getUsername(), request.getCourseId());

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setResult(enrollmentService.createEnrollment(request));
        return response;
    }

    @PostMapping("/createEnrollment")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<EnrollmentResponse> createEnrollment(
            @RequestBody EnrollmentCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        // Chỉ admin/lecturer mới dùng được API này
        if (currentUser.getUserType() != UserType.LECTURER &&
                (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin())) {
            throw new AppException(ErroCode.LECTURER_ONLY);
        }

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setResult(enrollmentService.createEnrollment(request));
        return response;
    }

    // Student xem các khóa học đã đăng ký
    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<EnrollmentResponse>> getMyEnrollments(
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setResult(enrollmentService.getEnrollmentsByStudentId(currentUserId));
        return response;
    }

    // Check if student is enrolled in a specific course
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Boolean> checkEnrollment(
            @PathVariable Long courseId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        boolean isEnrolled = enrollmentService.isStudentEnrolledInCourse(currentUserId, courseId);

        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setResult(isEnrolled);
        return response;
    }

    // Admin/Lecturer xem tất cả
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<List<EnrollmentResponse>> getAllEnrollment(@CurrentUser UserTokenInfo currentUser) {

        // Chỉ admin hoặc lecturer mới xem được tất cả
        if (currentUser.getUserType() != UserType.LECTURER &&
                (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin())) {
            throw new AppException(ErroCode.LECTURER_ONLY);
        }

        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setResult(enrollmentService.getAllEnrollments());
        return response;
    }

    @GetMapping("/{enrollmentId}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "returnObject.result.studentId == authentication.principal.userId")
    public ApiResponse<EnrollmentResponse> getEnrollmentById(
            @Valid @PathVariable("enrollmentId") Long enrollmentId,
            @CurrentUser UserTokenInfo currentUser) {

        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(enrollmentId);

        // Student chỉ xem được enrollment của mình
        if (currentUser.getUserType() == UserType.STUDENT) {
            if (!enrollment.getStudentId().equals(currentUser.getUserId())) {
                throw new AppException(ErroCode.ACCESS_DENIED);
            }
        }

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setResult(enrollment);
        return response;
    }

    @PutMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<EnrollmentResponse> updateEnrollmentById(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody EnrollmentUpdateRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setResult(enrollmentService.updateEnrollment(enrollmentId, request));
        return response;
    }

    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteEnrollmentById(
            @PathVariable("enrollmentId") Long enrollmentId,
            @CurrentUser UserTokenInfo currentUser) {

        // Chỉ admin mới được delete
        if (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin()) {
            throw new AppException(ErroCode.ADMIN_ONLY);
        }

        enrollmentService.deleteEnrollment(enrollmentId);

        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Enrollment deleted successfully");
        return response;
    }

}