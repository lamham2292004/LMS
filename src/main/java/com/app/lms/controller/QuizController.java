package com.app.lms.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.quizRequest.QuizCreateRequest;
import com.app.lms.dto.request.quizRequest.QuizUpdateRequest;
import com.app.lms.dto.response.QuizResponse;
import com.app.lms.service.AuthorizationService;
import com.app.lms.service.QuizService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizController {
    final QuizService quizService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or " + "(hasRole('LECTURER') and @authorizationService.canLecturerCreateQuizInLesson(#request.lessonId, authentication.name))")
    ApiResponse<QuizResponse> createQuiz (
            @Valid @RequestBody QuizCreateRequest request,
            @CurrentUser UserTokenInfo currentUser){

        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.createQuiz(request));
        return apiResponse;
    }

    @GetMapping("{quizId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " + 
            "@authorizationService.canStudentAccessQuiz(#quizId, authentication.name)")
    ApiResponse<QuizResponse> getQuizById (@Valid @PathVariable Long quizId){
        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getQuizById(quizId));
        return apiResponse;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')or hasRole('LECTURER')")
    ApiResponse<List<QuizResponse>> getQuizList(){
        ApiResponse<List<QuizResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getAllQuizzes());
        return apiResponse;
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.canStudentAccessLessonQuizzes(#lessonId, authentication.name)")
    public ApiResponse<List<QuizResponse>> getQuizzesByLesson(@PathVariable Long lessonId) {
        ApiResponse<List<QuizResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getQuizzesByLessonId(lessonId));
        return apiResponse;
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.canStudentAccessCourseQuizzes(#courseId, authentication.name)")
    public ApiResponse<List<QuizResponse>> getQuizzesByCourse(@PathVariable Long courseId) {
        ApiResponse<List<QuizResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.getQuizzesByCourseId(courseId));
        return apiResponse;
    }

    @PutMapping("{quizId}")
    @PreAuthorize("hasRole('ADMIN') or " +
            "(hasRole('LECTURER') and @authorizationService.canLecturerEditQuiz(#quizId, authentication.name))")
    ApiResponse<QuizResponse> updateQuiz (@Valid @PathVariable Long quizId, @Valid @RequestBody QuizUpdateRequest request){
        ApiResponse<QuizResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(quizService.updateQuiz(quizId, request));
        return apiResponse;
    }

    @DeleteMapping("{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteQuiz(@Valid @PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.<String>builder()
                .result("Quiz deleted successfully")
                .build();
    }


}
