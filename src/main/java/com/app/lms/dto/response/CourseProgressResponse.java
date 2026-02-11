package com.app.lms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressResponse implements Serializable {
    Long courseId;
    String courseName;
    Integer totalLessons;
    Integer completedLessons;
    Double overallProgressPercent; // 0.0 - 100.0
    List<LessonProgressResponse> lessonProgresses;
}
