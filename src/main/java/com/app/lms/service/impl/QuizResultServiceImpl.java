package com.app.lms.service.impl;

import com.app.lms.dto.request.quizResultRequest.SubmitQuizRequest;
import com.app.lms.dto.response.QuizResultResponse;
import com.app.lms.entity.AnswerOption;
import com.app.lms.entity.Question;
import com.app.lms.entity.Quiz;
import com.app.lms.entity.QuizResult;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.enums.QuestionType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.QuizResultMapper;
import com.app.lms.repository.EnrollmentRepository;
import com.app.lms.repository.QuizRepository;
import com.app.lms.repository.QuizResultRepository;
import com.app.lms.service.QuizResultService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class QuizResultServiceImpl implements QuizResultService {
    QuizResultRepository quizResultRepository;
    QuizRepository quizRepository;
    EnrollmentRepository enrollmentRepository;
    QuizResultMapper quizResultMapper;

    @Override
    public QuizResultResponse submitQuiz(SubmitQuizRequest request) {
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
        savedResult.setQuiz(quiz); // Set để mapper có thể access

        // 8. Log kết quả
        log.info("Student {} completed quiz {} with score {}",
                request.getStudentId(), request.getQuizId(), gradingResult.getScore());

        // 9. Trả về response sử dụng mapper
        return quizResultMapper.toQuizResultResponse(
                savedResult, quiz, null, gradingResult.getFeedback());
    }

    @Override
    public List<QuizResultResponse> getQuizResultsByStudentAndQuiz(Long quizId, Long studentId) {
        List<QuizResult> results = quizResultRepository
                .findByQuizIdAndStudentIdOrderByAttemptNumberDesc(quizId, studentId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, quiz))
                .toList();
    }

    @Override
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

    @Override
    public List<QuizResultResponse> getStudentResultsInCourse(Long courseId, Long studentId) {
        List<QuizResult> results = quizResultRepository
                .findByStudentIdAndCourseId(studentId, courseId);

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, result.getQuiz()))
                .toList();
    }

    @Override
    public List<QuizResultResponse> getAllQuizResults(Long quizId) {
        List<QuizResult> results = quizResultRepository
                .findByQuizIdOrderByScoreDesc(quizId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        return results.stream()
                .map(result -> quizResultMapper.toQuizResultResponse(result, quiz))
                .toList();
    }

    @Override
    public boolean canTakeQuiz(Long quizId, Long studentId) {
        try {
            validateQuizPermission(quizId, studentId);
            return true;
        } catch (AppException e) {
            log.warn("Student {} cannot take quiz {}: {}", studentId, quizId, e.getMessage());
            return false;
        }
    }

    // Private helper methods
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
        // 1. Check quiz exists
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));

        // 2. Check enrollment
        Long courseId = quiz.getLesson().getCourseId();
        boolean isEnrolled = enrollmentRepository
                .existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE);

        if (!isEnrolled) {
            throw new AppException(ErroCode.NOT_ENROLLED);
        }

        // 3. Check attempt limit
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

        double totalPossiblePoints = questions.stream()
                .mapToDouble(Question::getPoints)
                .sum();

        double earnedPoints = 0;
        int correctAnswers = 0;
        StringBuilder feedback = new StringBuilder();

        for (Question question : questions) {
            String studentAnswer = studentAnswers.get(question.getId());
            boolean isCorrect = false;

            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                    question.getQuestionType() == QuestionType.TRUE_FALSE) {

                Optional<AnswerOption> correctOption = question.getAnswerOptions().stream()
                        .filter(AnswerOption::getIsCorrect)
                        .findFirst();

                if (correctOption.isPresent() &&
                        correctOption.get().getId().toString().equals(studentAnswer)) {
                    isCorrect = true;
                    earnedPoints += question.getPoints();
                    correctAnswers++;
                }

                feedback.append("Câu ").append(question.getOrderIndex() != null ? question.getOrderIndex() : "?")
                        .append(": ").append(isCorrect ? "Đúng" : "Sai");

                if (!isCorrect && correctOption.isPresent()) {
                    feedback.append(" (Đáp án đúng: ").append(correctOption.get().getAnswerText()).append(")");
                }
                feedback.append("\n");

            } else if (question.getQuestionType() == QuestionType.SHORT_ANSWER) {
                feedback.append("Câu ").append(question.getOrderIndex() != null ? question.getOrderIndex() : "?")
                        .append(": Cần chấm thủ công\n");
            }
        }

        // Tính điểm phần trăm
        BigDecimal finalScore = BigDecimal.ZERO;
        if (totalPossiblePoints > 0) {
            finalScore = BigDecimal.valueOf(earnedPoints / totalPossiblePoints * 100)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return QuizGradingResult.builder()
                .score(finalScore)
                .totalQuestions(questions.size())
                .correctAnswers(correctAnswers)
                .feedback(feedback.toString())
                .build();
    }

    // Helper class cho kết quả chấm điểm
    @lombok.Data
    @lombok.Builder
    private static class QuizGradingResult {
        private BigDecimal score;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private String feedback;
    }
}
