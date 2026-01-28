package com.app.lms.dto.request.categoryRequest;

import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {
    String name;
    String description;

    List<CourseCreateRequest> Courses;

}
