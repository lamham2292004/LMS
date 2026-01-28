package com.app.lms.repository;

import com.app.lms.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    boolean existsByTitle (String title);

    List<Quiz> findByLessonId(Long lessonId);

    @Query("SELECT q FROM Quiz q JOIN q.lesson l WHERE l.courseId = :courseId")
    List<Quiz> findQuizzesByCourse(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.lessonId = :lessonId")
    Long countQuizzesByLesson(@Param("lessonId") Long lessonId);
}
