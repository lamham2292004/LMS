package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionCreateRequest;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionUpdateRequest;
import com.app.lms.dto.response.AnswerOptionResponse;
import com.app.lms.service.AnswerOptionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/answerOption")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerOptionController {
    final AnswerOptionService answerOptionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or "+"hasRole('LECTURER') and @authorizationService.canLecturerCreateAnswerOption(#request.questionId,authentication.name)")
    ApiResponse<AnswerOptionResponse> createAnswerOption(@Valid @RequestBody AnswerOptionCreateRequest request,@CurrentUser UserTokenInfo currentUser){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.createAnswerOption(request));
        return apiResponse;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ApiResponse<List<AnswerOptionResponse>> getAllAnswerOption(){
        ApiResponse<List<AnswerOptionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.getAllAnswerOptions());
        return apiResponse;
    }

    @GetMapping("{answerOptionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or "+"(@authorizationService.canStudentViewAnswerOption(#answerOptionId,authentication.name))")
    ApiResponse<AnswerOptionResponse> getAnswerOptionById(@Valid @PathVariable("answerOptionId") Long answerOptionId){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.getAnswerOptionById(answerOptionId));
        return apiResponse;
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or "+"(@authorizationService.canStudentViewQuestionAnswers(#questionId,authentication.name))")
    public ApiResponse<List<AnswerOptionResponse>> getAnswerOptionsByQuestion(@PathVariable Long questionId) {
        ApiResponse<List<AnswerOptionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.getAnswerOptionsByQuestionId(questionId));
        return apiResponse;
    }

    @PutMapping("{answerOptionId}")
    @PreAuthorize("hasRole('ADMIN') or"+"(hasRole('LECTURER') and @authorizationService.canLecturerEditAnswerOption(#answerOptionId,authentication.name))")
    ApiResponse<AnswerOptionResponse> updateAnswerOption( @PathVariable("answerOptionId") Long answerOptionId, @Valid @RequestBody AnswerOptionUpdateRequest request){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.updateAnswerOption(answerOptionId, request));
        return apiResponse;
    }

    @DeleteMapping("{answerOptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteAnswerOption(@PathVariable("answerOptionId") Long answerOptionId) {
        answerOptionService.deleteAnswerOption(answerOptionId);
        return ApiResponse.<String>builder()
                .result("Answer Option deleted successfully")
                .build();
    }
}
