package com.app.lms.dto.response;

import com.app.lms.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
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
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    Long categoryId;
    String categoryName;
    String img;

    List<LessonSummaryResponse> lessons;
    List<EnrollmentResponse> enrollments;
}
