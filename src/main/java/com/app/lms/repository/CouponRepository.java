package com.app.lms.repository;

import com.app.lms.entity.Coupon;
import com.app.lms.enums.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    Boolean existsByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.status = :status")
    Optional<Coupon> findByCodeAndStatus(@Param("code") String code, @Param("status") CouponStatus status);
}
