package com.app.lms.service;

import com.app.lms.dto.request.lessonProgressRequest.LessonProgressRequest;
import com.app.lms.dto.response.CourseProgressResponse;
import com.app.lms.dto.response.LessonProgressResponse;
import com.app.lms.entity.Lesson;
import com.app.lms.entity.LessonProgress;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.repository.EnrollmentRepository;
import com.app.lms.repository.LessonProgressRepository;
import com.app.lms.repository.LessonRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class LessonProgressService {
    LessonProgressRepository lessonProgressRepository;
    LessonRepository lessonRepository;
    EnrollmentRepository enrollmentRepository;

    public LessonProgressResponse saveProgress(Long studentId, LessonProgressRequest request) {
        // Validate bài học tồn tại
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErroCode.LESSON_NO_EXISTED));

        // Validate học viên đã đăng ký khóa học chứa bài học này
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, lesson.getCourseId(),
                com.app.lms.enums.EnrollmentStatus.ACTIVE);
        if (!isEnrolled) {
            throw new AppException(ErroCode.NOT_ENROLLED);
        }

        // Upsert: tìm record cũ hoặc tạo mới
        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, request.getLessonId())
                .orElse(LessonProgress.builder()
                        .studentId(studentId)
                        .lessonId(request.getLessonId())
                        .build());

        // Cập nhật tiến độ — chỉ cập nhật nếu watchedSeconds mới > cũ (tránh tụt tiến
        // độ)
        if (request.getWatchedSeconds() > progress.getWatchedSeconds()) {
            progress.setWatchedSeconds(request.getWatchedSeconds());
        }

        progress.setTotalSeconds(request.getTotalSeconds());
        progress.setLastPosition(request.getLastPosition());

        // Tự động đánh dấu hoàn thành khi xem >= 90%
        if (progress.getTotalSeconds() != null && progress.getTotalSeconds() > 0) {
            double percent = (double) progress.getWatchedSeconds() / progress.getTotalSeconds() * 100;
            if (percent >= 90.0) {
                progress.setCompleted(true);
            }
        }

        LessonProgress saved = lessonProgressRepository.save(progress);

        log.info("Progress saved: student={}, lesson={}, watched={}s/{}s, completed={}",
                studentId, request.getLessonId(),
                saved.getWatchedSeconds(), saved.getTotalSeconds(), saved.getCompleted());

        return toResponse(saved, lesson.getTitle());
    }

    /**
     * Lấy tiến độ 1 bài học (để resume video).
     */
    @Transactional(readOnly = true)
    public LessonProgressResponse getProgressByLesson(Long studentId, Long lessonId) {
        // Validate bài học tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErroCode.LESSON_NO_EXISTED));

        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);

        if (progress == null) {
            // Chưa xem bài này → trả về progress = 0
            return LessonProgressResponse.builder()
                    .lessonId(lessonId)
                    .lessonTitle(lesson.getTitle())
                    .studentId(studentId)
                    .watchedSeconds(0)
                    .totalSeconds(lesson.getDuration() != null ? lesson.getDuration() * 60 : 0)
                    .lastPosition(0)
                    .progressPercent(0.0)
                    .completed(false)
                    .build();
        }

        return toResponse(progress, lesson.getTitle());
    }

    /**
     * Lấy tổng tiến độ khóa học (bao nhiêu bài đã hoàn thành, % tổng).
     */
    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(Long studentId, Long courseId) {
        // Lấy tất cả bài học trong khóa
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        int totalLessons = lessons.size();

        // Lấy tiến độ của student cho các bài trong khóa
        List<LessonProgress> progresses = lessonProgressRepository
                .findByStudentIdAndLesson_CourseId(studentId, courseId);

        // Map lessonId → progress để lookup nhanh
        Map<Long, LessonProgress> progressMap = progresses.stream()
                .collect(Collectors.toMap(LessonProgress::getLessonId, p -> p));

        // Build chi tiết từng bài
        List<LessonProgressResponse> lessonProgresses = lessons.stream()
                .map(lesson -> {
                    LessonProgress p = progressMap.get(lesson.getId());
                    if (p != null) {
                        return toResponse(p, lesson.getTitle());
                    }
                    // Chưa xem
                    return LessonProgressResponse.builder()
                            .lessonId(lesson.getId())
                            .lessonTitle(lesson.getTitle())
                            .studentId(studentId)
                            .watchedSeconds(0)
                            .totalSeconds(lesson.getDuration() != null ? lesson.getDuration() * 60 : 0)
                            .lastPosition(0)
                            .progressPercent(0.0)
                            .completed(false)
                            .build();
                })
                .collect(Collectors.toList());

        int completedLessons = (int) lessonProgresses.stream()
                .filter(lp -> Boolean.TRUE.equals(lp.getCompleted()))
                .count();

        double overallPercent = totalLessons > 0
                ? (double) completedLessons / totalLessons * 100
                : 0.0;

        // Lấy tên khóa học
        String courseName = lessons.isEmpty() ? "" : lessons.get(0).getCourse().getTitle();

        return CourseProgressResponse.builder()
                .courseId(courseId)
                .courseName(courseName)
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .overallProgressPercent(Math.round(overallPercent * 100.0) / 100.0)
                .lessonProgresses(lessonProgresses)
                .build();
    }

    // ===== Helper =====

    private LessonProgressResponse toResponse(LessonProgress progress, String lessonTitle) {
        double percent = 0.0;
        if (progress.getTotalSeconds() != null && progress.getTotalSeconds() > 0) {
            percent = (double) progress.getWatchedSeconds() / progress.getTotalSeconds() * 100;
            percent = Math.min(percent, 100.0); // cap tại 100%
            percent = Math.round(percent * 100.0) / 100.0; // 2 decimal
        }

        return LessonProgressResponse.builder()
                .id(progress.getId())
                .lessonId(progress.getLessonId())
                .lessonTitle(lessonTitle)
                .studentId(progress.getStudentId())
                .watchedSeconds(progress.getWatchedSeconds())
                .totalSeconds(progress.getTotalSeconds())
                .lastPosition(progress.getLastPosition())
                .progressPercent(percent)
                .completed(progress.getCompleted())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
