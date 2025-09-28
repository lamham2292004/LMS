package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class JwtController {
    // check thông tin token
    @GetMapping("/me")
    public ApiResponse<UserTokenInfo> getCurrentUser(@CurrentUser UserTokenInfo user) {
        ApiResponse<UserTokenInfo> response = new ApiResponse<>();
        response.setResult(user);
        return response;
    }

    @GetMapping("/my-id")
    public ApiResponse<Long> getCurrentUserId(@CurrentUserId Long userId) {
        ApiResponse<Long> response = new ApiResponse<>();
        response.setResult(userId);
        return response;
    }

    @GetMapping("/simple")
    public ApiResponse<String> simpleTest() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Server hoạt động OK - không cần token!");
        return response;
    }
}
