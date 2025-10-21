package com.app.lms.controller;

import java.util.List;

import com.app.lms.dto.request.ApprovalRequest.ApprovalRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CourseController {
    final CourseService courseService;

    @PostMapping(value = "/createCourse", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CourseResponse> createCourse(
            @Valid @RequestPart("course") CourseCreateRequest request,
            @RequestPart("file") MultipartFile file,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() == UserType.LECTURER) {
            request.setTeacherId(currentUser.getUserId());
        }

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        CourseResponse course = courseService.createCourse(request, file);
        apiResponse.setResult(course);
        apiResponse.setMessage("Khóa học đã được tạo và đang chờ phê duyệt từ Admin");
        return apiResponse;
    }

    @GetMapping("/{courseId}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.isStudentEnrolledInCourse(#courseId, authentication.name)")
    public ApiResponse<CourseResponse> getCourse(@Valid @PathVariable("courseId") Long courseId) {
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getCourseById(courseId));
        return apiResponse;
    }

    @GetMapping
    // Public - chỉ hiển thị khóa học đã APPROVED
    public ApiResponse<List<CourseResponse>> getAllCourse() {
        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getAllCourses());
        return apiResponse;
    }

    // ADMIN XEM TẤT CẢ KHÓA HỌC (BAO GỒM PENDING, REJECTED)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CourseResponse>> getAllCoursesForAdmin() {
        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getAllCoursesForAdmin());
        return apiResponse;
    }

    // ADMIN XEM DANH SÁCH KHÓA HỌC CHỜ PHÊ DUYỆT
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CourseResponse>> getPendingCourses() {
        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getPendingCourses());
        return apiResponse;
    }

    // LECTURER XEM KHÓA HỌC CỦA MÌNH (BAO GỒM PENDING)
    @GetMapping("/lecturer/my-courses")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<List<CourseResponse>> getCoursesByLecturer(
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.LECTURER) {
            throw new AppException(ErroCode.LECTURER_ONLY);
        }

        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getCoursesByLecturer(currentUserId));
        return apiResponse;
    }

    // ADMIN PHÊ DUYỆT/TỪ CHỐI KHÓA HỌC
    @PostMapping("/{courseId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CourseResponse> approveCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody ApprovalRequest request,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        // Double check admin role
        if (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin()) {
            throw new AppException(ErroCode.ADMIN_ONLY);
        }

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        CourseResponse course = courseService.approveCourse(courseId, request, currentUserId);
        apiResponse.setResult(course);

        String message = switch (request.getApprovalStatus()) {
            case APPROVED -> "Khóa học đã được phê duyệt thành công";
            case REJECTED -> "Khóa học đã bị từ chối";
            default -> "Trạng thái khóa học đã được cập nhật";
        };
        apiResponse.setMessage(message);

        return apiResponse;
    }

    @PutMapping(value = "/updateCourse/{courseId}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('LECTURER') and @authorizationService.isLecturerOwnsCourse(#courseId, authentication.name))")
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestPart("course") CourseUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @CurrentUser UserTokenInfo currentUser) {

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        CourseResponse course = courseService.updateCourse(courseId, request, file);
        apiResponse.setResult(course);

        // Nếu lecturer sửa khóa học đã approved → chuyển về pending
        if (currentUser.getUserType() == UserType.LECTURER) {
            apiResponse.setMessage("Khóa học đã được cập nhật và đang chờ phê duyệt lại");
        } else {
            apiResponse.setMessage("Khóa học đã được cập nhật thành công");
        }

        return apiResponse;
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.<String>builder()
                .result("Course deleted successfully")
                .build();
    }
}