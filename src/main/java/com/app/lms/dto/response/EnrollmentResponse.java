package com.app.lms.dto.response;

import com.app.lms.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.OffsetDateTime;
import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse implements Serializable {
    Long id;
    Long studentId;
    String studentName;
    String studentEmail;
    Long courseId;
    EnrollmentStatus status;
    OffsetDateTime enrolledAt;
    String courseName;
    String courseImg;
}
