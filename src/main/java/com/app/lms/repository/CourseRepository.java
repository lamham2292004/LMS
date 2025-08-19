package com.app.lms.repository;

import com.app.lms.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Courses,Long> {
//    @Query("SELECT e.course FROM Enrollments e WHERE e.studentId = :studentId")
//    List<Courses> findCoursesByStudentId(@Param("studentId") Long studentId);
}
