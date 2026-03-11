package com.app.lms.service;

import com.app.lms.config.VNPayConfig;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.paymentRequest.PaymentCreateRequest;
import com.app.lms.dto.response.CouponValidationResponse;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.dto.response.PaymentResponse;
import com.app.lms.entity.Coupon;
import com.app.lms.entity.Course;
import com.app.lms.entity.Payment;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.enums.PaymentStatus;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.repository.CourseRepository;
import com.app.lms.repository.PaymentRepository;
import com.app.lms.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentService {

    PaymentRepository paymentRepository;
    CourseRepository courseRepository;
    CouponService couponService;
    VNPayService vnPayService;
    EnrollmentService enrollmentService;
    VNPayConfig vnPayConfig;

    /**
     * Tạo payment và generate VNPay URL
     */
    @Transactional
    public PaymentResponse createPayment(
            PaymentCreateRequest request,
            Long studentId,
            String studentEmail,
            String studentName,
            HttpServletRequest httpRequest) {
        log.info("Creating payment for student: {}, course: {}", studentId, request.getCourseId());

        // 1. Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

        BigDecimal originalPrice = course.getPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalPrice = originalPrice;
        Coupon coupon = null;

        // 2. Validate và apply coupon nếu có
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            log.info("Validating coupon: {}", request.getCouponCode());
            CouponValidationResponse validation = couponService.validateCoupon(
                    request.getCouponCode(), course, originalPrice);

            if (!validation.getValid()) {
                throw new AppException(ErroCode.COUPON_INVALID);
            }

            discountAmount = validation.getDiscountAmount();
            finalPrice = validation.getFinalPrice();
            coupon = couponService.getCouponByCode(request.getCouponCode());
        }

        // 3. Tạo payment record
        String vnpayTxnRef = VNPayUtil.getRandomNumber(8);
        String ipAddress = VNPayUtil.getIpAddress(httpRequest);
        String orderInfo = String.format("Thanh toan khoa hoc: %s", course.getTitle());

        Payment payment = Payment.builder()
                .studentId(studentId)
                .studentEmail(studentEmail)
                .studentName(studentName)
                .course(course)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .vnpayTxnRef(vnpayTxnRef)
                .status(PaymentStatus.PENDING)
                .coupon(coupon)
                .ipAddress(ipAddress)
                .orderInfo(orderInfo)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}, TxnRef: {}", payment.getId(), vnpayTxnRef);

        // 4. Generate VNPay payment URL
        String paymentUrl;
        try {
            paymentUrl = vnPayService.createPaymentUrl(payment, httpRequest);
        } catch (Exception e) {
            log.error("Error creating VNPay payment URL", e);
            throw new AppException(ErroCode.PAYMENT_CREATE_ERROR);
        }

        // 5. Return response
        return PaymentResponse.builder()
                .id(payment.getId())
                .vnpayTxnRef(vnpayTxnRef)
                .studentId(studentId)
                .studentEmail(studentEmail)
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .couponCode(request.getCouponCode())
                .status(PaymentStatus.PENDING)
                .paymentUrl(paymentUrl)
                .createdAt(payment.getCreatedAt())
                .build();
    }

    /**
     * Xử lý callback từ VNPay sau khi thanh toán
     */
    @Transactional
    public Payment handleVNPayCallback(Map<String, String> vnpayParams) {
        log.info("Processing VNPay callback");

        // 1. Verify signature
        boolean isValid = vnPayService.verifyPaymentCallback(vnpayParams);
        if (!isValid) {
            log.error("Invalid VNPay signature!");
            throw new AppException(ErroCode.PAYMENT_INVALID_SIGNATURE);
        }

        // 2. Lấy thông tin từ params
        String vnpayTxnRef = vnpayParams.get("vnp_TxnRef");
        String vnpayTransactionNo = vnpayParams.get("vnp_TransactionNo");
        String vnpayResponseCode = vnpayParams.get("vnp_ResponseCode");
        String vnpayBankCode = vnpayParams.get("vnp_BankCode");
        String vnpayBankTranNo = vnpayParams.get("vnp_BankTranNo");
        String vnpayCardType = vnpayParams.get("vnp_CardType");
        String vnpayPayDate = vnpayParams.get("vnp_PayDate");
        String transactionStatus = vnpayParams.get("vnp_TransactionStatus");

        log.info("VNPay callback - TxnRef: {}, ResponseCode: {}, TransactionStatus: {}",
                vnpayTxnRef, vnpayResponseCode, transactionStatus);

        // 3. Tìm payment
        Payment payment = paymentRepository.findByVnpayTxnRef(vnpayTxnRef)
                .orElseThrow(() -> new AppException(ErroCode.PAYMENT_NOT_FOUND));

        // 4. Check if already processed (idempotency)
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Payment {} already processed with status: {}", payment.getId(), payment.getStatus());
            return payment; // Đã xử lý rồi, return luôn
        }

        // 5. Update payment info
        payment.setVnpayTransactionNo(vnpayTransactionNo);
        payment.setVnpayResponseCode(vnpayResponseCode);
        payment.setVnpayBankCode(vnpayBankCode);
        payment.setVnpayBankTranNo(vnpayBankTranNo);
        payment.setVnpayCardType(vnpayCardType);
        payment.setVnpayPayDate(vnpayPayDate);

        // 6. Xử lý dựa trên transaction status
        if ("00".equals(transactionStatus) && "00".equals(vnpayResponseCode)) {
            // Thanh toán thành công
            log.info("Payment successful! Processing enrollment...");
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(OffsetDateTime.now());

            // Tạo enrollment
            EnrollmentResponse enrollment = createEnrollmentFromPayment(payment);
            payment.setEnrollmentId(enrollment.getId());

            // Record coupon usage nếu có
            if (payment.getCoupon() != null) {
                couponService.recordUsage(
                        payment.getCoupon(),
                        payment,
                        payment.getStudentId(),
                        payment.getDiscountAmount());
            }

            log.info("Payment processed successfully. Enrollment ID: {}", enrollment.getId());
        } else {
            // Thanh toán thất bại
            log.warn("Payment failed. ResponseCode: {}", vnpayResponseCode);
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    /**
     * Tạo enrollment sau khi thanh toán thành công
     */
    private EnrollmentResponse createEnrollmentFromPayment(Payment payment) {
        EnrollmentCreateRequest enrollmentRequest = EnrollmentCreateRequest.builder()
                .studentId(payment.getStudentId())
                .studentName(payment.getStudentName())
                .studentEmail(payment.getStudentEmail())
                .courseId(payment.getCourse().getId())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        return enrollmentService.createEnrollment(enrollmentRequest);
    }

    /**
     * Lấy tất cả payment (ADMIN only)
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Lấy payment history của student
     */
    public List<Payment> getPaymentHistory(Long studentId) {
        return paymentRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    /**
     * Lấy payment theo ID
     */
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErroCode.PAYMENT_NOT_FOUND));
    }

    /**
     * Lấy payment theo vnpayTxnRef
     */
    public Payment getPaymentByTxnRef(String vnpayTxnRef) {
        return paymentRepository.findByVnpayTxnRef(vnpayTxnRef)
                .orElseThrow(() -> new AppException(ErroCode.PAYMENT_NOT_FOUND));
    }
}
