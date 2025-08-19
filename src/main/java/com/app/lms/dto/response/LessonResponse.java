package com.app.lms.dto.response;

import com.app.lms.entity.Courses;
import com.app.lms.enums.LessonStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    Courses course;
    LessonStatus status;

}

