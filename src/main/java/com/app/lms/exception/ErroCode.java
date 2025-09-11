package com.app.lms.exception;

import lombok.Getter;

@Getter
public enum ErroCode {
    // General errors
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định"),
    TITLE_INVALID(1001, "Tiêu đề không được để trống"),
    COURSE_INVALID(1006, "Khóa học không được để trống"),
    INVALID_KEY(1003, "Lỗi không xác định"),
    TITLE_EXISTED(1002, "Tiêu đề đã tồn tại"),
    COURSE_NO_EXISTED(1004, "Khóa học không tồn tại"),
    FILE_ERRO(1005, "Lỗi khi upload file"),
    FILE_EXISTED(1007, "File rỗng"),
    LESSON_NO_EXISTED(1008, "Bài học không tồn tại"),
    CATEGORY_NO_EXISTED(1009, "Danh mục không tồn tại"),
    NAME_CATEGORY_INVALID(1010, "Tên danh mục đã tồn tại"),
    ENROLLMENT_NO_EXISTED(1011, "Đăng ký không tồn tại"),

    // Quiz related errors
    QUIZ_NO_EXISTED(1012, "Bài kiểm tra không tồn tại"),
    QUESTION_NO_EXISTED(1013, "Câu hỏi không tồn tại"),
    ANSWER_OPTION_NO_EXISTED(1014, "Đáp án không tồn tại"),

    // Quiz Result specific errors
    QUIZ_ALREADY_SUBMITTED(1015, "Đã nộp bài kiểm tra này rồi"),
    QUIZ_TIME_EXPIRED(1016, "Thời gian làm bài đã hết"),
    QUIZ_MAX_ATTEMPTS_REACHED(1017, "Đã hết số lần làm bài"),
    QUIZ_NOT_OPEN(1018, "Bài kiểm tra chưa mở"),
    QUIZ_ANSWERS_REQUIRED(1019, "Phải trả lời ít nhất 1 câu hỏi"),
    QUIZ_INVALID_ANSWER(1020, "Đáp án không hợp lệ"),
    QUIZ_PERMISSION_DENIED(1021, "Không có quyền làm bài kiểm tra này"),

    // Authentication & Authorization errors
    TOKEN_MISSING(2001, "Token không tồn tại trong request"),
    TOKEN_INVALID(2002, "Token không hợp lệ"),
    TOKEN_EXPIRED(2003, "Token đã hết hạn"),
    ACCESS_DENIED(2101, "Không có quyền truy cập"),
    STUDENT_ONLY(2103, "Chỉ dành cho học viên"),
    LECTURER_ONLY(2104, "Chỉ dành cho giảng viên"),
    NOT_ENROLLED(2106, "Chưa đăng ký khóa học"),

    // User related errors
    STUDENT_NOT_FOUND(3001, "Học viên không tồn tại"),
    USER_NOT_ACTIVE(3002, "Tài khoản đã bị khóa");
    private int code;
    private String message;

    ErroCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
