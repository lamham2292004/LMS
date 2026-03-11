package com.app.lms.entity;

import com.app.lms.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Thông tin student
    @Column(name = "student_id", nullable = false)
    Long studentId;

    @Column(name = "student_email")
    String studentEmail;

    @Column(name = "student_name")
    String studentName;

    // Thông tin khóa học
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "lessons", "enrollments", "category", "teacher"})
    Course course;

    // Thông tin giá
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    BigDecimal originalPrice; // Giá gốc của khóa học

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    BigDecimal discountAmount = BigDecimal.ZERO; // Số tiền giảm (nếu có coupon)

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    BigDecimal finalPrice; // Giá cuối = originalPrice - discountAmount

    // VNPay transaction info
    @Column(name = "vnpay_txn_ref", unique = true, nullable = false, length = 50)
    String vnpayTxnRef; // Mã giao dịch của LMS (tự generate)

    @Column(name = "vnpay_transaction_no", length = 50)
    String vnpayTransactionNo; // Mã giao dịch từ VNPay (sau khi thanh toán)

    @Column(name = "vnpay_response_code", length = 10)
    String vnpayResponseCode; // Mã phản hồi từ VNPay (00 = thành công)

    @Column(name = "vnpay_bank_code", length = 20)
    String vnpayBankCode; // Mã ngân hàng

    @Column(name = "vnpay_bank_tran_no", length = 50)
    String vnpayBankTranNo; // Mã giao dịch tại ngân hàng

    @Column(name = "vnpay_card_type", length = 20)
    String vnpayCardType; // Loại thẻ (ATM, VISA, etc.)

    @Column(name = "vnpay_pay_date", length = 20)
    String vnpayPayDate; // Thời gian thanh toán từ VNPay (format: yyyyMMddHHmmss)

    // Payment status
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    PaymentStatus status = PaymentStatus.PENDING; // PENDING, SUCCESS, FAILED, CANCELLED

    // Coupon đã áp dụng (nếu có)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @Column(name = "ip_address", length = 50)
    String ipAddress; // IP của người thanh toán

    @Column(name = "order_info", columnDefinition = "TEXT")
    String orderInfo; // Mô tả đơn hàng gửi cho VNPay

    @CreationTimestamp
    @Column(name = "created_at")
    OffsetDateTime createdAt; // Thời gian tạo payment

    @Column(name = "paid_at")
    OffsetDateTime paidAt; // Thời gian thanh toán thành công

    @Column(name = "enrollment_id")
    Long enrollmentId; // ID của enrollment được tạo sau khi payment thành công
}
