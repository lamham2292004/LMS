package com.app.lms.dto.request.notificationRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPayload {
    Long admin_id;
    String admin_type;
    Long lecturer_id;
    String lecturer_name;
    String title;
    String course_review_url;
    String date;
}

