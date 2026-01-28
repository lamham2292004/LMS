package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.config.VNPayConfig;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.paymentRequest.PaymentCreateRequest;
import com.app.lms.dto.response.PaymentResponse;
import com.app.lms.entity.Payment;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.PaymentService;
import com.app.lms.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentController {

    PaymentService paymentService;
    VNPayService vnPayService;
    VNPayConfig vnPayConfig;

    /**
     * Tạo payment và nhận VNPay URL
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            @CurrentUser UserTokenInfo currentUser,
            HttpServletRequest httpRequest) {
        log.info("Payment create request from student: {}", currentUser.getUserId());

        // Verify student role
        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        PaymentResponse paymentResponse = paymentService.createPayment(
                request,
                currentUser.getUserId(),
                currentUser.getEmail(),
                currentUser.getFullName(),
                httpRequest);

        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(paymentResponse);
        return apiResponse;
    }

    /**
     * VNPay return URL - Callback sau khi thanh toán
     * Redirect về frontend với kết quả
     */
    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> allParams) {
        log.info("VNPay return callback received");

        try {
            // Xử lý callback
            Payment payment = paymentService.handleVNPayCallback(allParams);

            String frontendUrl = vnPayConfig.getFrontendUrl();
            String redirectUrl;

            if (payment.getStatus().name().equals("SUCCESS")) {
                // Thanh toán thành công - redirect to success page
                redirectUrl = String.format("%s/payment/success?txnRef=%s&courseId=%d",
                        frontendUrl,
                        URLEncoder.encode(payment.getVnpayTxnRef(), StandardCharsets.UTF_8),
                        payment.getCourse().getId());
            } else {
                // Thanh toán thất bại - redirect to fail page
                String message = vnPayService.getResponseMessage(payment.getVnpayResponseCode());
                redirectUrl = String.format("%s/payment/failed?txnRef=%s&message=%s",
                        frontendUrl,
                        URLEncoder.encode(payment.getVnpayTxnRef(), StandardCharsets.UTF_8),
                        URLEncoder.encode(message, StandardCharsets.UTF_8));
            }

            log.info("Redirecting to: {}", redirectUrl);
            return new RedirectView(redirectUrl);

        } catch (Exception e) {
            log.error("Error processing VNPay callback", e);
            String errorUrl = String.format("%s/payment/error?message=%s",
                    vnPayConfig.getFrontendUrl(),
                    URLEncoder.encode("Lỗi xử lý thanh toán", StandardCharsets.UTF_8));
            return new RedirectView(errorUrl);
        }
    }

    /**
     * VNPay IPN - Server-to-server notification
     * VNPay sẽ gọi endpoint này để notify kết quả thanh toán
     */
    @PostMapping("/vnpay-ipn")
    public Map<String, String> vnpayIPN(@RequestParam Map<String, String> allParams) {
        log.info("VNPay IPN notification received");

        Map<String, String> response = new HashMap<>();

        try {
            Payment payment = paymentService.handleVNPayCallback(allParams);

            // VNPay yêu cầu response format cụ thể
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

            log.info("IPN processed successfully for payment: {}", payment.getId());

        } catch (Exception e) {
            log.error("Error processing VNPay IPN", e);
            response.put("RspCode", "99");
            response.put("Message", "Confirm Fail");
        }

        return response;
    }

    /**
     * Lấy lịch sử thanh toán của student
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Payment>> getPaymentHistory(@CurrentUser UserTokenInfo currentUser) {
        log.info("Get payment history for student: {}", currentUser.getUserId());

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        List<Payment> payments = paymentService.getPaymentHistory(currentUser.getUserId());

        ApiResponse<List<Payment>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(payments);
        return apiResponse;
    }

    /**
     * Get payment by ID (Student hoặc Admin)
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ApiResponse<Payment> getPaymentById(
            @PathVariable Long paymentId,
            @CurrentUser UserTokenInfo currentUser) {
        log.info("Get payment by ID: {}", paymentId);

        Payment payment = paymentService.getPaymentById(paymentId);

        // Student chỉ xem được payment của mình
        if (currentUser.getUserType() == UserType.STUDENT) {
            if (!payment.getStudentId().equals(currentUser.getUserId())) {
                throw new AppException(ErroCode.ACCESS_DENIED);
            }
        }

        ApiResponse<Payment> apiResponse = new ApiResponse<>();
        apiResponse.setResult(payment);
        return apiResponse;
    }
}
