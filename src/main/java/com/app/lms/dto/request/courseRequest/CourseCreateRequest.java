package com.app.lms.dto.request.courseRequest;

import com.app.lms.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CourseCreateRequest {

    @NotBlank(message = "TITLE_INVALID")
    String title;

    String description;

    BigDecimal price = BigDecimal.ZERO;

    Long teacherId;

    Long categoryId;

    CourseStatus status;

    OffsetDateTime startTime;

    OffsetDateTime endTime;
}
