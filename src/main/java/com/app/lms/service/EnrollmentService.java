package com.app.lms.service;

import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentUpdateRequest;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.entity.Enrollment;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.EnrollmentMapper;
import com.app.lms.repository.EnrollmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentService {
    final EnrollmentRepository enrollmentRepository;
    final EnrollmentMapper enrollmentMapper;

    public EnrollmentResponse createEnrollment(EnrollmentCreateRequest request) {
        Enrollment enrollment = enrollmentMapper.toEnrollmentMapper(request);

        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollmentMapper::toEnrollmentResponse).toList();
    }

    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErroCode.ENROLLMENT_NO_EXISTED)));
    }

    public EnrollmentResponse updateEnrollment(Long enrollmentId, EnrollmentUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErroCode.ENROLLMENT_NO_EXISTED));
        enrollmentMapper.updateEnrollment(enrollment,request);
        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public void deleteEnrollment(Long enrollmentId) {
        enrollmentRepository.deleteById(enrollmentId);
    }
}
