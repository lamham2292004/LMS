package com.app.lms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOptionResponse {
    Long id;
    Long questionId;
    String answerText;
    Boolean isCorrect;
    Integer orderIndex;
}
