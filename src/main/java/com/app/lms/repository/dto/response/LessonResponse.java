package com.app.lms.repository.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    Long id;
    Long courseId;
    String title;
    String description;
    Integer orderIndex;
    Integer duration;   // thời lượng bài học (phút)
    Instant createdAt;
    Instant updatedAt;
}

