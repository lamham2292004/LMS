package com.app.lms.repository;

import com.app.lms.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository <Lesson,Long>{
    Boolean existsByTitle (String title);

    List<Lesson> findByCourseId(Long courseId);
}
