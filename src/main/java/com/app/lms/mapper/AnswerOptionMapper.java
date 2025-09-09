package com.app.lms.mapper;

import com.app.lms.dto.request.answerOptionRequest.AnswerOptionCreateRequest;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionUpdateRequest;
import com.app.lms.dto.response.AnswerOptionResponse;
import com.app.lms.entity.AnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface AnswerOptionMapper {
    AnswerOption toAnswerOptionMapper(AnswerOptionCreateRequest request);

    @Mapping(source = "question.id", target = "questionId")
    AnswerOptionResponse toAnswerOptionResponse(AnswerOption answerOption);

    void updateAnswerOption (@MappingTarget AnswerOption answerOption, AnswerOptionUpdateRequest request);
}
