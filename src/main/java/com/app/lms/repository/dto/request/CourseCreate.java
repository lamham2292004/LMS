package com.app.lms.repository.dto.request;

import com.app.lms.entity.Enrollments;
import com.app.lms.entity.Lessons;
import jakarta.persistence.Table;
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
public class CourseCreate {
    String title;
    String description;
    BigDecimal price = BigDecimal.ZERO;
    Long teacherId;
    Instant createdAt;
    Instant updatedAt;
    List<Lessons> lessons;
    List<Enrollments> enrollments;
}
