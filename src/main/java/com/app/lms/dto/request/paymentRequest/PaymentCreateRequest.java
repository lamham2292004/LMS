package com.app.lms.dto.request.paymentRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private String couponCode; // Optional - mã giảm giá

    private String language; // "vn" or "en" - ngôn ngữ giao diện VNPay

    private String bankCode; // Optional - mã ngân hàng (VNBANK, INTCARD, etc.)
}
