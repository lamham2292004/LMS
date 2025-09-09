package com.app.lms.repository;

import com.app.lms.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    boolean existsByAnswerText(String answerText);
}
