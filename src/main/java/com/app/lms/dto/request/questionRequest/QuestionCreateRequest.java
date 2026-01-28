package com.app.lms.dto.request.questionRequest;

import com.app.lms.enums.QuestionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreateRequest {
    Long quizId;
    String questionText;
    QuestionType questionType;
    Double points;
    Integer orderIndex;
}
