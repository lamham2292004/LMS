package com.app.lms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResultResponse {
    Long id;
    Long quizId;
    String quizTitle;
    Long studentId;
    String studentName; // Từ JWT token
    BigDecimal score;
    Integer totalQuestions;
    Integer correctAnswers;
    Integer timeTaken;
    Integer attemptNumber;
    Boolean isPassed;
    OffsetDateTime takenAt;
    String feedback; // Chi tiết từng câu đúng/sai
}
