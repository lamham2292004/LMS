package com.app.lms.dto.response;

import com.app.lms.entity.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    Long id;
    String name;
    String description;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    List<CourseResponse> courses ;
}
