package com.app.lms.repository;

import com.app.lms.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    boolean existsByTitle (String title);

    List<Quiz> findByLessonId(Long lessonId);
}
