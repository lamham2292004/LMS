package com.app.lms.repository;

import com.app.lms.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    List<CouponUsage> findByStudentId(Long studentId);

    List<CouponUsage> findByCoupon_Id(Long couponId);

    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.studentId = :studentId")
    Long countByCouponIdAndStudentId(@Param("couponId") Long couponId, @Param("studentId") Long studentId);

    Boolean existsByCoupon_IdAndStudentId(Long couponId, Long studentId);

    void deleteByCourseId(Long courseId);
}
