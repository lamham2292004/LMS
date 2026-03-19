package com.app.lms.dto.request.questionRequest;

import com.app.lms.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreateRequest {
    @NotNull(message = "Quiz ID không được để trống")
    Long quizId;

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    String questionText;

    @NotNull(message = "Loại câu hỏi không được để trống")
    QuestionType questionType;

    Double points;
    Integer orderIndex;
}
