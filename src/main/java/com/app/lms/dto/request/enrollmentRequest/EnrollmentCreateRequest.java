package com.app.lms.dto.request.enrollmentRequest;

import com.app.lms.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentCreateRequest {
    Long studentId;
    @NotNull(message = "COURSE_INVALID")
    Long courseId;
    EnrollmentStatus status;
}
