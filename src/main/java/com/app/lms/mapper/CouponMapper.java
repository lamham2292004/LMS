package com.app.lms.mapper;

import com.app.lms.dto.request.couponRequest.CouponCreateRequest;
import com.app.lms.dto.request.couponRequest.CouponUpdateRequest;
import com.app.lms.dto.response.CouponResponse;
import com.app.lms.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponMapper {

    /**
     * Convert CreateRequest to Coupon entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", constant = "0")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "applicableCourse", ignore = true) // Set manually in service
    @Mapping(target = "createdBy", ignore = true) // Set manually in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usages", ignore = true)
    Coupon toCoupon(CouponCreateRequest request);

    /**
     * Convert Coupon entity to Response DTO
     */
    @Mapping(target = "applicableCourseId", source = "applicableCourse.id")
    @Mapping(target = "applicableCourseName", source = "applicableCourse.title")
    CouponResponse toCouponResponse(Coupon coupon);

    /**
     * Update Coupon entity from UpdateRequest
     * Ignores null values
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "applicableCourse", ignore = true) // Set manually in service
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usages", ignore = true)
    void updateCoupon(@MappingTarget Coupon coupon, CouponUpdateRequest request);
}
