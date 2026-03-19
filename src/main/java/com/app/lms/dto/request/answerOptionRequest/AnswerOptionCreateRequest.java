package com.app.lms.dto.request.answerOptionRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerOptionCreateRequest {
    @NotNull(message = "Question ID không được để trống")
    Long questionId;

    @NotBlank(message = "Nội dung đáp án không được để trống")
    String answerText;

    Boolean isCorrect;
    Integer orderIndex;
}
