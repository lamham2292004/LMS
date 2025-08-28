package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentUpdateRequest;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.entity.Enrollment;
import com.app.lms.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentController {
    final EnrollmentService enrollmentService;

    @PostMapping("/createEnrollment")
    ApiResponse<EnrollmentResponse> createEnrollment(@RequestBody EnrollmentCreateRequest request) {
        ApiResponse<EnrollmentResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(enrollmentService.createEnrollment(request));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<EnrollmentResponse>> getAllEnrollment() {
        ApiResponse<List<EnrollmentResponse>> apiResponse = new ApiResponse<>();

        apiResponse.setResult(enrollmentService.getAllEnrollments());

        return apiResponse;
    }

    @GetMapping("/{enrollmentId}")
    ApiResponse<EnrollmentResponse> getEnrollmentById(@Valid @PathVariable("enrollmentId") Long enrollmentId) {
        ApiResponse<EnrollmentResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(enrollmentService.getEnrollmentById(enrollmentId));

        return apiResponse;
    }

    @PutMapping("/{enrollmentId}")
    ApiResponse<EnrollmentResponse> updateEnrollmentById(@PathVariable Long enrollmentId,@Valid @RequestBody EnrollmentUpdateRequest request) {
        ApiResponse<EnrollmentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(enrollmentService.updateEnrollment(enrollmentId, request));
        return apiResponse;
    }
    @DeleteMapping("/{enrollmentId}")
    String deleteEnrollmentById(@PathVariable("enrollmentId") Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return "Enrollment deleted";
    }

}
