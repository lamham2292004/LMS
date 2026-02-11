package com.app.lms.dto.response;

import com.app.lms.enums.LessonStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.OffsetDateTime;
import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse implements Serializable {
    Long id;
    Long courseId;
    String title;
    String description;
    int orderIndex;
    Integer duration; // thời lượng bài học (phút)
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    String youtubeUrl;
    String youtubeEmbedUrl; // URL nhúng video cho frontend (iframe)
    LessonStatus status;

}
