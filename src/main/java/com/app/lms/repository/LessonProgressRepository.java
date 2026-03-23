package com.app.lms.repository;

import com.app.lms.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    List<LessonProgress> findByStudentIdAndLesson_CourseId(Long studentId, Long courseId);

    // Đếm số bài đã hoàn thành trong 1 khóa học
    long countByStudentIdAndLesson_CourseIdAndCompletedTrue(Long studentId, Long courseId);

    void deleteByLessonId(Long lessonId);
}
