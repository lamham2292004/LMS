package com.app.lms.dto.response;

import com.app.lms.entity.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse implements Serializable {
    Long id;
    String name;
    String description;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    List<CourseResponse> courses;
}
