package com.app.lms.dto.request.categoryRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequest {
    String name;
    String description;

}
