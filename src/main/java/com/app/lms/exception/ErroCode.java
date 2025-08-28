package com.app.lms.exception;

import lombok.Getter;

@Getter
public enum ErroCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định"),
    TITLE_INVALID(1001, "Tiêu đề không được để trống"),
    COURSE_INVALID(1006, "khóa học không được để trống"),
    INVALID_KEY(1003, "Lỗi không xác định"),
    TITLE_EXISTED(1002, "tiêu đề đã tồn tại"),
    COURSE_NO_EXISTED(1004, "Khóa học không tồn tại"),
    FILE_ERRO(1005, "Lỗi khi upload file"),
    FILE_EXISTED(1007, "File rỗng"),
    LESSON_NO_EXISTED(1008, "Lớp học không tồn tại"),
    CATEGORY_NO_EXISTED(1009, "Danh sách không tồn tại"),
    NAME_CATEGORY_INVALID(1001, "Tên Danh sách đã tồn tại"),
    ENROLLMENT_NO_EXISTED(1009, "Người đăng ký không tồn tại"),
    ;
    private int code;
    private String message;

    ErroCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
