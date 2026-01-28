package com.app.lms.dto.response;

import com.app.lms.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String vnpayTxnRef;
    private Long studentId;
    private String studentEmail;
    private Long courseId;
    private String courseTitle;
    private BigDecimal originalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private String couponCode;
    private PaymentStatus status;
    private String paymentUrl; // URL để redirect đến VNPay
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
}
