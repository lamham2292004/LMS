package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.lessonRequest.LessonCreateRequest;
import com.app.lms.dto.request.lessonRequest.LessonUpdateRequest;
import com.app.lms.dto.response.LessonResponse;
import com.app.lms.service.LessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonController {
    private final LessonService lessonService;

    @PostMapping(value = "/createLesson", consumes = {"multipart/form-data"})
    public ApiResponse<LessonResponse> createLesson(
            @RequestPart("lesson") @Valid LessonCreateRequest request,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.createLesson(request, video));
        return apiResponse;
    }

    @GetMapping("/{lessonId}")
    ApiResponse<LessonResponse> getLesson(@Valid @PathVariable("lessonId") Long lessonId){

        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(lessonService.getLessonById(lessonId));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<LessonResponse>> getAllLesson(){
        ApiResponse<List<LessonResponse>> apiResponse = new ApiResponse<>();

        apiResponse.setResult(lessonService.getAllLessons());

        return apiResponse;
    }

    @PutMapping("/updateLesson/{lessonId}")
    ApiResponse<LessonResponse> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonUpdateRequest request){

        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(lessonService.updateLesson(lessonId, request));
        return apiResponse;
    }

    @DeleteMapping("/{lessonId}")
    String deleteLesson(@PathVariable Long lessonId){
        lessonService.deleteLesson(lessonId);
        return "Lesson deleted";
    }
}
