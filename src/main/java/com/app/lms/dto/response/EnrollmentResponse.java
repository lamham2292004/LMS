package com.app.lms.dto.response;

import com.app.lms.entity.Course;
import com.app.lms.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {
    Long id;
    Long studentId;
    Long courseId;
    EnrollmentStatus status;
    OffsetDateTime enrolledAt;
}
