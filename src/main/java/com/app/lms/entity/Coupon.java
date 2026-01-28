package com.app.lms.entity;

import com.app.lms.enums.CouponStatus;
import com.app.lms.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "coupons")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false, length = 50)
    String code; // Mã giảm giá (ví dụ: "WELCOME2024", "SALE50")

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    DiscountType discountType; // PERCENTAGE hoặc FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    BigDecimal discountValue; // 20 (cho 20%) hoặc 100000 (cho 100,000 VND)

    @Column(name = "min_order_value", precision = 10, scale = 2)
    BigDecimal minOrderValue; // Giá trị đơn hàng tối thiểu để áp dụng

    @Column(name = "max_discount", precision = 10, scale = 2)
    BigDecimal maxDiscount; // Giảm tối đa (dành cho PERCENTAGE type)

    @Column(name = "usage_limit")
    Integer usageLimit; // Giới hạn số lần sử dụng (null = unlimited)

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    Integer usedCount = 0; // Đã được sử dụng bao nhiêu lần

    @Column(name = "start_date")
    OffsetDateTime startDate; // Ngày bắt đầu có hiệu lực

    @Column(name = "end_date")
    OffsetDateTime endDate; // Ngày hết hạn

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    CouponStatus status = CouponStatus.ACTIVE; // ACTIVE, INACTIVE, EXPIRED

    @Column(columnDefinition = "TEXT")
    String description; // Mô tả về coupon

    // Áp dụng cho khóa học cụ thể (null = áp dụng cho tất cả)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicable_course_id")
    Course applicableCourse;

    @Column(name = "created_by")
    Long createdBy; // Admin ID người tạo coupon

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;

    // Quan hệ 1-n với CouponUsage
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<CouponUsage> usages = new ArrayList<>();
}
