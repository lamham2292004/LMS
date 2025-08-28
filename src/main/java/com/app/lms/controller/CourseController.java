package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseController {
    private final CourseService courseService;

    @PostMapping(value = "/createCourse", consumes = {"multipart/form-data"})
    ApiResponse<CourseResponse> createCourse(@Valid @RequestPart("course") CourseCreateRequest request,
                                             @RequestPart("file")MultipartFile file) {

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(courseService.createCourse(request,file));

        return apiResponse;
    }

    @GetMapping("/{courseId}")
    ApiResponse<CourseResponse> getCourse(@Valid @PathVariable("courseId") Long courseId) {

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(courseService.getCourseById(courseId));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<CourseResponse>> getAllCourse() {

        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();

        apiResponse.setResult(courseService.getAllCourses());

        return apiResponse;
    }

    @PutMapping("/updateCourse/{courseId}")
    ApiResponse<CourseResponse> updateCourse(@PathVariable Long courseId,@Valid @RequestBody CourseUpdateRequest request) {

        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(courseService.updateCourse(courseId,request));

        return apiResponse;
    }

    @DeleteMapping("/{courseId}")
    String deleteCourse(@PathVariable Long courseId) {

        courseService.deleteCourse(courseId);

        return "Course deleted";
    }

}
