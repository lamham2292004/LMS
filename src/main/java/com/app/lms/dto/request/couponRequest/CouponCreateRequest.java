package com.app.lms.dto.request.couponRequest;

import com.app.lms.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CouponCreateRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @Positive(message = "Giá trị giảm giá phải lớn hơn 0")
    private BigDecimal discountValue;

    private BigDecimal minOrderValue; // Giá trị đơn hàng tối thiểu
    private BigDecimal maxDiscount; // Giảm tối đa (cho PERCENTAGE type)
    private Integer usageLimit; // Giới hạn số lần sử dụng
    private OffsetDateTime startDate; // Ngày bắt đầu
    private OffsetDateTime endDate; // Ngày kết thúc
    private String description; // Mô tả
    private Long applicableCourseId; // Áp dụng cho khóa học cụ thể (null = tất cả)
}
