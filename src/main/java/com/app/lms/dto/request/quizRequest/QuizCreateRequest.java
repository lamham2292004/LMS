package com.app.lms.dto.request.quizRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizCreateRequest {
    @NotNull
    Long lessonId;

    @NotBlank
    String title;

    String description;

    Integer timeLimit;

    Integer maxAttempts;

    Double passScore;
}
