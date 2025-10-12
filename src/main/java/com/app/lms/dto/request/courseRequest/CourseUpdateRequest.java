package com.app.lms.dto.request.courseRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.app.lms.enums.CourseStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateRequest {
    String title;
    String description;
    BigDecimal price;
    CourseStatus status;   // upcoming | open | closed
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    Long categoryId;  // Fix: camelCase để Lombok tạo getCategoryId()
}
