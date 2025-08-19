package com.app.lms.repository;

import com.app.lms.entity.Lessons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository <Lessons,Long>{
}
