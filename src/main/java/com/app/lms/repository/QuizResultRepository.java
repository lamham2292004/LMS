package com.app.lms.repository;

import com.app.lms.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository <QuizResult, Long> {
    List<QuizResult> findByQuizIdAndStudentIdOrderByAttemptNumberDesc(Long quizId, Long studentId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quizId = :quizId AND qr.studentId = :studentId ORDER BY qr.score DESC LIMIT 1")
    Optional<QuizResult> findBestResultByQuizIdAndStudentId(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.quizId = :quizId AND qr.studentId = :studentId")
    Integer countAttemptsByQuizIdAndStudentId(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    @Query("SELECT qr FROM QuizResult qr JOIN qr.quiz q JOIN q.lesson l WHERE l.courseId = :courseId AND qr.studentId = :studentId")
    List<QuizResult> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    List<QuizResult> findByQuizIdOrderByScoreDesc(Long quizId);
}
