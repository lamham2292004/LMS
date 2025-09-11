package com.app.lms.service;

import com.app.lms.dto.request.quizResultRequest.SubmitQuizRequest;
import com.app.lms.dto.response.QuizResultResponse;
import java.util.List;

public interface QuizResultService {
    // Học viên submit bài làm
    QuizResultResponse submitQuiz(SubmitQuizRequest request);

    // Lấy tất cả kết quả của học viên cho 1 quiz
    List<QuizResultResponse> getQuizResultsByStudentAndQuiz(Long quizId, Long studentId);

    // Lấy kết quả cao nhất của học viên
    QuizResultResponse getBestResult(Long quizId, Long studentId);

    // Lấy tất cả kết quả của học viên trong khóa học
    List<QuizResultResponse> getStudentResultsInCourse(Long courseId, Long studentId);

    // Lấy tất cả kết quả của quiz (cho giảng viên)
    List<QuizResultResponse> getAllQuizResults(Long quizId);

    // Kiểm tra học viên có thể làm quiz không
    boolean canTakeQuiz(Long quizId, Long studentId);
}
