package com.app.lms.dto.request.lessonProgressRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgressRequest {
    @NotNull(message = "lessonId không được để trống")
    Long lessonId;

    @NotNull(message = "watchedSeconds không được để trống")
    @Min(value = 0, message = "watchedSeconds phải >= 0")
    Integer watchedSeconds;

    @NotNull(message = "totalSeconds không được để trống")
    @Min(value = 1, message = "totalSeconds phải >= 1")
    Integer totalSeconds;

    @NotNull(message = "lastPosition không được để trống")
    @Min(value = 0, message = "lastPosition phải >= 0")
    Integer lastPosition;
}
