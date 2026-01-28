package com.app.lms.repository;

import com.app.lms.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    boolean existsByAnswerText(String answerText);

    List<AnswerOption> findByQuestionIdOrderByOrderIndex(Long questionId);

    @Query("SELECT ao FROM AnswerOption ao WHERE ao.questionId = :questionId AND ao.isCorrect = true")
    Optional<AnswerOption> findCorrectAnswerByQuestion(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(ao) FROM AnswerOption ao WHERE ao.questionId = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);
}
