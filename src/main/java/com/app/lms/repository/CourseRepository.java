package com.app.lms.repository;

import com.app.lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course,Long> {
    Boolean existsByTitle (String title);
}
