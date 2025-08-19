package com.app.lms.repository;

import com.app.lms.entity.Courses;
import com.app.lms.entity.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository <Enrollments,Long> {
//    List<Courses> findByTeacherId(Long teacherId);
}
