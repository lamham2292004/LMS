package com.app.lms.service;

import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.entity.*;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service("authorizationService")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    // Course authorization methods
    public boolean isStudentEnrolledInCourse(Long courseId, String userIdStr) {
        try {
            Long studentId = Long.parseLong(userIdStr);

            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                    studentId, courseId, EnrollmentStatus.ACTIVE);

            log.debug("Enrollment check: studentId={}, courseId={}, enrolled={}",
                    studentId, courseId, enrolled);
            return enrolled;

        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userIdStr);
            return false;
        } catch (Exception e) {
            log.error("Error checking enrollment: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLecturerOwnsCourse(Long courseId, String userIdStr) {
        try {
            Long lecturerId = Long.parseLong(userIdStr);

            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                log.warn("Course not found: {}", courseId);
                return false;
            }

            boolean owns = course.get().getTeacherId().equals(lecturerId);
            log.debug("Course ownership check: lecturerId={}, courseId={}, owns={}",
                    lecturerId, courseId, owns);
            return owns;

        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userIdStr);
            return false;
        } catch (Exception e) {
            log.error("Error checking course ownership: {}", e.getMessage());
            return false;
        }
    }

    // Lesson methods
    public boolean canLecturerCreateLessonInCourse(Long courseId, String userIdStr) {
        return isLecturerOwnsCourse(courseId, userIdStr);
    }

    public boolean canStudentAccessLesson(Long lessonId, String userIdStr) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isEmpty()) return false;

            return isStudentEnrolledInCourse(lesson.get().getCourseId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking lesson access: {}", e.getMessage());
            return false;
        }
    }

    public boolean canLecturerEditLesson(Long lessonId, String userIdStr) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isEmpty()) return false;

            return isLecturerOwnsCourse(lesson.get().getCourseId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking lesson edit permission: {}", e.getMessage());
            return false;
        }
    }

    // Quiz methods
    public boolean canLecturerCreateQuizInLesson(Long lessonId, String userIdStr) {
        return canLecturerEditLesson(lessonId, userIdStr);
    }

    public boolean canStudentAccessQuiz(Long quizId, String userIdStr) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;

            return canStudentAccessLesson(quiz.get().getLessonId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking quiz access: {}", e.getMessage());
            return false;
        }
    }

    public boolean canLecturerEditQuiz(Long quizId, String userIdStr) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;

            return canLecturerCreateQuizInLesson(quiz.get().getLessonId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking quiz edit permission: {}", e.getMessage());
            return false;
        }
    }

    // Question methods
    public boolean canLecturerCreateQuestion(Long quizId, String userIdStr) {
        return canLecturerEditQuiz(quizId, userIdStr);
    }

    public boolean canStudentViewQuestion(Long questionId, String userIdStr) {
        try {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()) return false;

            return canStudentAccessQuiz(question.get().getQuizId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking question access: {}", e.getMessage());
            return false;
        }
    }

    public boolean canLecturerEditQuestion(Long questionId, String userIdStr) {
        try {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()) return false;

            return canLecturerCreateQuestion(question.get().getQuizId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking question edit permission: {}", e.getMessage());
            return false;
        }
    }

    // AnswerOption methods
    public boolean canLecturerCreateAnswerOption(Long questionId, String userIdStr) {
        return canLecturerEditQuestion(questionId, userIdStr);
    }

    public boolean canStudentViewAnswerOption(Long answerOptionId, String userIdStr) {
        try {
            Optional<AnswerOption> answerOption = answerOptionRepository.findById(answerOptionId);
            if (answerOption.isEmpty()) return false;

            return canStudentViewQuestion(answerOption.get().getQuestionId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking answer option access: {}", e.getMessage());
            return false;
        }
    }

    public boolean canLecturerEditAnswerOption(Long answerOptionId, String userIdStr) {
        try {
            Optional<AnswerOption> answerOption = answerOptionRepository.findById(answerOptionId);
            if (answerOption.isEmpty()) return false;

            return canLecturerEditQuestion(answerOption.get().getQuestionId(), userIdStr);
        } catch (Exception e) {
            log.error("Error checking answer option edit permission: {}", e.getMessage());
            return false;
        }
    }

    // Convenience methods
    public boolean canStudentViewQuestionAnswers(Long questionId, String userIdStr) {
        return canStudentViewQuestion(questionId, userIdStr);
    }

    public boolean canStudentViewQuizQuestions(Long quizId, String userIdStr) {
        return canStudentAccessQuiz(quizId, userIdStr);
    }

    public boolean canStudentAccessLessonQuizzes(Long lessonId, String userIdStr) {
        return canStudentAccessLesson(lessonId, userIdStr);
    }

    public boolean canStudentAccessCourseQuizzes(Long courseId, String userIdStr) {
        return isStudentEnrolledInCourse(courseId, userIdStr);
    }

    // Enrollment validation
    public boolean validateEnrollmentAccess(Long enrollmentId, String userIdStr) {
        try {
            Long userId = Long.parseLong(userIdStr);
            Optional<Enrollment> enrollment = enrollmentRepository.findById(enrollmentId);

            if (enrollment.isEmpty()) return false;

            return enrollment.get().getStudentId().equals(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userIdStr);
            return false;
        } catch (Exception e) {
            log.error("Error checking enrollment access: {}", e.getMessage());
            return false;
        }
    }
}
