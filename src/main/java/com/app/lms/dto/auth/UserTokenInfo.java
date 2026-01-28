package com.app.lms.dto.auth;

import com.app.lms.enums.UserType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTokenInfo {
    Long userId;           // student.id hoặc lecturer.id
    Long accountId;        // student_account.id hoặc lecturer_account.id
    String username;       // từ bảng account
    String email;          // từ bảng student/lecturer
    String fullName;       // từ bảng student/lecturer
    UserType userType;     // STUDENT, LECTURER, ADMIN
    String studentCode;    // chỉ có khi là student
    String lecturerCode;   // chỉ có khi là lecturer
    Long classId;          // chỉ có khi là student
    Long departmentId;     // chỉ có khi là lecturer
    Boolean isAdmin;       // từ lecturer_account.is_admin
}
