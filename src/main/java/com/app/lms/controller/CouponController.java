package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.couponRequest.CouponCreateRequest;
import com.app.lms.dto.request.couponRequest.CouponUpdateRequest;
import com.app.lms.dto.request.couponRequest.CouponValidationRequest;
import com.app.lms.dto.response.CouponResponse;
import com.app.lms.dto.response.CouponValidationResponse;
import com.app.lms.service.CouponService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CouponController {
    CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> createCoupon(
            @Valid @RequestBody CouponCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {
        log.info("Admin {} creating coupon: {}", currentUser.getUserId(), request.getCode());

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.createCoupon(request, currentUser.getUserId()));
        return apiResponse;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>> getAllCoupons() {
        ApiResponse<List<CouponResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.getAllCoupons());
        return apiResponse;
    }

    @GetMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> getCouponById(@PathVariable Long couponId) {
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.getCouponById(couponId));
        return apiResponse;
    }

    @PutMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody CouponUpdateRequest request) {

        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.updateCoupon(couponId, request));
        return apiResponse;
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ApiResponse.<String>builder()
                .result("Xóa mã giảm giá thành công")
                .build();
    }

    // ==================== STUDENT ENDPOINT ====================

    /**
     * Validate coupon cho khóa học (Student)
     * Trả về thông tin giảm giá nếu coupon hợp lệ
     */
    @PostMapping("/validate")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ApiResponse<CouponValidationResponse> validateCoupon(
            @Valid @RequestBody CouponValidationRequest request) {
        log.info("Validating coupon: {} for course: {}", request.getCouponCode(), request.getCourseId());

        ApiResponse<CouponValidationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.validateCouponForCourse(request.getCouponCode(), request.getCourseId()));
        return apiResponse;
    }

    @GetMapping("/available/{courseId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>> getAvailableCouponsForCourse(@PathVariable Long courseId) {
        log.info("Getting available coupons for course: {}", courseId);
        ApiResponse<List<CouponResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.getAvailableCouponsForCourse(courseId));
        return apiResponse;
    }
}
