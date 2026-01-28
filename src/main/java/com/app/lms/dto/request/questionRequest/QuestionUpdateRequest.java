package com.app.lms.dto.request.questionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionUpdateRequest {
    String questionText;
    String questionType;
    Double points;
    Integer orderIndex;
}
