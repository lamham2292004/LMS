package com.app.lms.service;

import com.app.lms.entity.*;
import com.app.lms.enums.EnrollmentStatus;
import com.app.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    //Phương thức này kiểm tra xem một sinh viên có đăng ký và đang ở trạng thái ACTIVE trong một khóa học cụ thể hay không.
    public boolean isStudentEnrolledInCourse(Long courseId, String userEmail) {
        try {
            // Tìm student qua email và check enrollment
            return enrollmentRepository.findAll().stream()
                    .anyMatch(enrollment ->
                                    enrollment.getCourseId().equals(courseId) &&
                                            enrollment.getStatus() == EnrollmentStatus.ACTIVE
                            // TODO: Add student email check when you have student service
                    );
        } catch (Exception e) {
            log.error("Error checking student enrollment: {}", e.getMessage());
            return false;
        }
    }
    //Phương thức này có nhiệm vụ kiểm tra xem giảng viên có sở hữu khóa học hay không.
    public boolean isLecturerOwnsCourse(Long courseId, String userEmail) {
        try {
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) return false;

            // TODO: Implement proper lecturer email check with teacherId
            // Hiện tại return true cho development
            return true;
        } catch (Exception e) {
            log.error("Error checking lecturer ownership: {}", e.getMessage());
            return false;
        }
    }

    // Lesson authorization methods
    //Kiểm tra xem một giảng viên (được xác định bởi userEmail) có được phép tạo một bài giảng trong một khóa học (courseId) hay không.
    public boolean canLecturerCreateLessonInCourse(Long courseId, String userEmail) {
        return isLecturerOwnsCourse(courseId, userEmail);
    }
    //Kiểm tra xem một sinh viên có được phép truy cập một bài giảng (lessonId) cụ thể hay không.
    public boolean canStudentAccessLesson(Long lessonId, String userEmail) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isEmpty()) return false;

            return isStudentEnrolledInCourse(lesson.get().getCourseId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking lesson access: {}", e.getMessage());
            return false;
        }
    }
    //Kiểm tra xem một giảng viên có được phép chỉnh sửa một bài giảng (lessonId) hay không
    public boolean canLecturerEditLesson(Long lessonId, String userEmail) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isEmpty()) return false;

            return isLecturerOwnsCourse(lesson.get().getCourseId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking lesson edit permission: {}", e.getMessage());
            return false;
        }
    }

    // Quiz authorization methods
    //Kiểm tra xem một giảng viên có được phép tạo một bài trắc nghiệm trong một bài giảng (lessonId) hay không.
    public boolean canLecturerCreateQuizInLesson(Long lessonId, String userEmail) {
        return canLecturerEditLesson(lessonId, userEmail);
    }
    //Kiểm tra xem một sinh viên có được phép truy cập một bài trắc nghiệm (quizId) hay không.
    public boolean canStudentAccessQuiz(Long quizId, String userEmail) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;

            return canStudentAccessLesson(quiz.get().getLessonId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking quiz access: {}", e.getMessage());
            return false;
        }
    }
    // kiếm tra xem giảng viên có được sửa 1 bài trắc nghiệm hay không
    public boolean canLecturerEditQuiz(Long quizId, String userEmail) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;

            return canLecturerCreateQuizInLesson(quiz.get().getLessonId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking quiz edit permission: {}", e.getMessage());
            return false;
        }
    }

    // Question authorization methods
    // Kiểm tra xem một giảng viên có được phép tạo một câu hỏi trong một bài trắc nghiệm (quizId) hay không.
    public boolean canLecturerCreateQuestion(Long quizId, String userEmail) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;

            return canLecturerCreateQuizInLesson(quiz.get().getLessonId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking question creation permission: {}", e.getMessage());
            return false;
        }
    }
    //Kiểm tra xem một sinh viên có được phép xem một câu hỏi (questionId) hay không.
    public boolean canStudentViewQuestion(Long questionId, String userEmail) {
        try {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()) return false;

            return canStudentAccessQuiz(question.get().getQuizId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking question view permission: {}", e.getMessage());
            return false;
        }
    }
    // kiếm tra xem giảng viên có được sửa 1 câu hỏi hay không
    public boolean canLecturerEditQuestion(Long questionId, String userEmail) {
        try {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()) return false;

            return canLecturerCreateQuizInLesson(question.get().getQuizId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking question edit permission: {}", e.getMessage());
            return false;
        }
    }

    // AnswerOption authorization methods
    //Kiểm tra xem một giảng viên có được phép tạo một đáp án trong một câu hỏi (questionId) hay không.
    public boolean canLecturerCreateAnswerOption(Long questionId, String userEmail) {
        try {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isEmpty()) return false;

            return canLecturerCreateQuestion(question.get().getQuizId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking answer option creation permission: {}", e.getMessage());
            return false;
        }
    }
    //Kiểm tra xem một sinh viên có được phép xem một đáp án
    public boolean canStudentViewAnswerOption(Long answerOptionId, String userEmail) {
        try {
            Optional<AnswerOption> answerOption = answerOptionRepository.findById(answerOptionId);
            if (answerOption.isEmpty()) return false;

            return canStudentViewQuestion(answerOption.get().getQuestionId(), userEmail);
        } catch (Exception e) {
            log.error("Error checking answer option view permission: {}", e.getMessage());
            return false;
        }
    }
    // kiếm tra xem giảng viên có được sửa đáp án câu hỏi hay không
    public boolean canLecturerEditAnswerOption(Long answerOptionId, String userEmail) {
        try {
            Optional<AnswerOption> answerOption = answerOptionRepository.findById(answerOptionId);
            if (answerOption.isEmpty()) return false;
            return canLecturerEditQuestion(answerOption.get().getQuestionId(), userEmail);
        }catch (Exception e){
            log.error("Error checking answer option view permission: {}", e.getMessage());
            return false;
        }
    }

    public boolean canStudentViewQuestionAnswers(Long questionId, String userEmail) {
        return canStudentViewQuestion(questionId, userEmail);
    }

    public boolean canStudentViewQuizQuestions(Long quizId, String userEmail) {
        return canStudentAccessQuiz(quizId, userEmail);
    }

    public boolean canStudentAccessLessonQuizzes(Long lessonId, String userEmail) {
        return canStudentAccessLesson(lessonId, userEmail);
    }
}
