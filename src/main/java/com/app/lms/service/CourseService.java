package com.app.lms.service;

import java.io.IOException;
import java.util.List;


import com.app.lms.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.entity.Course;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.CourseMapper;
import com.app.lms.repository.CategoryRepository;
import com.app.lms.repository.CourseRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {
   final CourseRepository courseRepository;
   final CourseMapper courseMapper;
   final FileUploadService fileUploadService;
   final CategoryRepository categoryRepository;

   public CourseResponse createCourse(CourseCreateRequest request, MultipartFile file) {
      if (courseRepository.existsByTitle(request.getTitle())){
         throw new AppException(ErroCode.TITLE_EXISTED);
      }
      Course course = courseMapper.toCourseMapper(request);

      Category category = categoryRepository.findById(request.getCategoryId())
              .orElseThrow(()-> new AppException(ErroCode.CATEGORY_NO_EXISTED));

      course.setCategory(category);

      String filePath;
      try {
         filePath = fileUploadService.saveCourseFile(file);
      } catch (IOException e) {
         throw new AppException(ErroCode.FILE_ERRO);
      }

      // Save path without leading slash (already included in uploads/courses/...)
      course.setImg(filePath.replace("\\", "/"));

      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   public List<CourseResponse> getAllCourses(){
      return courseRepository.findAll()
              .stream()
              .map(courseMapper::toCourseResponse)
              .toList();
   }

   public CourseResponse getCourseById(Long courseId){
      return courseMapper.toCourseResponse(courseRepository.findById(courseId)
      .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED)));
   }

   public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request, MultipartFile file){
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));
      
      // Update basic fields
      courseMapper.updateCourse(course, request);
      
      // Update category if provided
      if (request.getCategoryId() != null) {
         Category category = categoryRepository.findById(request.getCategoryId())
                 .orElseThrow(() -> new AppException(ErroCode.CATEGORY_NO_EXISTED));
         course.setCategory(category);
      }
      
      // Update image if file is provided
      if (file != null && !file.isEmpty()) {
         try {
            String filePath = fileUploadService.saveCourseFile(file);
            // Save path without leading slash
            course.setImg(filePath.replace("\\", "/"));
         } catch (IOException e) {
            throw new AppException(ErroCode.FILE_ERRO);
         }
      }
      
      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   public void deleteCourse(Long courseId){
      courseRepository.deleteById(courseId);
   }



}
