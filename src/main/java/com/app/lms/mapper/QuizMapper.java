package com.app.lms.mapper;

import com.app.lms.dto.request.quizRequest.QuizCreateRequest;
import com.app.lms.dto.request.quizRequest.QuizUpdateRequest;
import com.app.lms.dto.response.QuizResponse;
import com.app.lms.entity.Quiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface QuizMapper {
    Quiz toQuizMapper(QuizCreateRequest request);

    @Mapping(source = "lesson.id", target = "lessonId")
    QuizResponse toQuizResponse(Quiz quiz);

    void updateQuiz(@MappingTarget Quiz quiz, QuizUpdateRequest request);
}
