package com.app.lms.dto.response;

import com.app.lms.entity.AnswerOption;
import com.app.lms.enums.QuestionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    Long id;
    Long quizId;
    String questionText;
    QuestionType questionType;
    Integer orderIndex;
    Double points;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    List<AnswerOption> answerOptions = new ArrayList<>();
}
