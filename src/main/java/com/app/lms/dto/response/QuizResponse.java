package com.app.lms.dto.response;

import com.app.lms.entity.Question;
import com.app.lms.entity.QuizResult;
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
public class QuizResponse {
    Long id;
    Long lessonId;
    String title;
    String description;
    Integer timeLimit; // Thời gian làm bài (phút)
    Integer maxAttempts; // Số lần làm tối đa
    Double passScore; // Điểm đạt tối thiểu
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    List<Question> questions = new ArrayList<>();
    List<QuizResult> quizResults = new ArrayList<>();
}
