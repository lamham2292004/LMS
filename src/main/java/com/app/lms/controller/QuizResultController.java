package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.quizResultRequest.SubmitQuizRequest;
import com.app.lms.dto.response.QuizResultResponse;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.QuizResultService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuizResultController {
    QuizResultService quizResultService;

    @PostMapping("/submit")
    public ApiResponse<QuizResultResponse> submitQuiz(
            @RequestBody SubmitQuizRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        // Validate chỉ student mới được làm quiz
        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        request.setStudentId(currentUser.getUserId());

        // Pass studentName từ JWT token
        QuizResultResponse result = quizResultService.submitQuiz(request, currentUser.getFullName());

        ApiResponse<QuizResultResponse> response = new ApiResponse<>();
        response.setResult(result);
        return response;
    }

    @GetMapping("/my-results/{quizId}")
    public ApiResponse<List<QuizResultResponse>> getMyQuizResults(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<List<QuizResultResponse>> response = new ApiResponse<>();
        response.setResult(quizResultService.getQuizResultsByStudentAndQuiz(quizId, currentUserId, currentUser.getFullName()));
        return response;
    }

    @GetMapping("/my-best-result/{quizId}")
    public ApiResponse<QuizResultResponse> getMyBestResult(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<QuizResultResponse> response = new ApiResponse<>();
        response.setResult(quizResultService.getBestResult(quizId, currentUserId));
        return response;
    }

    @GetMapping("/my-course-results/{courseId}")
    public ApiResponse<List<QuizResultResponse>> getMyCourseResults(
            @PathVariable Long courseId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<List<QuizResultResponse>> response = new ApiResponse<>();
        response.setResult(quizResultService.getStudentResultsInCourse(courseId, currentUserId));
        return response;
    }

    @GetMapping("/quiz/{quizId}/all-results")
    public ApiResponse<List<QuizResultResponse>> getAllQuizResults(
            @PathVariable Long quizId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.LECTURER &&
                (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin())) {
            throw new AppException(ErroCode.LECTURER_ONLY);
        }

        ApiResponse<List<QuizResultResponse>> response = new ApiResponse<>();
        response.setResult(quizResultService.getAllQuizResults(quizId));
        return response;
    }
    //check student hiện tại có thể làm quiz không
    @GetMapping("/can-take/{quizId}")
    public ApiResponse<Boolean> canTakeQuiz(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {

        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }

        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setResult(quizResultService.canTakeQuiz(quizId, currentUserId));
        return response;
    }


}
