package com.app.lms.service;

import com.app.lms.dto.request.quizRequest.QuizCreateRequest;
import com.app.lms.dto.request.quizRequest.QuizUpdateRequest;
import com.app.lms.dto.response.QuizResponse;
import com.app.lms.entity.Lesson;
import com.app.lms.entity.Quiz;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.QuizMapper;
import com.app.lms.repository.LessonRepository;
import com.app.lms.repository.QuizRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {
    final QuizRepository quizRepository;
    final QuizMapper quizMapper;
    final LessonRepository lessonRepository;

    public QuizResponse createQuiz (QuizCreateRequest request){
        if(quizRepository.existsByTitle(request.getTitle())){
            throw new AppException(ErroCode.TITLE_EXISTED);
        }
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(()->new AppException(ErroCode.LESSON_NO_EXISTED));

        Quiz quiz = quizMapper.toQuizMapper(request);
        quiz.setLesson(lesson);

        return quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    public List<QuizResponse> getAllQuizzes(){
        return quizRepository.findAll()
                .stream()
                .map(quizMapper::toQuizResponse)
                .toList();
    }

    public QuizResponse getQuizById (Long quizId){
        return quizMapper.toQuizResponse(quizRepository.findById(quizId)
                .orElseThrow(()-> new AppException(ErroCode.QUIZ_NO_EXISTED)));
    }

    public QuizResponse updateQuiz (Long quizId, QuizUpdateRequest request){
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(()-> new AppException(ErroCode.QUIZ_NO_EXISTED));
        quizMapper.updateQuiz(quiz, request);
        return quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    public void deleteQuiz (Long quizId){
        quizRepository.deleteById(quizId);
    }

}
