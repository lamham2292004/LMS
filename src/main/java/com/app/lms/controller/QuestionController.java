package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.questionRequest.QuestionCreateRequest;
import com.app.lms.dto.request.questionRequest.QuestionUpdateRequest;
import com.app.lms.dto.response.QuestionResponse;
import com.app.lms.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionController {
    final QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or"+"(hasRole('LECTURER') and @authorizationService.canLecturerCreateQuestion(#request.quizId,authentication.name))")
    ApiResponse createQuestion(@Valid @RequestBody QuestionCreateRequest request,@CurrentUser UserTokenInfo currentUser) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(questionService.createQuestion(request));
        return apiResponse;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ApiResponse<List<QuestionResponse>> getAllQuestions() {
        ApiResponse<List<QuestionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.getAllQuestions());
        return apiResponse;
    }

    @GetMapping("{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')"+"(@authorizationService.canStudentViewQuestion(#questionId,authentication.name))")
    ApiResponse<QuestionResponse> getQuestion(@Valid @PathVariable("questionId") Long questionId) {
        ApiResponse<QuestionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.getQuestionById(questionId));
        return apiResponse;
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.canStudentViewQuizQuestions(#quizId, authentication.name)")
    public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {
        ApiResponse<List<QuestionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.getQuestionsByQuizId(quizId));
        return apiResponse;
    }

    @PutMapping("{questionId}")
    @PreAuthorize("(hasRole('ADMIN')) or "+"hasRole('LECTURER') and @authorizationService.canLecturerEditQuestion(#questionId,authentication.name)")
    ApiResponse<QuestionResponse> updateQuiz(@Valid @PathVariable("questionId") Long questionId, @RequestBody QuestionUpdateRequest request) {
        ApiResponse<QuestionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.updateQuestion(questionId, request));
        return apiResponse;
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteQuestion(@Valid @PathVariable("questionId") Long questionId) {
        questionService.deleteQuestion(questionId);
        return ApiResponse.<String>builder()
                .result("Question deleted successfully")
                .build();
    }
}
