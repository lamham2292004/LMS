package com.app.lms.dto.request.enrollmentRequest;

import com.app.lms.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentCreateRequest {
    Long studentId;
    Long courseId;
    EnrollmentStatus status;
}
