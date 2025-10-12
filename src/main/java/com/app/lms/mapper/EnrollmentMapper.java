package com.app.lms.mapper;

import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentUpdateRequest;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface EnrollmentMapper {
    Enrollment toEnrollmentMapper (EnrollmentCreateRequest request);
    @Mapping(source = "course.title", target = "courseName")
    @Mapping(source = "course.img", target = "courseImg")
    EnrollmentResponse toEnrollmentResponse (Enrollment enrollment);

    void updateEnrollment (@MappingTarget Enrollment enrollment, EnrollmentUpdateRequest request);
}
