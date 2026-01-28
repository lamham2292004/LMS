package com.app.lms.dto.request.couponRequest;

import com.app.lms.enums.CouponStatus;
import com.app.lms.enums.DiscountType;
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
public class CouponUpdateRequest {

    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String description;
    private Long applicableCourseId;
    private CouponStatus status; // ACTIVE, INACTIVE, EXPIRED
}
