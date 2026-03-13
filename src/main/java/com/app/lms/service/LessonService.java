package com.app.lms.service;

import java.util.List;

import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

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
import com.app.lms.util.YouTubeUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {
    final LessonRepository lessonRepository;
    final LessonMapper lessonMapper;
    final CourseRepository courseRepository;

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public LessonResponse createLesson(LessonCreateRequest request) {
        if (lessonRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErroCode.TITLE_EXISTED);
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

        // Validate YouTube URL nếu có
        if (request.getYoutubeUrl() != null && !request.getYoutubeUrl().isBlank()) {
            if (!YouTubeUtils.isValidYoutubeUrl(request.getYoutubeUrl())) {
                throw new AppException(ErroCode.INVALID_YOUTUBE_URL);
            }
        }

        Lesson lesson = lessonMapper.toLessonMapper(request);
        lesson.setCourse(course);

        if (lesson.getStatus() == null) {
            lesson.setStatus(com.app.lms.enums.LessonStatus.UPCOMING);
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        return buildLessonResponse(savedLesson);
    }

    @Cacheable(value = "lessons", key = "'all'")
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll()
                .stream()
                .map(this::buildLessonResponse)
                .toList();
    }

    @Cacheable(value = "lessons", key = "#lessonId")
    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErroCode.LESSON_NO_EXISTED));
        return buildLessonResponse(lesson);
    }

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })
    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErroCode.LESSON_NO_EXISTED));

        // Validate YouTube URL nếu có
        if (request.getYoutubeUrl() != null && !request.getYoutubeUrl().isBlank()) {
            if (!YouTubeUtils.isValidYoutubeUrl(request.getYoutubeUrl())) {
                throw new AppException(ErroCode.INVALID_YOUTUBE_URL);
            }
        }

        lessonMapper.updateLesson(lesson, request);

        Lesson savedLesson = lessonRepository.save(lesson);
        return buildLessonResponse(savedLesson);
    }

    @Caching(evict = {
            @CacheEvict(value = "lessons", allEntries = true),
            @CacheEvict(value = "courses", allEntries = true)
    })

    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErroCode.LESSON_NO_EXISTED));

        // Không được phép xóa bài giảng đang mở
        if (lesson.getStatus() == com.app.lms.enums.LessonStatus.OPEN) {
            throw new AppException(ErroCode.LESSON_CANNOT_DELETE_OPEN);
        }

        lessonRepository.deleteById(lessonId);
    }

    @Cacheable(value = "lessons", key = "'course_' + #courseId")
    public List<LessonResponse> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .map(this::buildLessonResponse)
                .toList();
    }

    private LessonResponse buildLessonResponse(Lesson lesson) {
        LessonResponse response = lessonMapper.toLessonResponse(lesson);
        // Tạo embed URL từ YouTube URL
        if (lesson.getYoutubeUrl() != null && !lesson.getYoutubeUrl().isBlank()) {
            response.setYoutubeEmbedUrl(YouTubeUtils.toEmbedUrl(lesson.getYoutubeUrl()));
        }
        return response;
    }
}
