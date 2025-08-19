package com.app.lms.dto.response;

import com.app.lms.entity.Courses;
import com.app.lms.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.Instant;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {
    Long id;
    Long studentId;
    Courses course;
    Long courseId;
    EnrollmentStatus status;
    Instant enrolledAt;
}
