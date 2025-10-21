package com.app.lms.service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.app.lms.dto.request.ApprovalRequest.ApprovalRequest;
import com.app.lms.entity.Category;
import com.app.lms.enums.ApprovalStatus;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
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

      course.setImg(filePath.replace("\\", "/"));

      course.setApprovalStatus(ApprovalStatus.PENDING);

      log.info("Creating course '{}' by lecturer {} - Status: PENDING approval",
              course.getTitle(), course.getTeacherId());

      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   // CHỈ LẤY KHÓA HỌC ĐÃ PHÊ DUYỆT CHO STUDENTS
   public List<CourseResponse> getAllCourses(){
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getApprovalStatus() == ApprovalStatus.APPROVED)
              .map(courseMapper::toCourseResponse)
              .toList();
   }

   // LẤY TẤT CẢ KHÓA HỌC (CHO ADMIN)
   public List<CourseResponse> getAllCoursesForAdmin(){
      return courseRepository.findAll()
              .stream()
              .map(courseMapper::toCourseResponse)
              .toList();
   }

   // LẤY KHÓA HỌC PENDING (CHỜ PHÊ DUYỆT)
   public List<CourseResponse> getPendingCourses(){
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getApprovalStatus() == ApprovalStatus.PENDING)
              .map(courseMapper::toCourseResponse)
              .toList();
   }

   // LẤY KHÓA HỌC CỦA LECTURER (BAO GỒM CẢ PENDING)
   public List<CourseResponse> getCoursesByLecturer(Long lecturerId){
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getTeacherId().equals(lecturerId))
              .map(courseMapper::toCourseResponse)
              .toList();
   }

   public CourseResponse getCourseById(Long courseId){
      return courseMapper.toCourseResponse(courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED)));
   }

   // PHÊ DUYỆT/TỪ CHỐI KHÓA HỌC (ADMIN ONLY)
   public CourseResponse approveCourse(Long courseId, ApprovalRequest request, Long adminId) {
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

      // Validate trạng thái hiện tại
      if (course.getApprovalStatus() != ApprovalStatus.PENDING) {
         throw new AppException(ErroCode.COURSE_NOT_PENDING);
      }

      // Validate rejection reason
      if (request.getApprovalStatus() == ApprovalStatus.REJECTED) {
         if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
            throw new AppException(ErroCode.REJECTION_REASON_REQUIRED);
         }
      }

      // Cập nhật trạng thái
      course.setApprovalStatus(request.getApprovalStatus());
      course.setApprovedBy(adminId);
      course.setApprovedAt(OffsetDateTime.now());

      if (request.getApprovalStatus() == ApprovalStatus.REJECTED) {
         course.setRejectionReason(request.getRejectionReason());
      }

      log.info("Course {} has been {} by admin {}",
              courseId, request.getApprovalStatus(), adminId);

      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request, MultipartFile file){
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

      courseMapper.updateCourse(course, request);

      if (request.getCategoryId() != null) {
         Category category = categoryRepository.findById(request.getCategoryId())
                 .orElseThrow(() -> new AppException(ErroCode.CATEGORY_NO_EXISTED));
         course.setCategory(category);
      }

      if (file != null && !file.isEmpty()) {
         try {
            String filePath = fileUploadService.saveCourseFile(file);
            course.setImg(filePath.replace("\\", "/"));
         } catch (IOException e) {
            throw new AppException(ErroCode.FILE_ERRO);
         }
      }
   
      // NẾU LECTURER SỬA KHÓA HỌC ĐÃ PHÊ DUYỆT → ĐƯA VỀ PENDING
      if (course.getApprovalStatus() == ApprovalStatus.APPROVED) {
         course.setApprovalStatus(ApprovalStatus.PENDING);
         course.setApprovedBy(null);
         course.setApprovedAt(null);
         log.info("Course {} updated - reset to PENDING approval", courseId);
      }

      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   public void deleteCourse(Long courseId){
      courseRepository.deleteById(courseId);
   }
}