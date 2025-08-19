package com.app.lms.repository.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class CourseResponse {
    Long id;
    String title;
    String description;
    BigDecimal price = BigDecimal.ZERO;
    Long teacherId;
    Instant createdAt;
    Instant updatedAt;

    List<LessonResponse> lessons;
    List<EnrollmentResponse> enrollments;
}
