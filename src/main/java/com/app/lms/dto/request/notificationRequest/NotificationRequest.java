package com.app.lms.dto.request.notificationRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    String topic;
    NotificationPayload payload;
    String priority;
    String key;
}

