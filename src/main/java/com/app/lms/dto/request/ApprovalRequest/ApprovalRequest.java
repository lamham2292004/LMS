package com.app.lms.dto.request.ApprovalRequest;

import com.app.lms.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequest {
    @NotNull(message = "Approval status is required")
    ApprovalStatus approvalStatus;

    String rejectionReason; // Bắt buộc nếu REJECTED
}
