package com.app.lms.dto.request.lessonRequest;

import com.app.lms.enums.LessonStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateRequest {

    @NotNull(message = "COURSE_INVALID")
    Long courseId;

    @NotBlank(message = "TITLE_INVALID")
    String title;

    String description;

    Integer orderIndex;

    LessonStatus status;

    Integer duration;

    String videoPath;

}
