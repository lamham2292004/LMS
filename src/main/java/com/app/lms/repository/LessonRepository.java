package com.app.lms.repository;

import com.app.lms.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository <Lesson,Long>{
    Boolean existsByTitle (String title);

    List<Lesson> findByCourseId(Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.courseId = :courseId AND l.status = 'OPEN'")
    List<Lesson> findOpenLessonsByCourse(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.courseId = :courseId")
    Long countLessonsByCourse(@Param("courseId") Long courseId);
}
