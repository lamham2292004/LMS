package com.app.lms.dto.response;

import com.app.lms.enums.CourseStatus;
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
    CourseStatus status;
    Instant startTime;
    Instant endTime;
    Instant createdAt;
    Instant updatedAt;

    List<LessonResponse> lessons;
    List<EnrollmentResponse> enrollments;
}
