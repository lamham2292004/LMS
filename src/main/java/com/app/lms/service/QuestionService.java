package com.app.lms.service;

import com.app.lms.dto.request.questionRequest.QuestionCreateRequest;
import com.app.lms.dto.request.questionRequest.QuestionUpdateRequest;
import com.app.lms.dto.response.QuestionResponse;
import com.app.lms.entity.Question;
import com.app.lms.entity.Quiz;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.QuestionMapper;
import com.app.lms.repository.QuestionRepository;
import com.app.lms.repository.QuizRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    final QuestionRepository questionRepository;
    final QuestionMapper questionMapper;
    final QuizRepository quizRepository;

    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        if (questionRepository.existsByQuestionText(request.getQuestionText())) {
            throw new AppException(ErroCode.TITLE_EXISTED);
        }
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(()-> new AppException(ErroCode.QUIZ_NO_EXISTED));
        Question question = questionMapper.toQuestionMapper(request);
        question.setQuiz(quiz);
        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    public QuestionResponse getQuestionById(Long questionId) {
        return questionMapper.toQuestionResponse(questionRepository
                .findById(questionId)
                .orElseThrow(()-> new AppException(ErroCode.QUESTION_NO_EXISTED)));
    }

    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
    }

    public QuestionResponse updateQuestion(Long questionId, QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(()-> new AppException(ErroCode.QUESTION_NO_EXISTED));
        questionMapper.updateQuestion(question,request);
        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findAll().stream()
                .filter(question -> question.getQuizId().equals(quizId))
                .map(questionMapper::toQuestionResponse)
                .toList();
    }
}
