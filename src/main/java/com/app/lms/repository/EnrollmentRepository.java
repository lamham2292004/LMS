package com.app.lms.repository;

import com.app.lms.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository <Enrollment,Long> {
//    List<Courses> findByTeacherId(Long teacherId);
}
