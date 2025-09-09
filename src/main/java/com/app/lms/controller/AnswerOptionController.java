package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionCreateRequest;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionUpdateRequest;
import com.app.lms.dto.response.AnswerOptionResponse;
import com.app.lms.service.AnswerOptionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/answerOption")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerOptionController {
    final AnswerOptionService answerOptionService;

    @PostMapping
    ApiResponse<AnswerOptionResponse> createAnswerOption(@Valid @RequestBody AnswerOptionCreateRequest request){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.createAnswerOption(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<AnswerOptionResponse>> getAllAnswerOption(){
        ApiResponse<List<AnswerOptionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.getAllAnswerOptions());
        return apiResponse;
    }

    @GetMapping("{answerOptionId}")
    ApiResponse<AnswerOptionResponse> getAnswerOptionById(@Valid @PathVariable("answerOptionId") Long answerOptionId){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.getAnswerOptionById(answerOptionId));
        return apiResponse;
    }

    @PutMapping("{answerOptionId}")
    ApiResponse<AnswerOptionResponse> updateAnswerOption( @PathVariable("answerOptionId") Long answerOptionId, @Valid @RequestBody AnswerOptionUpdateRequest request){
        ApiResponse<AnswerOptionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(answerOptionService.updateAnswerOption(answerOptionId, request));
        return apiResponse;
    }

    @DeleteMapping("{answerOptionId}")
    String deleteAnswerOption(@PathVariable("answerOptionId") Long answerOptionId){
        answerOptionService.deleteAnswerOption(answerOptionId);
        return "Deleted Answer Option Id: " + answerOptionId;
    }
}
