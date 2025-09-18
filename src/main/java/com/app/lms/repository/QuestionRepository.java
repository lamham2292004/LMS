package com.app.lms.repository;

import com.app.lms.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsByQuestionText(String questionText);

    List<Question> findByQuizIdOrderByOrderIndex(Long quizId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.quizId = :quizId")
    Long countQuestionsByQuiz(@Param("quizId") Long quizId);

    @Query("SELECT q FROM Question q WHERE q.quizId = :quizId AND q.questionType = 'MULTIPLE_CHOICE'")
    List<Question> findMultipleChoiceByQuiz(@Param("quizId") Long quizId);
}
