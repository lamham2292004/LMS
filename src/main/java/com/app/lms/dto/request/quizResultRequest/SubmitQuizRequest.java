package com.app.lms.dto.request.quizResultRequest;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitQuizRequest {
    @NotNull(message = "QUIZ_NO_EXISTED")
    Long quizId;

    Long studentId; // Sẽ được set từ JWT token, không trust từ client

    @NotNull(message = "QUIZ_ANSWERS_REQUIRED")
    Map<Long, String> answers; // questionId -> answer (ID của AnswerOption hoặc text)

    Integer timeTaken; // Thời gian làm bài (phút)
}
