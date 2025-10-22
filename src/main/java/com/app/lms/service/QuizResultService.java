package com.app.lms.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.app.lms.entity.AnswerOption;
import com.app.lms.entity.Question;
import com.app.lms.entity.Quiz;
import com.app.lms.entity.QuizResult;
import com.app.lms.repository.EnrollmentRepository;
import com.app.lms.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.lms.dto.request.quizResultRequest.SubmitQuizRequest;
import com.app.lms.dto.response.QuizResultResponse;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.enums.QuestionType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.QuizResultMapper;
import com.app.lms.repository.QuizResultRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class QuizResultService {

    QuizResultRepository quizResultRepository;
    QuizRepository quizRepository;
    EnrollmentRepository enrollmentRepository;
    QuizResultMapper quizResultMapper;

    public QuizResultResponse submitQuiz(SubmitQuizRequest request, String studentName) {
        // 1. Validate dữ liệu đầu vào
        validateQuizSubmission(request);

        // 2. Lấy quiz và validate
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        // 3. Kiểm tra quyền làm bài
        validateQuizPermission(request.getQuizId(), request.getStudentId());

        // 4. Tính điểm
        QuizGradingResult gradingResult = calculateScore(quiz, request.getAnswers());

        // 5. Xác định số lần thử
        Integer currentAttempt = quizResultRepository
                .countAttemptsByQuizIdAndStudentId(request.getQuizId(), request.getStudentId()) + 1;

        // 6. Tạo QuizResult
        QuizResult quizResult = QuizResult.builder()
                .quiz(quiz)
                .quizId(request.getQuizId())
                .studentId(request.getStudentId())
                .score(gradingResult.getScore())
                .totalQuestions(gradingResult.getTotalQuestions())
                .correctAnswers(gradingResult.getCorrectAnswers())
                .timeTaken(request.getTimeTaken())
                .attemptNumber(currentAttempt)
                .isPassed(gradingResult.getScore().doubleValue() >=
                        (quiz.getPassScore() != null ? quiz.getPassScore() : 60.0))
                .build();

        // 7. Lưu vào database
        QuizResult savedResult = quizResultRepository.save(quizResult);
        savedResult.setQuiz(quiz);

        // 8. Log kết quả
        log.info("Student {} completed quiz {} with score {}",
                request.getStudentId(), request.getQuizId(), gradingResult.getScore());

        // 9. Trả về response sử dụng mapper
        return quizResultMapper.toQuizResultResponse(
                savedResult, quiz, studentName, gradingResult.getFeedback());
    }

    public List<QuizResultResponse> getQuizResultsByStudentAndQuiz(Long quizId, Long studentId, String studentName) {
        List<QuizResult> results = quizResultRepository
                .findByQuizIdAndStudentIdOrderByAttemptNumberDesc(quizId, studentId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, quiz, studentName, null))
                .toList();
    }

    public QuizResultResponse getBestResult(Long quizId, Long studentId) {
        Optional<QuizResult> bestResult = quizResultRepository
                .findBestResultByQuizIdAndStudentId(quizId, studentId);

        if (bestResult.isEmpty()) {
            return null;
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        return quizResultMapper.toQuizResultResponse(bestResult.get(), quiz);
    }

    public List<QuizResultResponse> getStudentResultsInCourse(Long courseId, Long studentId) {
        List<QuizResult> results = quizResultRepository
                .findByStudentIdAndCourseId(studentId, courseId);

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, result.getQuiz()))
                .toList();
    }

    public List<QuizResultResponse> getAllQuizResults(Long quizId) {
        List<QuizResult> results = quizResultRepository
                .findByQuizIdOrderByScoreDesc(quizId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, quiz))
                .toList();
    }

    public List<QuizResultResponse> getAllStudentResults(Long studentId) {
        log.info("Getting all quiz results for student {}", studentId);
        
        List<QuizResult> results = quizResultRepository
                .findByStudentIdOrderByTakenAtDesc(studentId);

        log.info("Found {} quiz results for student {}", results.size(), studentId);

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, result.getQuiz()))
                .toList();
    }

    public QuizResultResponse getQuizResultDetail(Long resultId, Long currentUserId, com.app.lms.dto.auth.UserTokenInfo currentUser) {
        log.info("Getting quiz result detail {} for user {}", resultId, currentUserId);
        
        QuizResult result = quizResultRepository.findById(resultId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_RESULT_NOT_FOUND));

        // Security check: Students can only view their own results
        if (currentUser.getUserType() == com.app.lms.enums.UserType.STUDENT 
            && !result.getStudentId().equals(currentUserId)) {
            throw new AppException(ErroCode.ACCESS_DENIED);
        }

        Quiz quiz = result.getQuiz();
        if (quiz == null) {
            quiz = quizRepository.findById(result.getQuizId())
                    .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));
        }

        return quizResultMapper.toQuizResultResponse(result, quiz);
    }

    public boolean canTakeQuiz(Long quizId, Long studentId) {
        try {
            validateQuizPermission(quizId, studentId);
            return true;
        } catch (AppException e) {
            log.warn("Student {} cannot take quiz {}: {}", studentId, quizId, e.getMessage());
            return false;
        }
    }

    // Private helper methods (giữ nguyên logic cũ)
    private void validateQuizSubmission(SubmitQuizRequest request) {
        if (request.getQuizId() == null) {
            throw new AppException(ErroCode.QUIZ_NO_EXISTED);
        }
        if (request.getStudentId() == null) {
            throw new AppException(ErroCode.STUDENT_NOT_FOUND);
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new AppException(ErroCode.QUIZ_ANSWERS_REQUIRED);
        }
    }

    private void validateQuizPermission(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        Long courseId = quiz.getLesson().getCourseId();
        boolean isEnrolled = enrollmentRepository
                .existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE);

        if (!isEnrolled) {
            throw new AppException(ErroCode.NOT_ENROLLED);
        }

        if (quiz.getMaxAttempts() != null) {
            Integer attemptCount = quizResultRepository
                    .countAttemptsByQuizIdAndStudentId(quizId, studentId);

            if (attemptCount >= quiz.getMaxAttempts()) {
                throw new AppException(ErroCode.QUIZ_MAX_ATTEMPTS_REACHED);
            }
        }
    }

    private QuizGradingResult calculateScore(Quiz quiz, Map<Long, String> studentAnswers) {
        List<Question> questions = quiz.getQuestions();

        if (questions.isEmpty()) {
            throw new AppException(ErroCode.QUIZ_INVALID_ANSWER);
        }

        // Chỉ tính điểm cho questions có answer options
        List<Question> validQuestions = questions.stream()
                .filter(q -> q.getAnswerOptions() != null && !q.getAnswerOptions().isEmpty())
                .toList();

        double totalPossiblePoints = validQuestions.stream()
                .mapToDouble(Question::getPoints)
                .sum();

        // Nếu không có question nào có answer options
        if (validQuestions.isEmpty()) {
            return QuizGradingResult.builder()
                    .score(BigDecimal.ZERO)
                    .totalQuestions(questions.size())
                    .correctAnswers(0)
                    .feedback("Tất cả câu hỏi chưa có đáp án để chấm")
                    .build();
        }

        double earnedPoints = 0;
        int correctAnswers = 0;
        StringBuilder feedback = new StringBuilder();

        int questionNumber = 1; // Sử dụng counter thay vì orderIndex

        for (Question question : questions) {
            String studentAnswer = studentAnswers.get(question.getId());
            boolean isCorrect = false;

            // Kiểm tra question có answer options không
            if (question.getAnswerOptions() == null || question.getAnswerOptions().isEmpty()) {
                feedback.append("Câu ").append(questionNumber)
                        .append(": Chưa có đáp án để chấm\n");
                questionNumber++;
                continue; // Skip question này, không tính điểm
            }

            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                    question.getQuestionType() == QuestionType.TRUE_FALSE) {

                Optional<AnswerOption> correctOption = question.getAnswerOptions().stream()
                        .filter(AnswerOption::getIsCorrect)
                        .findFirst();

                // Kiểm tra student có trả lời không
                if (studentAnswer != null && correctOption.isPresent() &&
                        correctOption.get().getId().toString().equals(studentAnswer)) {
                    isCorrect = true;
                    earnedPoints += question.getPoints();
                    correctAnswers++;
                }

                // Tạo feedback chi tiết
                feedback.append("Câu ").append(questionNumber)
                        .append(": ");

                if (studentAnswer == null) {
                    feedback.append("Chưa trả lời");
                } else {
                    feedback.append(isCorrect ? "Đúng" : "Sai");

                    // Hiển thị đáp án đúng nếu sai
                    if (!isCorrect && correctOption.isPresent()) {
                        feedback.append(" (Đáp án đúng: ")
                                .append(correctOption.get().getAnswerText())
                                .append(")");
                    }
                }
                feedback.append("\n");

            } else if (question.getQuestionType() == QuestionType.SHORT_ANSWER) {
                feedback.append("Câu ").append(questionNumber)
                        .append(": Cần chấm thủ công\n");
            }

            questionNumber++;
        }

        // Tính điểm phần trăm dựa trên valid questions
        BigDecimal finalScore = BigDecimal.ZERO;
        if (totalPossiblePoints > 0) {
            finalScore = BigDecimal.valueOf(earnedPoints / totalPossiblePoints * 100)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return QuizGradingResult.builder()
                .score(finalScore)
                .totalQuestions(validQuestions.size()) // Chỉ count valid questions
                .correctAnswers(correctAnswers)
                .feedback(feedback.toString())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    private static class QuizGradingResult {
        private BigDecimal score;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private String feedback;
    }
}
