package com.app.lms.dto.request.quizRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class QuizUpdateRequest {
    String title;

    String description;

    Integer timeLimit;

    Integer maxAttempts;

    Double passScore;
}
