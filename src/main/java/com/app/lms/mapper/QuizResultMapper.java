package com.app.lms.mapper;

import com.app.lms.dto.response.QuizResultResponse;
import com.app.lms.entity.Quiz;
import com.app.lms.entity.QuizResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface QuizResultMapper {
    @Mapping(source = "quizResult.id", target = "id")
    @Mapping(source = "quizResult.quizId", target = "quizId")
    @Mapping(source = "quiz.title", target = "quizTitle")
    @Mapping(source = "quizResult.studentId", target = "studentId")
    @Mapping(source = "studentName", target = "studentName")
    @Mapping(source = "feedback", target = "feedback")
    QuizResultResponse toQuizResultResponse(
            QuizResult quizResult,
            Quiz quiz,
            String studentName,
            String feedback
    );
    @Mapping(source = "quizResult.id", target = "id")
    @Mapping(source = "quizResult.quizId", target = "quizId")
    @Mapping(source = "quiz.title", target = "quizTitle")
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    QuizResultResponse toQuizResultResponse(QuizResult quizResult, Quiz quiz);
}
