package com.app.lms.service;

import com.app.lms.dto.request.answerOptionRequest.AnswerOptionCreateRequest;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionUpdateRequest;
import com.app.lms.dto.response.AnswerOptionResponse;
import com.app.lms.entity.AnswerOption;
import com.app.lms.entity.Question;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.AnswerOptionMapper;
import com.app.lms.repository.AnswerOptionRepository;
import com.app.lms.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerOptionService {
    final AnswerOptionRepository answerOptionRepository;
    final QuestionRepository questionRepository;
    final AnswerOptionMapper answerOptionMapper;

    public AnswerOptionResponse createAnswerOption (AnswerOptionCreateRequest request){
        if (answerOptionRepository.existsByAnswerText(request.getAnswerText())) {
            throw new AppException(ErroCode.TITLE_EXISTED);
        }
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(()-> new AppException(ErroCode.QUESTION_NO_EXISTED));
        AnswerOption answerOption = answerOptionMapper.toAnswerOptionMapper(request);
        answerOption.setQuestion(question);
        return answerOptionMapper.toAnswerOptionResponse(answerOptionRepository.save(answerOption));
    }

    public List<AnswerOptionResponse> getAllAnswerOptions() {
        return answerOptionRepository
                .findAll()
                .stream()
                .map(answerOptionMapper::toAnswerOptionResponse)
                .toList();
    }

    public AnswerOptionResponse getAnswerOptionById (Long answerOptionId) {
        return answerOptionMapper.toAnswerOptionResponse(answerOptionRepository
                .findById(answerOptionId)
                .orElseThrow(()-> new AppException(ErroCode.ANSWER_OPTION_NO_EXISTED)));
    }

    public AnswerOptionResponse updateAnswerOption (Long answerOptionId, AnswerOptionUpdateRequest request){
        AnswerOption answerOption = answerOptionRepository.findById(answerOptionId)
                .orElseThrow(() -> new AppException(ErroCode.ANSWER_OPTION_NO_EXISTED));
        answerOptionMapper.updateAnswerOption(answerOption,request);
        return answerOptionMapper.toAnswerOptionResponse(answerOptionRepository.save(answerOption));
    }

    public void deleteAnswerOption (Long answerOptionId) {
        if (!answerOptionRepository.existsById(answerOptionId)) {
            throw new AppException(ErroCode.ANSWER_OPTION_NO_EXISTED);
        }
        answerOptionRepository.deleteById(answerOptionId);
    }

    public List<AnswerOptionResponse> getAnswerOptionsByQuestionId(Long questionId) {
        return answerOptionRepository.findAll().stream()
                .filter(option -> option.getQuestionId().equals(questionId))
                .map(answerOptionMapper::toAnswerOptionResponse)
                .toList();
    }
}
