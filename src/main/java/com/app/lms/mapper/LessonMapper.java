package com.app.lms.mapper;

import com.app.lms.dto.request.lessonRequest.LessonCreateRequest;
import com.app.lms.dto.request.lessonRequest.LessonUpdateRequest;
import com.app.lms.dto.response.LessonResponse;
import com.app.lms.dto.response.LessonSummaryResponse;
import com.app.lms.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface LessonMapper {
    Lesson toLessonMapper(LessonCreateRequest request);

    @Mapping(source = "course.id", target = "courseId")
    LessonResponse toLessonResponse(Lesson lesson);
    LessonSummaryResponse toLessonSummaryResponse(Lesson lesson);

    void updateLesson(@MappingTarget Lesson lesson, LessonUpdateRequest request);
}
