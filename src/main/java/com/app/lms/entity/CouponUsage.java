package com.app.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "coupon_usage")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id", nullable = false)
    Coupon coupon;

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    BigDecimal discountAmount; // Số tiền đã được giảm

    @Column(name = "course_id", nullable = false)
    Long courseId; // Khóa học đã mua

    @CreationTimestamp
    @Column(name = "used_at")
    OffsetDateTime usedAt;
}
