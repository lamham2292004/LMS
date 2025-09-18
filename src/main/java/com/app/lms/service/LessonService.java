package com.app.lms.service;

import com.app.lms.dto.request.lessonRequest.LessonCreateRequest;
import com.app.lms.dto.request.lessonRequest.LessonUpdateRequest;
import com.app.lms.dto.response.LessonResponse;
import com.app.lms.entity.Course;
import com.app.lms.entity.Lesson;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.LessonMapper;
import com.app.lms.repository.CourseRepository;
import com.app.lms.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {
    final LessonRepository lessonRepository;
    final LessonMapper lessonMapper;
    final FileUploadService fileUploadService;
    final CourseRepository courseRepository;

    public LessonResponse createLesson(LessonCreateRequest request, MultipartFile videoFile) {
        if (lessonRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErroCode.TITLE_EXISTED);
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(()-> new AppException(ErroCode.COURSE_NO_EXISTED));

        Lesson lesson = lessonMapper.toLessonMapper(request);

        lesson.setCourse(course);

        // Nếu có file video thì upload
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String videoPath = fileUploadService.saveLessonVideo(videoFile);
                lesson.setVideoPath(videoPath); // giả sử trong entity Lesson có field `video`
            } catch (IOException e) {
                throw new AppException(ErroCode.FILE_EXISTED); // bạn có thể thêm enum mới
            }
        }

        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll()
                .stream()
                .map(lessonMapper::toLessonResponse)
                .toList();
    }


    public LessonResponse getLessonById(Long lessonId) {
        return lessonMapper.toLessonResponse(lessonRepository.findById(lessonId)
                .orElseThrow(()-> new AppException(ErroCode.LESSON_NO_EXISTED)));
    }

    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(()-> new AppException(ErroCode.LESSON_NO_EXISTED));
        lessonMapper.updateLesson(lesson,request);
        return lessonMapper.toLessonResponse(lessonRepository.save(lesson));
    }

    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new AppException(ErroCode.LESSON_NO_EXISTED);
        }
        lessonRepository.deleteById(lessonId);
    }

    public List<LessonResponse> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(lessonMapper::toLessonResponse)
                .toList();
    }
}
