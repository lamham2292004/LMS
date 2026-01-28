package com.app.lms.dto.request.answerOptionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerOptionUpdateRequest {
    String answerText;
    Boolean isCorrect;
    Integer orderIndex;
}
