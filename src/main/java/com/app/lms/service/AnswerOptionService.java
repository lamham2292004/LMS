package com.app.lms.service;

import com.app.lms.dto.request.answerOptionRequest.AnswerOptionCreateRequest;
import com.app.lms.dto.request.answerOptionRequest.AnswerOptionUpdateRequest;
import com.app.lms.dto.response.AnswerOptionResponse;
import com.app.lms.entity.AnswerOption;
import com.app.lms.entity.Question;
import com.app.lms.enums.QuestionType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.AnswerOptionMapper;
import com.app.lms.repository.AnswerOptionRepository;
import com.app.lms.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerOptionService {
    final AnswerOptionRepository answerOptionRepository;
    final QuestionRepository questionRepository;
    final AnswerOptionMapper answerOptionMapper;

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public AnswerOptionResponse createAnswerOption (AnswerOptionCreateRequest request){
        if (answerOptionRepository.existsByAnswerTextAndQuestionId(request.getAnswerText(), request.getQuestionId())) {
            throw new AppException(ErroCode.TITLE_EXISTED);
        }
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(()->new AppException(ErroCode.QUESTION_NO_EXISTED));

        // Validate số lượng đáp án theo loại câu hỏi
        List<AnswerOption> existingOptions = answerOptionRepository.findByQuestionIdOrderByOrderIndex(question.getId());
        if (question.getQuestionType() == QuestionType.TRUE_FALSE && existingOptions.size() >= 2) {
            throw new AppException(ErroCode.ANSWER_OPTION_LIMIT_EXCEEDED);
        }

        // Validate chỉ cho phép 1 đáp án đúng cho MULTIPLE_CHOICE và TRUE_FALSE
        if (Boolean.TRUE.equals(request.getIsCorrect())) {
            boolean hasCorrectAnswer = existingOptions.stream()
                    .anyMatch(opt -> Boolean.TRUE.equals(opt.getIsCorrect()));
            if (hasCorrectAnswer && (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE
                    || question.getQuestionType() == QuestionType.TRUE_FALSE)) {
                throw new AppException(ErroCode.MULTIPLE_CORRECT_ANSWERS_NOT_ALLOWED);
            }
        }

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

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public AnswerOptionResponse updateAnswerOption (Long answerOptionId, AnswerOptionUpdateRequest request){
        AnswerOption answerOption = answerOptionRepository.findById(answerOptionId)
                .orElseThrow(() -> new AppException(ErroCode.ANSWER_OPTION_NO_EXISTED));
        answerOptionMapper.updateAnswerOption(answerOption,request);
        return answerOptionMapper.toAnswerOptionResponse(answerOptionRepository.save(answerOption));
    }

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public void deleteAnswerOption (Long answerOptionId) {
        if (!answerOptionRepository.existsById(answerOptionId)) {
            throw new AppException(ErroCode.ANSWER_OPTION_NO_EXISTED);
        }
        answerOptionRepository.deleteById(answerOptionId);
    }

    public List<AnswerOptionResponse> getAnswerOptionsByQuestionId(Long questionId) {
        return answerOptionRepository.findByQuestionIdOrderByOrderIndex(questionId).stream()
                .map(answerOptionMapper::toAnswerOptionResponse)
                .toList();
    }
}
