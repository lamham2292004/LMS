package com.app.lms.mapper;

import com.app.lms.dto.response.PaymentResponse;
import com.app.lms.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "coupon.code", target = "couponCode")
    @Mapping(target = "paymentUrl", ignore = true)
    PaymentResponse toPaymentResponse(Payment payment);
}
