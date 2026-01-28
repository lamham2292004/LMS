package com.app.lms.repository;

import com.app.lms.entity.Enrollment;
import com.app.lms.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository <Enrollment,Long> {
    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.courseId = :courseId AND e.status = :status")
    List<Enrollment> findActiveByCourseId(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
}
