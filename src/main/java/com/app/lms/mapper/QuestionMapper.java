package com.app.lms.mapper;

import com.app.lms.dto.request.questionRequest.QuestionCreateRequest;
import com.app.lms.dto.request.questionRequest.QuestionUpdateRequest;
import com.app.lms.dto.response.QuestionResponse;
import com.app.lms.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface QuestionMapper {
    Question toQuestionMapper(QuestionCreateRequest request);

    @Mapping(source = "quiz.id", target = "quizId")
    QuestionResponse toQuestionResponse(Question question);

    void updateQuestion(@MappingTarget Question question, QuestionUpdateRequest request);
}
