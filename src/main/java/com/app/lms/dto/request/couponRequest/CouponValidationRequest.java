package com.app.lms.dto.request.couponRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    private String couponCode;

    @NotNull(message = "Course ID không được để trống")
    private Long courseId;
}
