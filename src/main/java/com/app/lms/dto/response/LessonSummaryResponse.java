package com.app.lms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonSummaryResponse {
    Long id;
    String title;
    Integer duration;
}
