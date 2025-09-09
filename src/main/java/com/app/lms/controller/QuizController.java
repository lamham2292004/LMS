package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.quizRequest.QuizCreateRequest;
import com.app.lms.dto.request.quizRequest.QuizUpdateRequest;
import com.app.lms.dto.response.QuizResponse;
import com.app.lms.service.QuizService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizController {
    final QuizService quizService;

    @PostMapping
    ApiResponse<QuizResponse> createQuiz (@Valid @RequestBody QuizCreateRequest request){
        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.createQuiz(request));
        return apiResponse;
    }

    @GetMapping("{quizId}")
    ApiResponse<QuizResponse> getQuizById (@Valid @PathVariable Long quizId){
        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getQuizById(quizId));
        return apiResponse;
    }

    @GetMapping()
    ApiResponse<List<QuizResponse>> getQuizList(){
        ApiResponse<List<QuizResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getAllQuizzes());
        return apiResponse;
    }

    @PutMapping("{quizId}")
    ApiResponse<QuizResponse> updateQuiz (@Valid @PathVariable Long quizId, @Valid @RequestBody QuizUpdateRequest request){
        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.updateQuiz(quizId, request));
        return apiResponse;
    }

    @DeleteMapping("{quizId}")
    String deleteQuiz (@Valid @PathVariable Long quizId){
        quizService.deleteQuiz(quizId);
        return "Quiz deleted";
    }


}
