package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.questionRequest.QuestionCreateRequest;
import com.app.lms.dto.request.questionRequest.QuestionUpdateRequest;
import com.app.lms.dto.response.QuestionResponse;
import com.app.lms.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionController {
    final QuestionService questionService;

    @PostMapping
    ApiResponse createQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(questionService.createQuestion(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<QuestionResponse>> getAllQuestions() {
        ApiResponse<List<QuestionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.getAllQuestions());
        return apiResponse;
    }

    @GetMapping("{questionId}")
    ApiResponse<QuestionResponse> getQuestion(@Valid @PathVariable("questionId") Long questionId) {
        ApiResponse<QuestionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.getQuestionById(questionId));
        return apiResponse;
    }

    @PutMapping("{questionId}")
    ApiResponse<QuestionResponse> updateQuiz(@Valid @PathVariable("questionId") Long questionId, @RequestBody QuestionUpdateRequest request) {
        ApiResponse<QuestionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(questionService.updateQuestion(questionId, request));
        return apiResponse;
    }

    @DeleteMapping("{questionId}")
    String deleteQuestion(@Valid @PathVariable("questionId") Long questionId) {
        questionService.deleteQuestion(questionId);
        return "Question deleted";
    }
}
