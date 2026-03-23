package com.app.lms.service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.app.lms.dto.request.ApprovalRequest.ApprovalRequest;
import com.app.lms.entity.Category;
import com.app.lms.entity.Coupon;
import com.app.lms.entity.Lesson;
import com.app.lms.enums.ApprovalStatus;
import com.app.lms.enums.EnrollmentStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.app.lms.dto.request.courseRequest.CourseCreateRequest;
import com.app.lms.dto.request.courseRequest.CourseUpdateRequest;
import com.app.lms.dto.response.CourseResponse;
import com.app.lms.entity.Course;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.CourseMapper;
import com.app.lms.repository.*;
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
   final NotificationService notificationService;
   final EnrollmentRepository enrollmentRepository;
   final PaymentRepository paymentRepository;
   final LessonRepository lessonRepository;
   final LessonProgressRepository lessonProgressRepository;
   final CouponUsageRepository couponUsageRepository;
   final CouponRepository couponRepository;

   @CacheEvict(value = "courses", allEntries = true)
   public CourseResponse createCourse(CourseCreateRequest request, MultipartFile file, String lecturerName) {
      if (courseRepository.existsByTitle(request.getTitle())) {
         throw new AppException(ErroCode.TITLE_EXISTED);
      }
      Course course = courseMapper.toCourseMapper(request);

      Category category = categoryRepository.findById(request.getCategoryId())
              .orElseThrow(() -> new AppException(ErroCode.CATEGORY_NO_EXISTED));

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

      Course savedCourse = courseRepository.save(course);

      // Gửi thông báo về System-Management
      notificationService.sendCourseCreatedNotification(savedCourse, lecturerName);

      return courseMapper.toCourseResponse(savedCourse);
   }

   // CHỈ LẤY KHÓA HỌC ĐÃ PHÊ DUYỆT CHO STUDENTS
   @Cacheable(value = "courses", key = "'all_approved'")
   public List<CourseResponse> getAllCourses() {
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getApprovalStatus() == ApprovalStatus.APPROVED)
              .map(courseMapper::toCourseResponse)
              .collect(Collectors.toList());
   }

   // LẤY TẤT CẢ KHÓA HỌC (CHO ADMIN)
   @Cacheable(value = "courses", key = "'all_admin'")
   public List<CourseResponse> getAllCoursesForAdmin() {
      return courseRepository.findAll()
              .stream()
              .map(courseMapper::toCourseResponse)
              .collect(Collectors.toList());
   }

   // LẤY KHÓA HỌC PENDING (CHỜ PHÊ DUYỆT)
   @Cacheable(value = "courses", key = "'pending'")
   public List<CourseResponse> getPendingCourses() {
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getApprovalStatus() == ApprovalStatus.PENDING)
              .map(courseMapper::toCourseResponse)
              .collect(Collectors.toList());
   }

   // LẤY KHÓA HỌC CỦA LECTURER (BAO GỒM CẢ PENDING)
   @Cacheable(value = "courses", key = "'lecturer_' + #lecturerId")
   public List<CourseResponse> getCoursesByLecturer(Long lecturerId) {
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getTeacherId().equals(lecturerId))
              .map(courseMapper::toCourseResponse)
              .collect(Collectors.toList());
   }

   // LẤY KHÓA HỌC BỊ TỪ CHỐI CỦA LECTURER
   @Cacheable(value = "courses", key = "'lecturer_rejected_' + #lecturerId")
   public List<CourseResponse> getRejectedCoursesByLecturer(Long lecturerId) {
      return courseRepository.findAll()
              .stream()
              .filter(course -> course.getTeacherId().equals(lecturerId) && course.getApprovalStatus() == ApprovalStatus.REJECTED)
              .map(courseMapper::toCourseResponse)
              .collect(Collectors.toList());
   }

   @Cacheable(value = "courses", key = "#courseId")
   public CourseResponse getCourseById(Long courseId) {
      return courseMapper.toCourseResponse(courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED)));
   }

   // PHÊ DUYỆT/TỪ CHỐI KHÓA HỌC (ADMIN ONLY)
   @CacheEvict(value = "courses", allEntries = true)
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

      Course savedCourse = courseRepository.save(course);

      // Gửi thông báo về System-Management
      if (request.getApprovalStatus() == ApprovalStatus.APPROVED) {
         notificationService.sendCourseApprovedNotification(savedCourse, adminId);
      } else if (request.getApprovalStatus() == ApprovalStatus.REJECTED) {
         notificationService.sendCourseRejectedNotification(savedCourse, adminId);
      }

      return courseMapper.toCourseResponse(savedCourse);
   }

   @CacheEvict(value = "courses", allEntries = true)
   public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request, MultipartFile file, boolean isAdmin) {
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

      // NẾU LECTURER SỬA KHÓA HỌC (ĐÃ PHÊ DUYỆT HOẶC BỊ TỪ CHỐI) → ĐƯA VỀ PENDING
      // ADMIN sửa thì giữ nguyên trạng thái
      if (!isAdmin && (course.getApprovalStatus() == ApprovalStatus.APPROVED || course.getApprovalStatus() == ApprovalStatus.REJECTED)) {
         course.setApprovalStatus(ApprovalStatus.PENDING);
         course.setApprovedBy(null);
         course.setApprovedAt(null);
         course.setRejectionReason(null);
         log.info("Course {} updated by lecturer - reset to PENDING approval", courseId);
      }

      return courseMapper.toCourseResponse(courseRepository.save(course));
   }

   @CacheEvict(value = "courses", allEntries = true)
   @Transactional
   public void deleteCourse(Long courseId) {
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new AppException(ErroCode.COURSE_NO_EXISTED));

      // Kiểm tra có enrollment ACTIVE không
      List<?> activeEnrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
      if (!activeEnrollments.isEmpty()) {
         throw new AppException(ErroCode.COURSE_HAS_ACTIVE_ENROLLMENT);
      }

      log.info("Deleting course {} and all related data", courseId);

      // 1. Xóa CouponUsage liên quan tới course
      couponUsageRepository.deleteByCourseId(courseId);

      // 2. Gỡ liên kết Coupon.applicableCourse (set null thay vì xóa coupon)
      List<Coupon> linkedCoupons = couponRepository.findByApplicableCourse_Id(courseId);
      for (Coupon coupon : linkedCoupons) {
         coupon.setApplicableCourse(null);
         couponRepository.save(coupon);
      }

      // 3. Xóa Payment liên quan tới course
      paymentRepository.deleteByCourse_Id(courseId);

      // 4. Xóa LessonProgress cho tất cả lesson trong course
      List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
      for (Lesson lesson : lessons) {
         lessonProgressRepository.deleteByLessonId(lesson.getId());
      }

      // 5. Xóa Course (cascade sẽ tự xóa Lesson → Quiz → Question → AnswerOption, và Enrollment)
      courseRepository.delete(course);

      log.info("Course {} deleted successfully", courseId);
   }
}