package com.app.lms.mapper;

import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface CourseMapper {

    Course toCourseMapper(CourseCreateRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name",target = "categoryName")
    CourseResponse toCourseResponse(Course course);

    void updateCourse(@MappingTarget Course course, CourseUpdateRequest request);
}
