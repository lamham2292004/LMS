package com.app.lms.service;

import com.app.lms.dto.request.couponRequest.CouponCreateRequest;
import com.app.lms.dto.request.couponRequest.CouponUpdateRequest;
import com.app.lms.dto.response.CouponResponse;
import com.app.lms.dto.response.CouponValidationResponse;
import com.app.lms.entity.Coupon;
import com.app.lms.entity.Course;
import com.app.lms.entity.CouponUsage;
import com.app.lms.entity.Payment;
import com.app.lms.enums.CouponStatus;
import com.app.lms.enums.DiscountType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.CouponMapper;
import com.app.lms.repository.CouponRepository;
import com.app.lms.repository.CouponUsageRepository;
import com.app.lms.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CouponService {

    CouponRepository couponRepository;
    CouponUsageRepository couponUsageRepository;
    CourseRepository courseRepository;
    CouponMapper couponMapper;

    // ==================== CRUD Operations ====================

    /**
     * Tạo coupon mới (Admin)
     */
    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest request, Long adminId) {
        log.info("Creating coupon: {} by admin: {}", request.getCode(), adminId);

        // Check if code already exists
        if (couponRepository.findByCode(request.getCode().toUpperCase()).isPresent()) {
            throw new AppException(ErroCode.COUPON_INVALID);
        }

        // Map request to entity
        Coupon coupon = couponMapper.toCoupon(request);
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setCreatedBy(adminId);

        // Set applicable course if provided
        if (request.getApplicableCourseId() != null) {
            Course course = courseRepository.findById(request.getApplicableCourseId())
                    .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));
            coupon.setApplicableCourse(course);
        }

        coupon = couponRepository.save(coupon);
        log.info("Coupon created successfully: {}", coupon.getId());

        return couponMapper.toCouponResponse(coupon);
    }

    /**
     * Lấy tất cả coupon (Admin)
     */
    public List<CouponResponse> getAllCoupons() {
        log.info("Getting all coupons");
        return couponRepository.findAll().stream()
                .map(couponMapper::toCouponResponse)
                .toList();
    }

    /**
     * Lấy coupon theo ID (Admin)
     */
    public CouponResponse getCouponById(Long couponId) {
        log.info("Getting coupon by ID: {}", couponId);
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new AppException(ErroCode.COUPON_NOT_FOUND));
        return couponMapper.toCouponResponse(coupon);
    }

    /**
     * Cập nhật coupon (Admin)
     */
    @Transactional
    public CouponResponse updateCoupon(Long couponId, CouponUpdateRequest request) {
        log.info("Updating coupon ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new AppException(ErroCode.COUPON_NOT_FOUND));

        // Update fields using mapper (ignores null values)
        couponMapper.updateCoupon(coupon, request);

        // Handle code update (uppercase)
        if (request.getCode() != null) {
            coupon.setCode(request.getCode().toUpperCase());
        }

        // Handle applicable course update
        if (request.getApplicableCourseId() != null) {
            Course course = courseRepository.findById(request.getApplicableCourseId())
                    .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));
            coupon.setApplicableCourse(course);
        }

        coupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(coupon);
    }

    /**
     * Xóa coupon (Admin)
     */
    @Transactional
    public void deleteCoupon(Long couponId) {
        log.info("Deleting coupon ID: {}", couponId);
        if (!couponRepository.existsById(couponId)) {
            throw new AppException(ErroCode.COUPON_NOT_FOUND);
        }
        couponRepository.deleteById(couponId);
    }

    // ==================== Validation Operations ====================

    /**
     * Validate coupon cho khóa học cụ thể (Student)
     */
    public CouponValidationResponse validateCouponForCourse(String couponCode, Long courseId) {
        log.info("Validating coupon: {} for course: {}", couponCode, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

        return validateCoupon(couponCode, course, course.getPrice());
    }

    /**
     * Validate coupon và tính discount
     */
    public CouponValidationResponse validateCoupon(String couponCode, Course course, BigDecimal orderValue) {
        log.info("Validating coupon: {} for course: {} (price: {})", couponCode, course.getId(), orderValue);

        // 1. Tìm coupon
        Coupon coupon = couponRepository.findByCode(couponCode.toUpperCase())
                .orElse(null);

        if (coupon == null) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Mã giảm giá không tồn tại")
                    .build();
        }

        // 2. Kiểm tra trạng thái
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Mã giảm giá không còn hiệu lực")
                    .build();
        }

        // 3. Kiểm tra thời gian
        OffsetDateTime now = OffsetDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Mã giảm giá chưa có hiệu lực")
                    .build();
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Mã giảm giá đã hết hạn")
                    .build();
        }

        // 4. Kiểm tra usage limit
        if (coupon.getUsageLimit() != null) {
            int usedCount = coupon.getUsedCount() != null ? coupon.getUsedCount() : 0;
            if (usedCount >= coupon.getUsageLimit()) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message("Mã giảm giá đã hết lượt sử dụng")
                        .build();
            }
        }

        // 5. Kiểm tra áp dụng cho khóa học cụ thể
        if (coupon.getApplicableCourse() != null) {
            Long applicableCourseId = coupon.getApplicableCourse().getId();
            Long targetCourseId = course.getId();
            if (applicableCourseId != null && !applicableCourseId.equals(targetCourseId)) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message("Mã giảm giá không áp dụng cho khóa học này")
                        .build();
            }
        }

        // 6. Kiểm tra giá trị đơn hàng tối thiểu
        if (coupon.getMinOrderValue() != null) {
            if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message(String.format("Đơn hàng phải tối thiểu %s VND mới áp dụng mã này",
                                coupon.getMinOrderValue().toPlainString()))
                        .build();
            }
        }

        // 7. Tính discount
        BigDecimal discountAmount = calculateDiscount(coupon, orderValue);
        BigDecimal finalPrice = orderValue.subtract(discountAmount);

        // Đảm bảo giá cuối >= 0
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        log.info("Coupon valid! Discount: {}, Final price: {}", discountAmount, finalPrice);

        return CouponValidationResponse.builder()
                .valid(true)
                .message("Áp dụng mã giảm giá thành công")
                .couponCode(couponCode.toUpperCase())
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

    /**
     * Tính số tiền giảm dựa vào loại coupon
     */
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderValue) {
        BigDecimal discount;

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            // Giảm theo %
            BigDecimal discountValue = coupon.getDiscountValue();
            if (discountValue == null)
                discountValue = BigDecimal.ZERO;

            discount = orderValue.multiply(discountValue)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // Apply max discount nếu có
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            // Giảm số tiền cố định
            discount = coupon.getDiscountValue();
            if (discount == null)
                discount = BigDecimal.ZERO;
        }

        // Đảm bảo discount không vượt quá order value
        if (discount.compareTo(orderValue) > 0) {
            discount = orderValue;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Ghi nhận việc sử dụng coupon
     */
    @Transactional
    public void recordUsage(Coupon coupon, Payment payment, Long studentId, BigDecimal discountAmount) {
        log.info("Recording coupon usage - Coupon: {}, Student: {}, Payment: {}",
                coupon.getCode(), studentId, payment.getId());

        // Tạo CouponUsage record
        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .studentId(studentId)
                .payment(payment)
                .discountAmount(discountAmount)
                .courseId(payment.getCourse().getId())
                .build();

        couponUsageRepository.save(usage);

        // Tăng usedCount
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        log.info("Coupon usage recorded successfully. Total used: {}", coupon.getUsedCount());
    }

    /**
     * Lấy coupon theo code
     */
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new AppException(ErroCode.COUPON_NOT_FOUND));
    }
}
