package com.app.lms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgressResponse implements Serializable {
    Long id;
    Long lessonId;
    String lessonTitle;
    Long studentId;
    Integer watchedSeconds;
    Integer totalSeconds;
    Integer lastPosition;
    Double progressPercent; // 0.0 - 100.0
    Boolean completed;
    OffsetDateTime updatedAt;
}
