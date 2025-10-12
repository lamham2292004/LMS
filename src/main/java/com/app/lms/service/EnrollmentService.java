package com.app.lms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.lms.dto.request.enrollmentRequest.EnrollmentCreateRequest;
import com.app.lms.dto.request.enrollmentRequest.EnrollmentUpdateRequest;
import com.app.lms.dto.response.EnrollmentResponse;
import com.app.lms.entity.Course;
import com.app.lms.entity.Enrollment;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.EnrollmentMapper;
import com.app.lms.repository.CourseRepository;
import com.app.lms.repository.EnrollmentRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class EnrollmentService {
    final EnrollmentRepository enrollmentRepository;
    final EnrollmentMapper enrollmentMapper;
    final CourseRepository courseRepository;

    public EnrollmentResponse createEnrollment(EnrollmentCreateRequest request) {
        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

        // Check if student already enrolled in this course
        if (request.getStudentId() != null) {
            boolean alreadyEnrolled = enrollmentRepository
                    .existsByStudentIdAndCourseIdAndStatus(
                            request.getStudentId(),
                            request.getCourseId(),
                            EnrollmentStatus.ACTIVE
                    );

            if (alreadyEnrolled) {
                throw new AppException(ErroCode.ALREADY_ENROLLED);
            }
        }

        // Set default status if not provided
        if (request.getStatus() == null) {
            request.setStatus(EnrollmentStatus.ACTIVE);
        }

        Enrollment enrollment = enrollmentMapper.toEnrollmentMapper(request);
        enrollment.setCourse(course);

        log.info("Creating enrollment for student {} in course {}",
                request.getStudentId(), request.getCourseId());

        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollmentMapper::toEnrollmentResponse).toList();
    }

    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        return enrollmentMapper.toEnrollmentResponse(
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() -> new AppException(ErroCode.ENROLLMENT_NO_EXISTED))
        );
    }

    public EnrollmentResponse updateEnrollment(Long enrollmentId, EnrollmentUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErroCode.ENROLLMENT_NO_EXISTED));

        enrollmentMapper.updateEnrollment(enrollment, request);
        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    public void deleteEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new AppException(ErroCode.ENROLLMENT_NO_EXISTED);
        }
        enrollmentRepository.deleteById(enrollmentId);
    }

    // Additional helper methods
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);
        return enrollments.stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .toList();
    }

    public List<EnrollmentResponse> getEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
        return enrollments.stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .toList();
    }

    public boolean isStudentEnrolledInCourse(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, courseId, EnrollmentStatus.ACTIVE
        );
    }
}
