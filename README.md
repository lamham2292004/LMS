# 🎓 Hệ Thống Quản Lý Học Tập (Learning Management System - LMS)

## 📋 Mục Lục
- [Tổng Quan](#tổng-quan)
- [Kiến Trúc Hệ Thống](#kiến-trúc-hệ-thống)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt & Thiết Lập](#cài-đặt--thiết-lập)
- [Tài Liệu API](#tài-liệu-api)
- [Cấu Trúc Class](#cấu-trúc-class)
- [Test API với Postman](#test-api-với-postman)
- [Lỗi Thường Gặp & Cách Khắc Phục](#lỗi-thường-gặp--cách-khắc-phục)
- [Cấu Hình Môi Trường](#cấu-hình-môi-trường)

## 📖 Tổng Quan

Hệ thống LMS (Learning Management System - Hệ thống quản lý học tập) được xây dựng bằng Spring Boot. Hệ thống hỗ trợ 3 loại người dùng:

- **👨‍🎓 Students (Sinh viên)**: Đăng ký khóa học, làm quiz (bài kiểm tra), xem kết quả
- **👨‍🏫 Lecturers (Giảng viên)**: Tạo khóa học, bài giảng, quiz, quản lý học viên
- **👨‍💼 Admins (Quản trị viên)**: Quản lý toàn bộ hệ thống

### Tính Năng Chính:
- 🔐 JWT Authentication (Xác thực JWT) & Role-based Authorization (Phân quyền theo vai trò)
- 📚 Course (Khóa học) & Lesson (Bài học) Management (Quản lý)
- 📝 Quiz System (Hệ thống bài kiểm tra) với Auto-grading (Tự động chấm điểm)
- 📊 Enrollment Management (Quản lý ghi danh)
- 🎥 File Upload (Tải lên tệp) cho hình ảnh và video
- 🔍 Category-based (Phân loại theo danh mục) Course Organization (Tổ chức khóa học)

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │ -> │    Services     │ -> │  Repositories   │
│ (Điều khiển API)│    │ (Xử lý nghiệp vụ)│    │ (Truy cập CSDL) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         |                       |                       |
         v                       v                       v
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      DTOs       │    │     Mappers     │    │    Entities     │
│ (Đối tượng      │    │ (Chuyển đổi     │    │ (Thực thể CSDL) │
│  truyền dữ liệu)│    │   dữ liệu)      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Luồng Bảo Mật (Security Flow):
```
Request -> CORS -> JWT Filter -> Security Config -> Controller
(Yêu cầu)        (Bộ lọc JWT)  (Cấu hình bảo mật)
                    ↓               ↓
            (Trích xuất & Xác thực) (Kiểm tra quyền)
                    ↓               ↓
               (Thiết lập xác thực) (Cho phép/Từ chối)
```

## 💻 Yêu Cầu Hệ Thống

- **Java**: 17 trở lên
- **Spring Boot**: 3.2.5
- **Database (Cơ sở dữ liệu)**: MySQL 8.0 trở lên
- **Build Tool (Công cụ build)**: Maven 3.6 trở lên
- **IDE**: IntelliJ IDEA / Eclipse / VS Code

## ⚙️ Cài Đặt & Thiết Lập

### 1. Clone Repository (Sao chép mã nguồn)
```bash
git clone https://github.com/your-username/lms-system.git
cd lms-system
```

### 2. Thiết Lập Database (Cơ sở dữ liệu)
```sql
-- Tạo database
CREATE DATABASE LMS;

-- Tạo user (tùy chọn)
CREATE USER 'lms_user'@'localhost' IDENTIFIED BY 'abc123-';
GRANT ALL PRIVILEGES ON LMS.* TO 'lms_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Cấu Hình (Configuration)

**Quan trọng**: File cấu hình chính `application.yaml` đã được ẩn để bảo mật. Bạn cần tạo nó từ file mẫu.

1.  Copy file mẫu:
    ```bash
    cp src/main/resources/application.yaml.example src/main/resources/application.yaml
    ```
    *(Hoặc copy/paste thủ công và đổi tên thành `application.yaml`)*

2.  Cập nhật các thông tin trong `src/main/resources/application.yaml`:
    -   `spring.datasource.password`: Mật khẩu database của bạn.
    -   `app.jwt.secret`: Secret key cho JWT (tự định nghĩa).
    -   `vnpay.secret-key`: Secret key của VNPay (nếu có).

```yaml
spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/LMS"
    username: root
    password: YOUR_DB_PASSWORD # <--- Cập nhật cái này
```

### 4. Chạy Ứng Dụng
```bash
# Sử dụng Maven
mvn spring-boot:run

# Hoặc chạy file JAR
mvn clean package
java -jar target/LMS-0.0.1-SNAPSHOT.jar
```

Server sẽ chạy tại: `http://localhost:8083`

## 🌐 Tài Liệu API

### Base URL: `http://localhost:8083/api`

### Headers (Tiêu đề) Xác Thực:
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

## 📊 Các Endpoints (Điểm cuối) API

### 🔓 **Public APIs (API Công khai - Không yêu cầu xác thực)**

#### Health Check (Kiểm tra sức khỏe)
```http
GET /api/health
```
**Response (Phản hồi):**
```json
{
  "code": 1000,
  "result": {
    "status": "UP",
    "timestamp": "2024-01-15T10:30:00Z",
    "service": "LMS API",
    "version": "1.0.0"
  }
}
```

#### Duyệt Categories (Danh mục)
```http
GET /api/category
```
**Response:**
```json
{
  "code": 1000,
  "result": [
    {
      "id": 1,
      "name": "Programming",
      "description": "Các khóa học lập trình",
      "createdAt": "2024-01-15T10:30:00Z",
      "courses": []
    }
  ]
}
```

#### Duyệt Courses (Khóa học)
```http
GET /api/course
```
**Response:**
```json
{
  "code": 1000,
  "result": [
    {
      "id": 1,
      "title": "Java Spring Boot",
      "description": "Học Spring Boot từ cơ bản",
      "price": 99.99,
      "teacherId": 1,
      "status": "OPEN",
      "categoryId": 1,
      "categoryName": "Programming",
      "img": "/uploads/courses/image.jpg"
    }
  ]
}
```

### 🔐 **Authentication Test APIs (API Test Xác Thức)**

#### Lấy Thông Tin User (Người dùng) Hiện Tại
```http
GET /api/test/me
Authorization: Bearer <JWT_TOKEN>
```
**Response:**
```json
{
  "code": 1000,
  "result": {
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "userType": "STUDENT",
    "isAdmin": false
  }
}
```

---

### 📚 **Course Management APIs (API Quản lý Khóa học)**

#### Tạo Course (Chỉ ADMIN/LECTURER)
```http
POST /api/course/createCourse
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data

Form Data:
- course: {"title":"Java Cơ Bản","description":"Học Java từ đầu","price":50,"categoryId":1}
- file: [course_image.jpg]
```

#### Lấy Chi Tiết Course
```http
GET /api/course/{courseId}
Authorization: Bearer <JWT_TOKEN>
```
**Lưu ý**: Students chỉ xem được courses đã enroll (ghi danh), Lecturers/Admins xem được tất cả.

#### Cập Nhật Course
```http
PUT /api/course/updateCourse/{courseId}
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Java Nâng Cao",
  "description": "Các khái niệm Java nâng cao",
  "price": 120.00,
  "status": "OPEN"
}
```

---

### 📖 **Lesson Management APIs (API Quản lý Bài học)**

#### Tạo Lesson
```http
POST /api/lesson/createLesson
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data

Form Data:
- lesson: {"courseId":1,"title":"Giới thiệu","description":"Bài giới thiệu khóa học","orderIndex":1,"duration":30}
- video: [lesson_video.mp4] (tùy chọn)
```

#### Lấy Lesson
```http
GET /api/lesson/{lessonId}
Authorization: Bearer <JWT_TOKEN>
```

---

### 🎯 **Quiz Management APIs (API Quản lý Bài kiểm tra)**

#### Tạo Quiz
```http
POST /api/quiz
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "lessonId": 1,
  "title": "Bài Kiểm Tra Java Cơ Bản",
  "description": "Kiểm tra kiến thức Java của bạn",
  "timeLimit": 30,
  "maxAttempts": 3,
  "passScore": 70.0
}
```

#### Tạo Question (Câu hỏi)
```http
POST /api/question
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "quizId": 1,
  "questionText": "Java là gì?",
  "questionType": "MULTIPLE_CHOICE",
  "points": 10.0,
  "orderIndex": 1
}
```

#### Tạo Answer Option (Đáp án)
```http
POST /api/answerOption
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "questionId": 1,
  "answerText": "Ngôn ngữ lập trình",
  "isCorrect": true,
  "orderIndex": 1
}
```

---

### 📝 **Quiz Taking APIs (API Làm Bài kiểm tra)**

#### Submit Quiz (Nộp bài kiểm tra) - STUDENT only
```http
POST /api/quiz-results/submit
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "quizId": 1,
  "answers": {
    "1": "1",  // questionId: answerOptionId
    "2": "4"
  },
  "timeTaken": 25
}
```

#### Lấy Kết Quả Quiz Của Tôi
```http
GET /api/quiz-results/my-results/{quizId}
Authorization: Bearer <JWT_TOKEN>
```

---

### 📋 **Enrollment APIs (API Ghi danh)**

#### Enroll Vào Course - STUDENT only
```http
POST /api/enrollment/enroll
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "courseId": 1
}
```

#### Xem Các Course Đã Enroll
```http
GET /api/enrollment/my-enrollments
Authorization: Bearer <JWT_TOKEN>
```

---

## 🏗️ Cấu Trúc Class Chi Tiết

### 📁 **Package Structure (Cấu trúc Package)**

```
com.app.lms/
├── 📁 annotation/          # Custom Annotations (Chú thích tùy chỉnh)
│   ├── CurrentUser.java    # Annotation để inject thông tin user hiện tại
│   └── CurrentUserId.java  # Annotation để inject userId hiện tại
├── 📁 config/             # Configuration Classes (Lớp cấu hình)
│   ├── CorsConfig.java    # Cấu hình CORS cho frontend
│   ├── SecurityConfig.java # Cấu hình Spring Security
│   ├── WebConfig.java     # Cấu hình Web MVC
│   └── ...
├── 📁 controller/         # REST Controllers (Điều khiển REST)
├── 📁 dto/               # Data Transfer Objects (Đối tượng truyền dữ liệu)
├── 📁 entity/            # JPA Entities (Thực thể JPA)
├── 📁 enums/             # Enumerations (Liệt kê)
├── 📁 exception/         # Exception Handling (Xử lý ngoại lệ)
├── 📁 mapper/            # MapStruct Mappers (Bộ chuyển đổi)
├── 📁 repository/        # JPA Repositories (Kho dữ liệu JPA)
├── 📁 resolver/          # Argument Resolvers (Bộ phân giải tham số)
├── 📁 service/           # Business Services (Dịch vụ nghiệp vụ)
└── 📁 until/             # Utility Classes (Lớp tiện ích)
```

---

### 🎯 **Core Entities (Thực thể Cốt lõi)**

#### 1. **Category.java** (Danh mục)
```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;                    // ID danh mục
    
    String name;                // Tên danh mục (VD: "Lập trình", "Thiết kế")
    String description;         // Mô tả danh mục
    
    @CreationTimestamp
    OffsetDateTime createdAt;   // Thời gian tạo
    
    @UpdateTimestamp  
    OffsetDateTime updatedAt;   // Thời gian cập nhật cuối
    
    @OneToMany(mappedBy = "category")
    List<Course> courses;       // Danh sách khóa học trong danh mục
}
```

#### 2. **Course.java** (Khóa học)
```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    Long id;                    // ID khóa học
    
    String title;               // Tiêu đề khóa học
    String description;         // Mô tả chi tiết
    BigDecimal price;           // Giá khóa học
    Long teacherId;             // ID giảng viên (từ Identity Service)
    
    @Enumerated(EnumType.STRING)
    CourseStatus status;        // Trạng thái: UPCOMING, OPEN, CLOSED
    
    OffsetDateTime startTime;   // Thời gian bắt đầu
    OffsetDateTime endTime;     // Thời gian kết thúc
    String img;                 // Đường dẫn ảnh đại diện
    
    @ManyToOne
    Category category;          // Danh mục khóa học
    
    @OneToMany(mappedBy = "course")
    List<Lesson> lessons;       // Danh sách bài học
    
    @OneToMany(mappedBy = "course")
    List<Enrollment> enrollments; // Danh sách ghi danh
}
```

#### 3. **Lesson.java** (Bài học)
```java
@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    Long id;                    // ID bài học
    
    @ManyToOne
    Course course;              // Khóa học chứa bài học này
    Long courseId;              // ID khóa học
    
    String title;               // Tiêu đề bài học
    String description;         // Mô tả bài học
    int orderIndex;             // Thứ tự bài học (1, 2, 3...)
    
    @Enumerated(EnumType.STRING)
    LessonStatus status;        // Trạng thái: UPCOMING, OPEN, CLOSED
    
    Integer duration;           // Thời lượng (phút)
    String videoPath;           // Đường dẫn video bài học
}
```

#### 4. **Quiz.java** (Bài kiểm tra)
```java
@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    Long id;                    // ID bài kiểm tra
    
    @ManyToOne
    Lesson lesson;              // Bài học chứa quiz này
    Long lessonId;              // ID bài học
    
    String title;               // Tiêu đề quiz
    String description;         // Mô tả quiz
    Integer timeLimit;          // Thời gian làm bài (phút)
    Integer maxAttempts;        // Số lần làm tối đa
    Double passScore;           // Điểm đạt tối thiểu (%)
    
    @OneToMany(mappedBy = "quiz")
    List<Question> questions;   // Danh sách câu hỏi
    
    @OneToMany(mappedBy = "quiz")
    List<QuizResult> quizResults; // Danh sách kết quả
}
```

#### 5. **Question.java** (Câu hỏi)
```java
@Entity
@Table(name = "questions")
public class Question {
    @Id
    Long id;                    // ID câu hỏi
    
    @ManyToOne
    Quiz quiz;                  // Quiz chứa câu hỏi này
    Long quizId;                // ID quiz
    
    String questionText;        // Nội dung câu hỏi
    
    @Enumerated(EnumType.STRING)
    QuestionType questionType;  // Loại: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    
    Integer orderIndex;         // Thứ tự câu hỏi
    Double points;              // Điểm số câu hỏi
    
    @OneToMany(mappedBy = "question")
    List<AnswerOption> answerOptions; // Danh sách đáp án
}
```

#### 6. **AnswerOption.java** (Đáp án)
```java
@Entity
@Table(name = "answer_options")
public class AnswerOption {
    @Id
    Long id;                    // ID đáp án
    
    @ManyToOne
    Question question;          // Câu hỏi chứa đáp án này
    Long questionId;            // ID câu hỏi
    
    String answerText;          // Nội dung đáp án
    Boolean isCorrect;          // Đáp án đúng hay sai
    Integer orderIndex;         // Thứ tự đáp án (A, B, C, D)
}
```

#### 7. **Enrollment.java** (Ghi danh)
```java
@Entity
@Table(name = "enrollment")
public class Enrollment {
    @Id
    Long id;                    // ID ghi danh
    
    Long studentId;             // ID sinh viên (từ Identity Service)
    
    @ManyToOne
    Course course;              // Khóa học được ghi danh
    Long courseId;              // ID khóa học
    
    @Enumerated(EnumType.STRING)
    EnrollmentStatus status;    // Trạng thái: ACTIVE, COMPLETED, CANCELLED
    
    OffsetDateTime enrolledAt;  // Thời gian ghi danh
}
```

#### 8. **QuizResult.java** (Kết quả bài kiểm tra)
```java
@Entity
@Table(name = "quiz_results")
public class QuizResult {
    @Id
    Long id;                    // ID kết quả
    
    @ManyToOne
    Quiz quiz;                  // Quiz được làm
    Long quizId;                // ID quiz
    
    Long studentId;             // ID sinh viên
    BigDecimal score;           // Điểm số (%)
    Integer totalQuestions;     // Tổng số câu hỏi
    Integer correctAnswers;     // Số câu trả lời đúng
    Integer timeTaken;          // Thời gian làm bài (phút)
    Integer attemptNumber;      // Lần thử thứ mấy (1, 2, 3...)
    Boolean isPassed;           // Có đậu hay không
    OffsetDateTime takenAt;     // Thời gian làm bài
}
```

---

### 🎮 **Controllers (Điều khiển) Chi Tiết**

#### 1. **CategoryController.java**
```java
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    // 📝 Tạo danh mục mới (ADMIN/LECTURER only)
    @PostMapping("/createCategory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {
        // Tạo danh mục mới với thông tin từ request
        // Trả về thông tin danh mục đã tạo
    }
    
    // 👁️ Lấy thông tin danh mục theo ID (Public)
    @GetMapping("{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(
            @PathVariable("categoryId") Long categoryId) {
        // Lấy chi tiết danh mục theo ID
        // Không cần xác thực - công khai
    }
    
    // 📋 Lấy danh sách tất cả danh mục (Public)
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        // Lấy tất cả danh mục để người dùng browse
        // Không cần xác thực - công khai
    }
    
    // ✏️ Cập nhật danh mục (ADMIN/LECTURER only)
    @PutMapping("{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request,
            @CurrentUser UserTokenInfo currentUser) {
        // Cập nhật thông tin danh mục
    }
    
    // 🗑️ Xóa danh mục (ADMIN only)
    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCategory(@PathVariable Long categoryId) {
        // Xóa danh mục (chỉ ADMIN)
    }
}
```

#### 2. **CourseController.java** 
```java
@RestController
@RequestMapping("/api/course")
public class CourseController {
    
    // 📝 Tạo khóa học mới với upload ảnh
    @PostMapping(value = "/createCourse", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CourseResponse> createCourse(
            @Valid @RequestPart("course") CourseCreateRequest request,
            @RequestPart("file") MultipartFile file,
            @CurrentUser UserTokenInfo currentUser) {
        
        // Nếu là LECTURER, tự động set teacherId = current user
        if (currentUser.getUserType() == UserType.LECTURER) {
            request.setTeacherId(currentUser.getUserId());
        }
        
        // Upload file ảnh và tạo khóa học
    }
    
    // 👁️ Lấy chi tiết khóa học
    @GetMapping("/{courseId}")
    @PostAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or " +
            "@authorizationService.isStudentEnrolledInCourse(#courseId, authentication.name)")
    public ApiResponse<CourseResponse> getCourse(@PathVariable("courseId") Long courseId) {
        // Student chỉ xem được course đã enroll
        // Lecturer/Admin xem tất cả
    }
    
    // 📋 Browse tất cả khóa học (Public)
    @GetMapping
    public ApiResponse<List<CourseResponse>> getAllCourse() {
        // Công khai để mọi người browse khóa học
    }
}
```

#### 3. **EnrollmentController.java**
```java
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    
    // 📝 Student tự enroll vào khóa học
    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<EnrollmentResponse> enrollCourse(
            @Valid @RequestBody EnrollmentCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {
        
        // Validate chỉ STUDENT mới được enroll
        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }
        
        // Tự động set studentId từ JWT token (không trust client)
        request.setStudentId(currentUser.getUserId());
        
        // Kiểm tra không enroll trùng khóa học
        // Tạo enrollment record
    }
    
    // 📋 Xem các khóa học đã enroll
    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<EnrollmentResponse>> getMyEnrollments(
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {
        
        // Chỉ STUDENT mới xem được enrollments của mình
        // Trả về danh sách khóa học đã đăng ký
    }
}
```

#### 4. **QuizResultController.java**
```java
@RestController  
@RequestMapping("/api/quiz-results")
public class QuizResultController {
    
    // 📝 Student nộp bài quiz
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizResultResponse> submitQuiz(
            @RequestBody SubmitQuizRequest request,
            @CurrentUser UserTokenInfo currentUser) {
        
        // Validate chỉ STUDENT mới được làm quiz
        if (currentUser.getUserType() != UserType.STUDENT) {
            throw new AppException(ErroCode.STUDENT_ONLY);
        }
        
        // Set studentId từ JWT (security)
        request.setStudentId(currentUser.getUserId());
        
        // Validate:
        // - Student có enroll course chứa quiz không?
        // - Quiz có vượt quá maxAttempts không?
        // - Quiz có hết thời gian không?
        
        // Tự động chấm điểm:
        // - So sánh answers với correct answers
        // - Tính điểm theo points của từng câu
        // - Xác định pass/fail theo passScore
        
        // Lưu kết quả và trả về
    }
    
    // 📊 Xem kết quả quiz của mình
    @GetMapping("/my-results/{quizId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<QuizResultResponse>> getMyQuizResults(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId,
            @CurrentUser UserTokenInfo currentUser) {
        
        // Trả về tất cả lần làm bài của student cho quiz này
        // Sắp xếp theo thời gian (mới nhất trước)
    }
    
    // 🏆 Lấy kết quả tốt nhất
    @GetMapping("/my-best-result/{quizId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizResultResponse> getMyBestResult(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId) {
        
        // Trả về kết quả có điểm cao nhất của student
    }
    
    // ✅ Kiểm tra có thể làm quiz không
    @GetMapping("/can-take/{quizId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Boolean> canTakeQuiz(
            @PathVariable Long quizId,
            @CurrentUserId Long currentUserId) {
        
        // Kiểm tra:
        // - Đã enroll course chưa?
        // - Đã hết số lần làm chưa?
        // - Quiz có đang mở không?
        
        return true/false;
    }
}
```

---

### 🛠️ **Services (Dịch vụ) Chi Tiết**

#### 1. **AuthorizationService.java** (Dịch vụ phân quyền)
```java
@Service("authorizationService")
public class AuthorizationService {
    
    // 🎓 Kiểm tra Student có enroll course không
    public boolean isStudentEnrolledInCourse(Long courseId, String userIdStr) {
        try {
            Long studentId = Long.parseLong(userIdStr);
            
            // Kiểm tra trong bảng enrollment
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                    studentId, courseId, EnrollmentStatus.ACTIVE);
                    
            return enrolled;
        } catch (Exception e) {
            log.error("Lỗi kiểm tra enrollment: {}", e.getMessage());
            return false;
        }
    }
    
    // 👨‍🏫 Kiểm tra Lecturer có sở hữu course không
    public boolean isLecturerOwnsCourse(Long courseId, String userIdStr) {
        try {
            Long lecturerId = Long.parseLong(userIdStr);
            
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) return false;
            
            // So sánh teacherId với lecturerId hiện tại
            return course.get().getTeacherId().equals(lecturerId);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 📖 Kiểm tra Student có thể xem lesson không
    public boolean canStudentAccessLesson(Long lessonId, String userIdStr) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isEmpty()) return false;
            
            // Kiểm tra student có enroll course chứa lesson này không
            return isStudentEnrolledInCourse(lesson.get().getCourseId(), userIdStr);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 🎯 Kiểm tra Student có thể làm quiz không
    public boolean canStudentAccessQuiz(Long quizId, String userIdStr) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);
            if (quiz.isEmpty()) return false;
            
            // Kiểm tra student có thể access lesson chứa quiz này không
            return canStudentAccessLesson(quiz.get().getLessonId(), userIdStr);
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### 2. **QuizResultService.java** (Dịch vụ kết quả quiz)
```java
@Service
@Transactional
public class QuizResultService {
    
    // 📝 Xử lý submit quiz
    public QuizResultResponse submitQuiz(SubmitQuizRequest request, String studentName) {
        
        // 1️⃣ Validate dữ liệu đầu vào
        validateQuizSubmission(request);
        
        // 2️⃣ Lấy quiz và validate
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));
        
        // 3️⃣ Kiểm tra quyền làm bài
        validateQuizPermission(request.getQuizId(), request.getStudentId());
        
        // 4️⃣ Tính điểm tự động
        QuizGradingResult gradingResult = calculateScore(quiz, request.getAnswers());
        
        // 5️⃣ Xác định số lần thử hiện tại
        Integer currentAttempt = quizResultRepository
                .countAttemptsByQuizIdAndStudentId(request.getQuizId(), request.getStudentId()) + 1;
        
        // 6️⃣ Tạo QuizResult
        QuizResult quizResult = QuizResult.builder()
                .quiz(quiz)
                .studentId(request.getStudentId())
                .score(gradingResult.getScore())
                .totalQuestions(gradingResult.getTotalQuestions())
                .correctAnswers(gradingResult.getCorrectAnswers())
                .timeTaken(request.getTimeTaken())
                .attemptNumber(currentAttempt)
                .isPassed(gradingResult.getScore().doubleValue() >= quiz.getPassScore())
                .build();
        
        // 7️⃣ Lưu vào database
        QuizResult savedResult = quizResultRepository.save(quizResult);
        
        // 8️⃣ Trả về response
        return quizResultMapper.toQuizResultResponse(
                savedResult, quiz, studentName, gradingResult.getFeedback());
    }
    
    // 🧮 Thuật toán tự động chấm điểm
    private QuizGradingResult calculateScore(Quiz quiz, Map<Long, String> studentAnswers) {
        List<Question> questions = quiz.getQuestions();
        
        // Chỉ tính câu hỏi có đáp án
        List<Question> validQuestions = questions.stream()
                .filter(q -> q.getAnswerOptions() != null && !q.getAnswerOptions().isEmpty())
                .toList();
        
        double totalPossiblePoints = validQuestions.stream()
                .mapToDouble(Question::getPoints)
                .sum();
        
        double earnedPoints = 0;
        int correctAnswers = 0;
        StringBuilder feedback = new StringBuilder();
        
        int questionNumber = 1;
        for (Question question : questions) {
            String studentAnswer = studentAnswers.get(question.getId());
            boolean isCorrect = false;
            
            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                question.getQuestionType() == QuestionType.TRUE_FALSE) {
                
                // Tìm đáp án đúng
                Optional<AnswerOption> correctOption = question.getAnswerOptions().stream()
                        .filter(AnswerOption::getIsCorrect)
                        .findFirst();
                
                // So sánh với câu trả lời của student
                if (studentAnswer != null && correctOption.isPresent() &&
                    correctOption.get().getId().toString().equals(studentAnswer)) {
                    isCorrect = true;
                    earnedPoints += question.getPoints();
                    correctAnswers++;
                }
                
                // Tạo feedback chi tiết
                feedback.append("Câu ").append(questionNumber).append(": ");
                if (studentAnswer == null) {
                    feedback.append("Chưa trả lời");
                } else {
                    feedback.append(isCorrect ? "Đúng" : "Sai");
                    if (!isCorrect && correctOption.isPresent()) {
                        feedback.append(" (Đáp án đúng: ")
                               .append(correctOption.get().getAnswerText())
                               .append(")");
                    }
                }
                feedback.append("\n");
            }
            
            questionNumber++;
        }
        
        // Tính điểm phần trăm
        BigDecimal finalScore = BigDecimal.ZERO;
        if (totalPossiblePoints > 0) {
            finalScore = BigDecimal.valueOf(earnedPoints / totalPossiblePoints * 100)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return QuizGradingResult.builder()
                .score(finalScore)
                .totalQuestions(validQuestions.size())
                .correctAnswers(correctAnswers)
                .feedback(feedback.toString())
                .build();
    }
    
    // 🔒 Validate quyền làm quiz
    private void validateQuizPermission(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErroCode.QUIZ_NO_EXISTED));
        
        Long courseId = quiz.getLesson().getCourseId();
        
        // Kiểm tra đã enroll course chưa
        boolean isEnrolled = enrollmentRepository
                .existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE);
        if (!isEnrolled) {
            throw new AppException(ErroCode.NOT_ENROLLED);
        }
        
        // Kiểm tra số lần làm bài
        if (quiz.getMaxAttempts() != null) {
            Integer attemptCount = quizResultRepository
                    .countAttemptsByQuizIdAndStudentId(quizId, studentId);
            if (attemptCount >= quiz.getMaxAttempts()) {
                throw new AppException(ErroCode.QUIZ_MAX_ATTEMPTS_REACHED);
            }
        }
    }
}
```

#### 3. **FileUploadService.java** (Dịch vụ upload file)
```java
@Service
public class FileUploadService {
    private final String COURSE_UPLOAD_DIR = "uploads/courses/";      // Thư mục ảnh khóa học
    private final String LESSON_UPLOAD_DIR = "uploads/lessons/videos/"; // Thư mục video bài học
    
    // 🖼️ Lưu file ảnh khóa học
    public String saveCourseFile(MultipartFile file) throws IOException {
        return saveFile(file, COURSE_UPLOAD_DIR);
    }
    
    // 🎥 Lưu file video bài học  
    public String saveLessonVideo(MultipartFile file) throws IOException {
        return saveFile(file, LESSON_UPLOAD_DIR);
    }
    
    // 💾 Logic chung lưu file
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        // Kiểm tra file không rỗng
        if (file.isEmpty()) {
            throw new AppException(ErroCode.FILE_EXISTED);
        }
        
        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Tạo tên file unique (timestamp + tên gốc)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // Copy file vào thư mục
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Trả về đường dẫn để lưu vào database
        return uploadDir + fileName;
    }
}
```

---

### 🔐 **Security Components (Thành phần bảo mật)**

#### 1. **JwtAuthenticationFilter.java** (Bộ lọc JWT)
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1️⃣ Kiểm tra có Authorization header không
            String authHeader = request.getHeader("Authorization");
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                // Không có token -> để Spring Security quyết định (có thể là public endpoint)
                filterChain.doFilter(request, response);
                return;
            }
            
            // 2️⃣ Trích xuất token từ header
            String token = tokenExtractor.extractTokenFromRequest(request);
            
            // 3️⃣ Validate token
            if (jwtTokenUtil.validateToken(token)) {
                
                // 4️⃣ Lấy thông tin user từ token
                UserTokenInfo userInfo = jwtTokenUtil.getUserInfoFromToken(token);
                
                // 5️⃣ Chuyển đổi UserType thành Spring Security roles
                Collection<GrantedAuthority> authorities = convertUserTypeToAuthorities(userInfo);
                
                // 6️⃣ Tạo Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userInfo.getUserId().toString(), // principal
                                null,                            // credentials
                                authorities                      // authorities (roles)
                        );
                
                // 7️⃣ Set authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT Authentication thành công cho user: {}", userInfo.getEmail());
            }
            
        } catch (AppException e) {
            log.warn("JWT Authentication thất bại: {}", e.getMessage());
            // Không throw exception, để Spring Security xử lý
        }
        
        filterChain.doFilter(request, response);
    }
    
    // 🏷️ Chuyển đổi UserType thành Spring Security roles
    private Collection<GrantedAuthority> convertUserTypeToAuthorities(UserTokenInfo userInfo) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        switch (userInfo.getUserType()) {
            case STUDENT -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            }
            case LECTURER -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_LECTURER"));
                // Lecturer có thể là Admin
                if (Boolean.TRUE.equals(userInfo.getIsAdmin())) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }
            case ADMIN -> {
                // Admin có tất cả quyền
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_LECTURER"));
                authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            }
        }
        
        return authorities;
    }
}
```

#### 2. **JwtTokenUtil.java** (Tiện ích JWT)
```java
@Component
public class JwtTokenUtil {
    @Value("${app.jwt.secret}")
    String secret; // JWT secret từ application.yaml
    
    // 🔑 Lấy signing key từ secret
    SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    // 📄 Lấy tất cả claims từ token
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AppException(ErroCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (SecurityException e) {
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }
    
    // 🆔 Lấy userId từ token
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object userIdObj = claims.get("userId");
        
        if (userIdObj == null) {
            userIdObj = claims.getSubject(); // Fallback
        }
        
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        } else if (userIdObj instanceof String) {
            return Long.parseLong((String) userIdObj);
        }
        
        throw new AppException(ErroCode.TOKEN_INVALID);
    }
    
    // 👤 Lấy toàn bộ thông tin user từ token
    public UserTokenInfo getUserInfoFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        
        return UserTokenInfo.builder()
                .userId(getUserIdFromToken(token))
                .accountId(extractLongFromClaims(claims, "accountId"))
                .username((String) claims.get("username"))
                .email((String) claims.get("email"))
                .fullName((String) claims.get("full_name"))
                .userType(getUserTypeFromToken(token))
                .studentCode((String) claims.get("studentCode"))
                .lecturerCode((String) claims.get("lecturerCode"))
                .classId(extractLongFromClaims(claims, "classId"))
                .departmentId(extractLongFromClaims(claims, "departmentId"))
                .isAdmin(extractBooleanFromClaims(claims, "isAdmin"))
                .build();
    }
    
    // ✅ Validate token
    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    // ⏰ Kiểm tra token đã hết hạn chưa
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
```

---

## 📋 **Test API với Postman - Hướng Dẫn Đầy Đủ**

### 🚀 **Setup Postman Environment (Thiết lập môi trường Postman)**

#### 1. Tạo Environment (Môi trường) mới:
- **Environment Name (Tên môi trường)**: `LMS Development`
- **Variables (Biến):**
  ```
  base_url: http://localhost:8083/api
  jwt_token: (sẽ set sau khi login)
  student_token: (JWT token của student - sinh viên)
  lecturer_token: (JWT token của lecturer - giảng viên) 
  admin_token: (JWT token của admin - quản trị viên)
  ```

#### 2. Tạo Collection (Bộ sưu tập): `LMS API Complete Tests`

---

## 🧪 **Test Tất Cả APIs Theo Controller (Điều khiển)**

---

### 🏥 **1. HEALTH CONTROLLER APIs (API Kiểm tra sức khỏe hệ thống)**

#### **1.1 Health Check (Kiểm tra sức khỏe)**
```http
GET {{base_url}}/health
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "status": "UP",
        "timestamp": "2024-01-15T10:30:00Z",
        "service": "LMS API",
        "version": "1.0.0"
    }
}
```

---

### 🧪 **2. JWT TEST CONTROLLER APIs (API Test xác thực JWT)**

#### **2.1 Test Simple - Public (Test đơn giản - công khai)**
```http
GET {{base_url}}/test/simple
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": "Server hoạt động OK - không cần token!"
}
```

#### **2.2 Get Current User Info (Lấy thông tin người dùng hiện tại)**
```http
GET {{base_url}}/test/me
Authorization: Bearer {{jwt_token}}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "userId": 1,
        "accountId": 1,
        "username": "student123",
        "email": "student@example.com",
        "fullName": "Nguyễn Văn A",
        "userType": "STUDENT",
        "studentCode": "SV001",
        "classId": 1,
        "isAdmin": false
    }
}
```

#### **2.3 Get Current User ID (Lấy ID người dùng hiện tại)**
```http
GET {{base_url}}/test/my-id
Authorization: Bearer {{jwt_token}}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": 1
}
```

---

### 📂 **3. CATEGORY CONTROLLER APIs (API Quản lý danh mục)**

#### **3.1 Browse All Categories - Public (Duyệt tất cả danh mục - công khai)**
```http
GET {{base_url}}/category
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": [
        {
            "id": 1,
            "name": "Lập Trình",
            "description": "Các khóa học lập trình",
            "createdAt": "2024-01-15T10:30:00Z",
            "updatedAt": "2024-01-15T10:30:00Z",
            "courses": []
        }
    ]
}
```

#### **3.2 Get Category By ID - Public (Lấy danh mục theo ID - công khai)**
```http
GET {{base_url}}/category/1
```

#### **3.3 Create Category (Tạo danh mục) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
POST {{base_url}}/category/createCategory
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "name": "Thiết Kế Đồ Họa",
    "description": "Các khóa học về thiết kế và đồ họa"
}
```

**Expected Success Response (Phản hồi thành công mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 2,
        "name": "Thiết Kế Đồ Họa",
        "description": "Các khóa học về thiết kế và đồ họa",
        "createdAt": "2024-01-15T11:00:00Z",
        "updatedAt": "2024-01-15T11:00:00Z",
        "courses": []
    }
}
```

#### **3.4 Update Category (Cập nhật danh mục) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
PUT {{base_url}}/category/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "name": "Lập Trình Nâng Cao",
    "description": "Các khóa học lập trình từ cơ bản đến nâng cao"
}
```

#### **3.5 Delete Category (Xóa danh mục) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/category/1
Authorization: Bearer {{admin_token}}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": "Category deleted successfully"
}
```

---

### 📚 **4. COURSE CONTROLLER APIs (API Quản lý khóa học)**

#### **4.1 Browse All Courses - Public (Duyệt tất cả khóa học - công khai)**
```http
GET {{base_url}}/course
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": [
        {
            "id": 1,
            "title": "Java Spring Boot Cơ Bản",
            "description": "Học Spring Boot từ đầu",
            "price": 500000,
            "teacherId": 2,
            "status": "OPEN",
            "startTime": "2024-02-01T08:00:00Z",
            "endTime": "2024-04-01T17:00:00Z",
            "createdAt": "2024-01-15T10:30:00Z",
            "updatedAt": "2024-01-15T10:30:00Z",
            "categoryId": 1,
            "categoryName": "Lập Trình",
            "img": "/uploads/courses/1642234567890_java.jpg",
            "lessons": [],
            "enrollments": []
        }
    ]
}
```

#### **4.2 Get Course By ID (Lấy khóa học theo ID) - Enrolled Students only (chỉ sinh viên đã ghi danh)**
```http
GET {{base_url}}/course/1
Authorization: Bearer {{student_token}}
```

#### **4.3 Create Course with Image Upload (Tạo khóa học với tải ảnh lên) - ADMIN/LECTURER**
```http
POST {{base_url}}/course/createCourse
Authorization: Bearer {{lecturer_token}}
Content-Type: multipart/form-data

Form Data (Dữ liệu form):
- course: {
    "title": "React.js Toàn Tập",
    "description": "Khóa học React.js từ cơ bản đến nâng cao với các project thực tế",
    "price": 800000,
    "categoryId": 1,
    "status": "UPCOMING",
    "startTime": "2024-03-01T08:00:00Z",
    "endTime": "2024-05-01T17:00:00Z"
  }
- file: [Chọn file ảnh khóa học]
```

**⚠️ Postman Setup (Thiết lập Postman):**
1. Chọn `Body` -> `form-data`
2. Key `course`: Type `Text`, Value là JSON string (chuỗi JSON)
3. Key `file`: Type `File`, browse (duyệt) và chọn ảnh

#### **4.4 Update Course (Cập nhật khóa học) - ADMIN/Course Owner (ADMIN/Chủ sở hữu khóa học)**
```http
PUT {{base_url}}/course/updateCourse/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "title": "Java Spring Boot Nâng Cao",
    "description": "Khóa học Spring Boot nâng cao với microservices",
    "price": 750000,
    "status": "OPEN"
}
```

#### **4.5 Delete Course (Xóa khóa học) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/course/1
Authorization: Bearer {{admin_token}}
```

---

### 📖 **5. LESSON CONTROLLER APIs (API Quản lý bài học)**

#### **5.1 Get All Lessons (Lấy tất cả bài học) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
GET {{base_url}}/lesson
Authorization: Bearer {{lecturer_token}}
```

#### **5.2 Get Lesson By ID (Lấy bài học theo ID) - Enrolled Students/LECTURER/ADMIN (Sinh viên đã ghi danh/LECTURER/ADMIN)**
```http
GET {{base_url}}/lesson/1
Authorization: Bearer {{student_token}}
```

#### **5.3 Create Lesson with Video Upload (Tạo bài học với tải video lên)**
```http
POST {{base_url}}/lesson/createLesson
Authorization: Bearer {{lecturer_token}}
Content-Type: multipart/form-data

Form Data (Dữ liệu form):
- lesson: {
    "courseId": 1,
    "title": "Giới Thiệu về Spring Boot",
    "description": "Bài học đầu tiên về Spring Boot framework",
    "orderIndex": 1,
    "status": "OPEN",
    "duration": 45
  }
- video: [Chọn file video bài học - optional (tùy chọn)]
```

**Expected Success Response (Phản hồi thành công mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "courseId": 1,
        "title": "Giới Thiệu về Spring Boot",
        "description": "Bài học đầu tiên về Spring Boot framework",
        "orderIndex": 1,
        "duration": 45,
        "createdAt": "2024-01-15T11:30:00Z",
        "updatedAt": "2024-01-15T11:30:00Z",
        "videoPath": "/uploads/lessons/videos/1642237890123_intro.mp4",
        "status": "OPEN"
    }
}
```

#### **5.4 Update Lesson (Cập nhật bài học)**
```http
PUT {{base_url}}/lesson/updateLesson/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "title": "Giới Thiệu về Spring Boot - Cập Nhật",
    "description": "Bài học giới thiệu Spring Boot với nội dung mới",
    "orderIndex": 1,
    "status": "OPEN",
    "duration": 50
}
```

#### **5.5 Delete Lesson (Xóa bài học) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/lesson/1
Authorization: Bearer {{admin_token}}
```

---

### 🎯 **6. QUIZ CONTROLLER APIs (API Quản lý bài kiểm tra)**

#### **6.1 Get All Quizzes (Lấy tất cả bài kiểm tra) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
GET {{base_url}}/quiz
Authorization: Bearer {{lecturer_token}}
```

#### **6.2 Get Quiz By ID (Lấy bài kiểm tra theo ID)**
```http
GET {{base_url}}/quiz/1
Authorization: Bearer {{student_token}}
```

#### **6.3 Get Quizzes By Lesson (Lấy bài kiểm tra theo bài học)**
```http
GET {{base_url}}/quiz/lesson/1
Authorization: Bearer {{student_token}}
```

#### **6.4 Get Quizzes By Course (Lấy bài kiểm tra theo khóa học)**
```http
GET {{base_url}}/quiz/course/1
Authorization: Bearer {{student_token}}
```

#### **6.5 Create Quiz (Tạo bài kiểm tra) - ADMIN/LECTURER**
```http
POST {{base_url}}/quiz
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "lessonId": 1,
    "title": "Kiểm Tra Spring Boot Cơ Bản",
    "description": "Bài kiểm tra đánh giá kiến thức Spring Boot cơ bản",
    "timeLimit": 30,
    "maxAttempts": 3,
    "passScore": 70.0
}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "lessonId": 1,
        "title": "Kiểm Tra Spring Boot Cơ Bản",
        "description": "Bài kiểm tra đánh giá kiến thức Spring Boot cơ bản",
        "timeLimit": 30,
        "maxAttempts": 3,
        "passScore": 70.0,
        "createdAt": "2024-01-15T12:00:00Z",
        "updatedAt": "2024-01-15T12:00:00Z",
        "questions": [],
        "quizResults": []
    }
}
```

#### **6.6 Update Quiz (Cập nhật bài kiểm tra)**
```http
PUT {{base_url}}/quiz/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "title": "Kiểm Tra Spring Boot Nâng Cao",
    "description": "Bài kiểm tra nâng cao về Spring Boot",
    "timeLimit": 45,
    "maxAttempts": 2,
    "passScore": 75.0
}
```

#### **6.7 Delete Quiz (Xóa bài kiểm tra) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/quiz/1
Authorization: Bearer {{admin_token}}
```

---

### ❓ **7. QUESTION CONTROLLER APIs (API Quản lý câu hỏi)**

#### **7.1 Get All Questions (Lấy tất cả câu hỏi) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
GET {{base_url}}/question
Authorization: Bearer {{lecturer_token}}
```

#### **7.2 Get Question By ID (Lấy câu hỏi theo ID)**
```http
GET {{base_url}}/question/1
Authorization: Bearer {{student_token}}
```

#### **7.3 Get Questions By Quiz (Lấy câu hỏi theo bài kiểm tra)**
```http
GET {{base_url}}/question/quiz/1
Authorization: Bearer {{student_token}}
```

#### **7.4 Create Question (Tạo câu hỏi) - ADMIN/LECTURER**
```http
POST {{base_url}}/question
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "quizId": 1,
    "questionText": "Spring Boot là framework của ngôn ngữ lập trình nào?",
    "questionType": "MULTIPLE_CHOICE",
    "points": 10.0,
    "orderIndex": 1
}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "quizId": 1,
        "questionText": "Spring Boot là framework của ngôn ngữ lập trình nào?",
        "questionType": "MULTIPLE_CHOICE",
        "orderIndex": 1,
        "points": 10.0,
        "createdAt": "2024-01-15T12:15:00Z",
        "updatedAt": "2024-01-15T12:15:00Z",
        "answerOptions": []
    }
}
```

#### **7.5 Update Question (Cập nhật câu hỏi)**
```http
PUT {{base_url}}/question/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "questionText": "Spring Boot framework được viết bằng ngôn ngữ lập trình nào?",
    "questionType": "MULTIPLE_CHOICE",
    "points": 15.0,
    "orderIndex": 1
}
```

#### **7.6 Delete Question (Xóa câu hỏi) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/question/1
Authorization: Bearer {{admin_token}}
```

---

### 💡 **8. ANSWER OPTION CONTROLLER APIs (API Quản lý đáp án)**

#### **8.1 Get All Answer Options (Lấy tất cả đáp án) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
GET {{base_url}}/answerOption
Authorization: Bearer {{lecturer_token}}
```

#### **8.2 Get Answer Option By ID (Lấy đáp án theo ID)**
```http
GET {{base_url}}/answerOption/1
Authorization: Bearer {{student_token}}
```

#### **8.3 Get Answer Options By Question (Lấy đáp án theo câu hỏi)**
```http
GET {{base_url}}/answerOption/question/1
Authorization: Bearer {{student_token}}
```

#### **8.4 Create Answer Option (Tạo đáp án) - ADMIN/LECTURER**
```http
POST {{base_url}}/answerOption
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "questionId": 1,
    "answerText": "Java",
    "isCorrect": true,
    "orderIndex": 1
}
```

#### **8.5 Create Additional Answer Options (Tạo thêm các đáp án khác)**

**Option B (Đáp án B) - Sai:**
```http
POST {{base_url}}/answerOption
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "questionId": 1,
    "answerText": "Python",
    "isCorrect": false,
    "orderIndex": 2
}
```

**Option C (Đáp án C) - Sai:**
```http
POST {{base_url}}/answerOption
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "questionId": 1,
    "answerText": "JavaScript",
    "isCorrect": false,
    "orderIndex": 3
}
```

**Option D (Đáp án D) - Sai:**
```http
POST {{base_url}}/answerOption
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "questionId": 1,
    "answerText": "C#",
    "isCorrect": false,
    "orderIndex": 4
}
```

#### **8.6 Update Answer Option (Cập nhật đáp án)**
```http
PUT {{base_url}}/answerOption/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "answerText": "Java Programming Language",
    "isCorrect": true,
    "orderIndex": 1
}
```

#### **8.7 Delete Answer Option (Xóa đáp án) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/answerOption/1
Authorization: Bearer {{admin_token}}
```

---

### 📝 **9. ENROLLMENT CONTROLLER APIs (API Quản lý ghi danh)**

#### **9.1 Student Self-Enroll (Sinh viên tự ghi danh) - STUDENT only (chỉ STUDENT)**
```http
POST {{base_url}}/enrollment/enroll
Authorization: Bearer {{student_token}}
Content-Type: application/json

{
    "courseId": 1
}
```

**Expected Success Response (Phản hồi thành công mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "studentId": 3,
        "courseId": 1,
        "status": "ACTIVE",
        "enrolledAt": "2024-01-15T13:00:00Z"
    }
}
```

#### **9.2 Admin/Lecturer Create Enrollment (Admin/Lecturer tạo ghi danh)**
```http
POST {{base_url}}/enrollment/createEnrollment
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "studentId": 4,
    "courseId": 1,
    "status": "ACTIVE"
}
```

#### **9.3 Get My Enrollments (Lấy ghi danh của tôi) - STUDENT only (chỉ STUDENT)**
```http
GET {{base_url}}/enrollment/my-enrollments
Authorization: Bearer {{student_token}}
```

**Expected Response (Phản hồi mong đợi):**
```json
{
    "code": 1000,
    "result": [
        {
            "id": 1,
            "studentId": 3,
            "courseId": 1,
            "status": "ACTIVE",
            "enrolledAt": "2024-01-15T13:00:00Z"
        }
    ]
}
```

#### **9.4 Get All Enrollments (Lấy tất cả ghi danh) - ADMIN/LECTURER only (chỉ ADMIN/LECTURER)**
```http
GET {{base_url}}/enrollment
Authorization: Bearer {{lecturer_token}}
```

#### **9.5 Get Enrollment By ID (Lấy ghi danh theo ID)**
```http
GET {{base_url}}/enrollment/1
Authorization: Bearer {{student_token}}
```

#### **9.6 Update Enrollment Status (Cập nhật trạng thái ghi danh)**
```http
PUT {{base_url}}/enrollment/1
Authorization: Bearer {{lecturer_token}}
Content-Type: application/json

{
    "studentId": 3,
    "courseId": 1,
    "status": "COMPLETED"
}
```

#### **9.7 Delete Enrollment (Xóa ghi danh) - ADMIN only (chỉ ADMIN)**
```http
DELETE {{base_url}}/enrollment/1
Authorization: Bearer {{admin_token}}
```

---

### 📊 **10. QUIZ RESULT CONTROLLER APIs (API Quản lý kết quả bài kiểm tra)**

#### **10.1 Submit Quiz (Nộp bài kiểm tra) - STUDENT only (chỉ STUDENT)**
```http
POST {{base_url}}/quiz-results/submit
Authorization: Bearer {{student_token}}
Content-Type: application/json

{
    "quizId": 1,
    "answers": {
        "1": "1"
    },
    "timeTaken": 15
}
```

**Expected Success Response (Phản hồi thành công mong đợi):**
```json
{
    "code": 1000,
    "result": {
        "id": 1,
        "quizId": 1,
        "quizTitle": "Kiểm Tra Spring Boot Cơ Bản",
        "studentId": 3,
        "studentName": "Nguyễn Văn A",
        "score": 100.00,
        "totalQuestions": 1,
        "correctAnswers": 1,
        "timeTaken": 15,
        "attemptNumber": 1,
        "isPassed": true,
        "takenAt": "2024-01-15T14:00:00Z",
        "feedback": "Câu 1: Đúng\n"
    }
}
```

#### **10.2 Get My Quiz Results (Lấy kết quả bài kiểm tra của tôi) - STUDENT only (chỉ STUDENT)**
```http
GET {{base_url}}/quiz-results/my-results/1
Authorization: Bearer {{student_token}}
```

#### **10.3 Get My Best Result (Lấy kết quả tốt nhất của tôi) - STUDENT only (chỉ STUDENT)**
```http
GET {{base_url}}/quiz-results/my-best-result/1
Authorization: Bearer {{student_token}}
```

#### **10.4 Get My Course Results (Lấy kết quả khóa học của tôi) - STUDENT only (chỉ STUDENT)**
```http
GET {{base_url}}/quiz-results/my-course-results/1
Authorization: Bearer {{student_token}}
```

#### **10.5 Get All Quiz Results (Lấy tất cả kết quả bài kiểm tra) - ADMIN/LECTURER**
```http
GET {{base_url}}/quiz-results/quiz/1/all-results
Authorization: Bearer {{lecturer_token}}
```

#### **10.6 Check Can Take Quiz (Kiểm tra có thể làm bài không) - STUDENT only (chỉ STUDENT)**
```http
GET {{base_url}}/quiz-results/can-take/1
Authorization: Bearer {{student_token}}
```

**Expected Response:**
```json
{
    "code": 1000,
    "result": true
}
```
---

## 🚀 **Complete Testing Workflows (Luồng Test Hoàn Chỉnh)**

### **🎓 Workflow 1: Student Journey (Hành trình Sinh viên)**

#### **Bước 1: Setup Environment (Thiết lập môi trường)**
```javascript
// Postman Pre-request Script - Collection level (Script tiền yêu cầu - mức độ collection)
pm.environment.set("student_token", "your_student_jwt_token_here");
```

#### **Bước 2: Browse Available Content (Duyệt nội dung có sẵn)**
```http
1. GET {{base_url}}/health                    // ✅ Kiểm tra server
2. GET {{base_url}}/category                  // 👀 Xem danh mục
3. GET {{base_url}}/course                    // 👀 Browse (duyệt) khóa học
4. GET {{base_url}}/category/1                // 📋 Chi tiết danh mục
```

#### **Bước 3: Authentication Test (Test xác thực)**
```http
5. GET {{base_url}}/test/simple               // ✅ Test public API (API công khai)
6. GET {{base_url}}/test/me                   // 🔐 Test JWT token
   Authorization: Bearer {{student_token}}
7. GET {{base_url}}/test/my-id                // 🆔 Lấy user ID
   Authorization: Bearer {{student_token}}
```

#### **Bước 4: Course Enrollment (Ghi danh khóa học)**
```http
8. POST {{base_url}}/enrollment/enroll        // 📝 Đăng ký khóa học
   Authorization: Bearer {{student_token}}
   {
     "courseId": 1
   }

9. GET {{base_url}}/enrollment/my-enrollments // 📋 Xem đã đăng ký gì
   Authorization: Bearer {{student_token}}
```

#### **Bước 5: Access Course Content (Truy cập nội dung khóa học)**
```http
10. GET {{base_url}}/course/1                 // 📚 Xem chi tiết course
    Authorization: Bearer {{student_token}}

11. GET {{base_url}}/lesson/1                 // 📖 Xem bài học
    Authorization: Bearer {{student_token}}

12. GET {{base_url}}/quiz/lesson/1            // 🎯 Xem quiz của lesson
    Authorization: Bearer {{student_token}}
```

#### **Bước 6: Take Quiz (Làm bài kiểm tra)**
```http
13. GET {{base_url}}/quiz-results/can-take/1  // ✅ Kiểm tra có thể làm bài
    Authorization: Bearer {{student_token}}

14. GET {{base_url}}/quiz/1                   // 📄 Lấy thông tin quiz
    Authorization: Bearer {{student_token}}

15. GET {{base_url}}/question/quiz/1          // ❓ Lấy câu hỏi
    Authorization: Bearer {{student_token}}

16. GET {{base_url}}/answerOption/question/1  // 💡 Lấy đáp án
    Authorization: Bearer {{student_token}}

17. POST {{base_url}}/quiz-results/submit     // 📝 Nộp bài
    Authorization: Bearer {{student_token}}
    {
      "quizId": 1,
      "answers": {
        "1": "1"  // questionId: answerOptionId
      },
      "timeTaken": 20
    }

18. GET {{base_url}}/quiz-results/my-results/1 // 📊 Xem kết quả
    Authorization: Bearer {{student_token}}
```

---

### **👨‍🏫 Workflow 2: Lecturer Journey (Hành trình Giảng viên)**

#### **Bước 1: Setup & Authentication (Thiết lập & Xác thực)**
```javascript
pm.environment.set("lecturer_token", "your_lecturer_jwt_token_here");
```

```http
1. GET {{base_url}}/test/me                   // 🔐 Verify (xác minh) lecturer role
   Authorization: Bearer {{lecturer_token}}
```

#### **Bước 2: Content Management (Quản lý nội dung)**
```http
2. POST {{base_url}}/category/createCategory  // 📂 Tạo danh mục
   Authorization: Bearer {{lecturer_token}}
   {
     "name": "Machine Learning",
     "description": "Khóa học về học máy và AI"
   }

3. POST {{base_url}}/course/createCourse      // 📚 Tạo khóa học
   Authorization: Bearer {{lecturer_token}}
   Content-Type: multipart/form-data
   Form Data (Dữ liệu form):
   - course: {JSON course data}
   - file: [course_image.jpg]

4. GET {{base_url}}/course                    // 👀 Xem danh sách course

5. POST {{base_url}}/lesson/createLesson      // 📖 Tạo bài học
   Authorization: Bearer {{lecturer_token}}
   Content-Type: multipart/form-data
   Form Data (Dữ liệu form):
   - lesson: {JSON lesson data}  
   - video: [lesson_video.mp4]
```

#### **Bước 3: Quiz Creation (Tạo bài kiểm tra)**
```http
6. POST {{base_url}}/quiz                     // 🎯 Tạo bài kiểm tra
   Authorization: Bearer {{lecturer_token}}
   {
     "lessonId": 1,
     "title": "Quiz về Machine Learning",
     "timeLimit": 45,
     "maxAttempts": 2,
     "passScore": 75.0
   }

7. POST {{base_url}}/question                 // ❓ Tạo câu hỏi
   Authorization: Bearer {{lecturer_token}}
   {
     "quizId": 1,
     "questionText": "Thuật toán nào dùng cho classification (phân loại)?",
     "questionType": "MULTIPLE_CHOICE",
     "points": 10.0,
     "orderIndex": 1
   }

8-11. POST {{base_url}}/answerOption          // 💡 Tạo 4 đáp án
      Authorization: Bearer {{lecturer_token}}
      // Option A (Correct - Đúng)
      {
        "questionId": 1,
        "answerText": "Decision Tree",
        "isCorrect": true,
        "orderIndex": 1
      }
      // Option B-D (Incorrect - Sai) - Lặp lại với isCorrect: false
```

#### **Bước 4: Student Management (Quản lý học viên)**
```http
12. GET {{base_url}}/enrollment               // 📋 Xem danh sách enrollment
    Authorization: Bearer {{lecturer_token}}

13. POST {{base_url}}/enrollment/createEnrollment // ➕ Thêm student vào course
    Authorization: Bearer {{lecturer_token}}
    {
      "studentId": 5,
      "courseId": 1,
      "status": "ACTIVE"
    }

14. GET {{base_url}}/quiz-results/quiz/1/all-results // 📊 Xem kết quả tất cả student
    Authorization: Bearer {{lecturer_token}}
```

---

### **👨‍💼 Workflow 3: Admin Journey (Hành trình Quản trị viên)**

#### **Bước 1: System Overview (Tổng quan hệ thống)**
```http
1. GET {{base_url}}/test/me                   // 🔐 Verify (xác minh) admin role
   Authorization: Bearer {{admin_token}}

2. GET {{base_url}}/category                  // 📂 Xem tất cả danh mục
3. GET {{base_url}}/course                    // 📚 Xem tất cả khóa học
4. GET {{base_url}}/enrollment                // 📋 Xem tất cả enrollment
   Authorization: Bearer {{admin_token}}
```

#### **Bước 2: Content Moderation (Kiểm duyệt nội dung)**
```http
5. PUT {{base_url}}/category/1                // ✏️ Sửa danh mục
   Authorization: Bearer {{admin_token}}
   {
     "name": "Advanced Programming",
     "description": "Updated description (Mô tả đã cập nhật)"
   }

6. PUT {{base_url}}/course/updateCourse/1     // ✏️ Sửa khóa học
   Authorization: Bearer {{admin_token}}
   {
     "title": "Updated Course Title (Tiêu đề khóa học đã cập nhật)",
     "status": "CLOSED"
   }

7. PUT {{base_url}}/enrollment/1              // ✏️ Cập nhật enrollment status
   Authorization: Bearer {{admin_token}}
   {
     "status": "COMPLETED"
   }
```

#### **Bước 3: System Cleanup (Dọn dẹp hệ thống) - Nếu cần**
```http
8. DELETE {{base_url}}/answerOption/1         // 🗑️ Xóa đáp án
   Authorization: Bearer {{admin_token}}

9. DELETE {{base_url}}/question/1             // 🗑️ Xóa câu hỏi
   Authorization: Bearer {{admin_token}}

10. DELETE {{base_url}}/quiz/1                // 🗑️ Xóa quiz
    Authorization: Bearer {{admin_token}}

11. DELETE {{base_url}}/lesson/1              // 🗑️ Xóa bài học
    Authorization: Bearer {{admin_token}}

12. DELETE {{base_url}}/enrollment/1          // 🗑️ Xóa enrollment
    Authorization: Bearer {{admin_token}}

13. DELETE {{base_url}}/course/1              // 🗑️ Xóa khóa học
    Authorization: Bearer {{admin_token}}

14. DELETE {{base_url}}/category/1            // 🗑️ Xóa danh mục
    Authorization: Bearer {{admin_token}}
```

---

## 📊 **Postman Collection Organization (Tổ chức Postman Collection)**

### **📁 Folder Structure (Cấu trúc thư mục):**
```
LMS API Complete Tests/
├── 🏥 01. System Health (Sức khỏe hệ thống)/
│   └── Health Check (Kiểm tra sức khỏe)
├── 🧪 02. Authentication Tests (Test xác thực)/
│   ├── Test Simple - Public (Test đơn giản - Công khai)
│   ├── Get Current User Info (Lấy thông tin người dùng hiện tại)
│   └── Get Current User ID (Lấy ID người dùng hiện tại)
├── 📂 03. Category Management (Quản lý danh mục)/
│   ├── Browse Categories - Public (Duyệt danh mục - Công khai)
│   ├── Get Category By ID - Public (Lấy danh mục theo ID - Công khai)
│   ├── Create Category (Tạo danh mục) - LECTURER+
│   ├── Update Category (Cập nhật danh mục) - LECTURER+  
│   └── Delete Category (Xóa danh mục) - ADMIN
├── 📚 04. Course Management (Quản lý khóa học)/
│   ├── Browse Courses - Public (Duyệt khóa học - Công khai)
│   ├── Get Course Details (Lấy chi tiết khóa học) - Enrolled+ (Đã ghi danh+)
│   ├── Create Course (Tạo khóa học) - LECTURER+
│   ├── Update Course (Cập nhật khóa học) - Owner+ (Chủ sở hữu+)
│   └── Delete Course (Xóa khóa học) - ADMIN
├── 📖 05. Lesson Management (Quản lý bài học)/
│   ├── Get All Lessons (Lấy tất cả bài học) - LECTURER+
│   ├── Get Lesson By ID (Lấy bài học theo ID) - Enrolled+ (Đã ghi danh+)
│   ├── Create Lesson (Tạo bài học) - LECTURER+
│   ├── Update Lesson (Cập nhật bài học) - Owner+ (Chủ sở hữu+)
│   └── Delete Lesson (Xóa bài học) - ADMIN
├── 🎯 06. Quiz Management (Quản lý bài kiểm tra)/
│   ├── Get All Quizzes (Lấy tất cả bài kiểm tra) - LECTURER+
│   ├── Get Quiz By ID (Lấy bài kiểm tra theo ID) - Enrolled+ (Đã ghi danh+)
│   ├── Get Quizzes By Lesson (Lấy bài kiểm tra theo bài học) - Enrolled+ (Đã ghi danh+)
│   ├── Get Quizzes By Course (Lấy bài kiểm tra theo khóa học) - Enrolled+ (Đã ghi danh+)
│   ├── Create Quiz (Tạo bài kiểm tra) - LECTURER+
│   ├── Update Quiz (Cập nhật bài kiểm tra) - Owner+ (Chủ sở hữu+)
│   └── Delete Quiz (Xóa bài kiểm tra) - ADMIN
├── ❓ 07. Question Management (Quản lý câu hỏi)/
│   ├── Get All Questions (Lấy tất cả câu hỏi) - LECTURER+
│   ├── Get Question By ID (Lấy câu hỏi theo ID) - Enrolled+ (Đã ghi danh+)
│   ├── Get Questions By Quiz (Lấy câu hỏi theo bài kiểm tra) - Enrolled+ (Đã ghi danh+)
│   ├── Create Question (Tạo câu hỏi) - LECTURER+
│   ├── Update Question (Cập nhật câu hỏi) - Owner+ (Chủ sở hữu+)
│   └── Delete Question (Xóa câu hỏi) - ADMIN
├── 💡 08. Answer Option Management (Quản lý đáp án)/
│   ├── Get All Answer Options (Lấy tất cả đáp án) - LECTURER+
│   ├── Get Answer Option By ID (Lấy đáp án theo ID) - Enrolled+ (Đã ghi danh+)
│   ├── Get Answer Options By Question (Lấy đáp án theo câu hỏi) - Enrolled+ (Đã ghi danh+)
│   ├── Create Answer Option (Tạo đáp án) - LECTURER+
│   ├── Update Answer Option (Cập nhật đáp án) - Owner+ (Chủ sở hữu+)
│   └── Delete Answer Option (Xóa đáp án) - ADMIN
├── 📝 09. Enrollment Management (Quản lý ghi danh)/
│   ├── Student Self-Enroll (Sinh viên tự ghi danh) - STUDENT
│   ├── Admin Create Enrollment (Admin tạo ghi danh) - LECTURER+
│   ├── Get My Enrollments (Lấy ghi danh của tôi) - STUDENT
│   ├── Get All Enrollments (Lấy tất cả ghi danh) - LECTURER+
│   ├── Get Enrollment By ID (Lấy ghi danh theo ID) - Owner+ (Chủ sở hữu+)
│   ├── Update Enrollment (Cập nhật ghi danh) - LECTURER+
│   └── Delete Enrollment (Xóa ghi danh) - ADMIN
├── 📊 10. Quiz Results (Kết quả bài kiểm tra)/
│   ├── Submit Quiz (Nộp bài kiểm tra) - STUDENT
│   ├── Get My Quiz Results (Lấy kết quả bài kiểm tra của tôi) - STUDENT
│   ├── Get My Best Result (Lấy kết quả tốt nhất của tôi) - STUDENT
│   ├── Get My Course Results (Lấy kết quả khóa học của tôi) - STUDENT
│   ├── Get All Quiz Results (Lấy tất cả kết quả bài kiểm tra) - LECTURER+
│   └── Check Can Take Quiz (Kiểm tra có thể làm bài không) - STUDENT
└── 🔄 11. Complete Workflows (Luồng làm việc hoàn chỉnh)/
    ├── 🎓 Student Complete Journey (Hành trình hoàn chỉnh của sinh viên)
    ├── 👨‍🏫 Lecturer Complete Journey (Hành trình hoàn chỉnh của giảng viên)
    └── 👨‍💼 Admin Complete Journey (Hành trình hoàn chỉnh của quản trị viên)
```

### **🔧 Postman Environment Variables (Biến môi trường Postman):**
```javascript
{
  "base_url": "http://localhost:8083/api",
  "student_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "lecturer_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "admin_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "test_category_id": "1",
  "test_course_id": "1", 
  "test_lesson_id": "1",
  "test_quiz_id": "1",
  "test_question_id": "1",
  "test_answer_option_id": "1",
  "test_enrollment_id": "1"
}
```

### **📝 Postman Collection Variables (Biến collection Postman):**
```javascript
// Collection Pre-request Script (Script tiền yêu cầu của Collection)
pm.request.headers.add({
    key: 'Content-Type',
    value: 'application/json'
});

// Auto-set Authorization header based on endpoint (Tự động thiết lập header Authorization dựa trên endpoint)
const url = pm.request.url.toString();
if (url.includes('/enrollment/enroll') || url.includes('/my-enrollments') || url.includes('/quiz-results/')) {
    pm.request.headers.add({
        key: 'Authorization',
        value: 'Bearer ' + pm.environment.get('student_token')
    });
} else if (url.includes('/createCategory') || url.includes('/createCourse') || url.includes('/createLesson')) {
    pm.request.headers.add({
        key: 'Authorization', 
        value: 'Bearer ' + pm.environment.get('lecturer_token')
    });
} else if (pm.request.method === 'DELETE') {
    pm.request.headers.add({
        key: 'Authorization',
        value: 'Bearer ' + pm.environment.get('admin_token')
    });
}
```

### **✅ Postman Test Scripts (Script test Postman):**

#### **Global Tests (Test toàn cục) - Collection level (mức độ Collection):**
```javascript
// Tests tab - Collection level (Tab Tests - mức độ Collection)
pm.test("Response time is less than 2000ms (Thời gian phản hồi dưới 2000ms)", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

pm.test("Response is JSON (Phản hồi là JSON)", function () {
    pm.response.to.be.json;
});

pm.test("Has success code or error code (Có mã thành công hoặc mã lỗi)", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData.code).to.be.a('number');
});

// Store created IDs for later use (Lưu trữ các ID đã tạo để sử dụng sau)
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    if (jsonData.code === 1000 && jsonData.result && jsonData.result.id) {
        const endpoint = pm.request.url.getPath();
        if (endpoint.includes('/category/createCategory')) {
            pm.environment.set("test_category_id", jsonData.result.id);
        } else if (endpoint.includes('/course/createCourse')) {
            pm.environment.set("test_course_id", jsonData.result.id);
        } else if (endpoint.includes('/lesson/createLesson')) {
            pm.environment.set("test_lesson_id", jsonData.result.id);
        }
        // ... other endpoints (các endpoint khác)
    }
}
```

#### **Specific API Tests (Test API cụ thể):**

**Success Response Tests (Test phản hồi thành công):**
```javascript
// For successful requests (Cho các yêu cầu thành công)
pm.test("Status code is 200 (Mã trạng thái là 200)", function () {
    pm.response.to.have.status(200);
});

pm.test("Success response structure (Cấu trúc phản hồi thành công)", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(1000);
    pm.expect(jsonData).to.have.property('result');
    pm.expect(jsonData.result).to.not.be.null;
});
```

**Error Response Tests (Test phản hồi lỗi):**
```javascript
// For error scenarios (Cho các tình huống lỗi)
pm.test("Error response structure (Cấu trúc phản hồi lỗi)", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData.code).to.not.eql(1000);
});

pm.test("Unauthorized access returns 401 or 403 (Truy cập không được phép trả về 401 hoặc 403)", function () {
    if (pm.response.json().code === 2101 || pm.response.json().code === 2001) {
        pm.expect([401, 403, 200]).to.include(pm.response.code);
    }
});
```

**Data Validation Tests (Test xác thực dữ liệu):**
```javascript
// For specific endpoints (Cho các endpoint cụ thể)
pm.test("Course creation returns valid course object (Tạo khóa học trả về đối tượng khóa học hợp lệ)", function () {
    if (pm.request.url.toString().includes('/course/createCourse')) {
        const jsonData = pm.response.json();
        if (jsonData.code === 1000) {
            pm.expect(jsonData.result).to.have.property('id');
            pm.expect(jsonData.result).to.have.property('title');
            pm.expect(jsonData.result).to.have.property('teacherId');
            pm.expect(jsonData.result).to.have.property('categoryId');
        }
    }
});

pm.test("Quiz submission returns score (Nộp bài kiểm tra trả về điểm số)", function () {
    if (pm.request.url.toString().includes('/quiz-results/submit')) {
        const jsonData = pm.response.json();
        if (jsonData.code === 1000) {
            pm.expect(jsonData.result).to.have.property('score');
            pm.expect(jsonData.result).to.have.property('isPassed');
            pm.expect(jsonData.result).to.have.property('feedback');
        }
    }
});
```
---

### 📊 **Postman Test Scripts**

#### Tự động lưu JWT Token:
```javascript
// Trong Pre-request Script của Collection
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('jwt_token')
});
```

#### Validate Response:
```javascript
// Trong Tests tab
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has success code", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(1000);
});

pm.test("Result is not null", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.result).to.not.be.null;
});
```

---

## 🚨 **Lỗi Thường Gặp & Cách Khắc Phục**

### 🔐 **Authentication Errors (Lỗi xác thực)**

#### ❌ **Lỗi 1: "Token không tồn tại trong request"**
```json
{
    "code": 2001,
    "message": "Token không tồn tại trong request"
}
```

**🔍 Nguyên nhân:**
- Không có header `Authorization`
- Header không đúng format

**✅ Cách khắc phục:**
```http
// ❌ SAI
Authorization: your_token_here

// ✅ ĐÚNG  
Authorization: Bearer your_token_here
```

#### ❌ **Lỗi 2: "Token không hợp lệ"**
```json
{
    "code": 2002,
    "message": "Token không hợp lệ"
}
```

**🔍 Nguyên nhân:**
- Token đã bị modify
- Secret key không khớp
- Token format sai

**✅ Cách khắc phục:**
1. Lấy token mới từ Identity Service
2. Kiểm tra JWT secret trong application.yaml
3. Validate token format trên [jwt.io](https://jwt.io)

#### ❌ **Lỗi 3: "Token đã hết hạn"**
```json
{
    "code": 2003,
    "message": "Token đã hết hạn"
}
```

**✅ Cách khắc phục:**
- Login lại để lấy token mới
- Implement refresh token mechanism

### 🔒 **Authorization Errors (Lỗi phân quyền)**

#### ❌ **Lỗi 4: "Không có quyền truy cập"**
```json
{
    "code": 2101,
    "message": "Không có quyền truy cập"
}
```

**🔍 Nguyên nhân:**
- User không có role phù hợp
- Endpoint yêu cầu quyền cao hơn

**✅ Cách khắc phục:**
```java
// Ví dụ: Endpoint yêu cầu ADMIN nhưng user chỉ là STUDENT
@PreAuthorize("hasRole('ADMIN')")  // ❌ STUDENT không có quyền

// Giải pháp: Dùng user có role phù hợp hoặc thay đổi logic
@PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
```

#### ❌ **Lỗi 5: "Chỉ dành cho học viên"**
```json
{
    "code": 2103,
    "message": "Chỉ dành cho học viên"
}
```

**🔍 Nguyên nhân:**
- API chỉ dành cho STUDENT nhưng dùng token LECTURER/ADMIN

**✅ Cách khắc phục:**
- Sử dụng token của user có role STUDENT

### 💾 **Database Errors (Lỗi cơ sở dữ liệu)**

#### ❌ **Lỗi 6: "Khóa học không tồn tại"**
```json
{
    "code": 1004,
    "message": "Khóa học không tồn tại"
}
```

**🔍 Nguyên nhân:**
- courseId không tồn tại trong database
- Course đã bị xóa

**✅ Cách khắc phục:**
1. Kiểm tra ID có đúng không
2. Tạo course trước khi test
3. Kiểm tra database:
```sql
SELECT * FROM courses WHERE id = 1;
```

#### ❌ **Lỗi 7: Connection refused**
```
Could not create connection to database server
```

**🔍 Nguyên nhân:**
- MySQL server không chạy
- Connection string sai
- Username/password sai

**✅ Cách khắc phục:**
1. Khởi động MySQL:
```bash
# Windows
net start mysql

# macOS
brew services start mysql

# Linux  
sudo systemctl start mysql
```

2. Kiểm tra connection:
```sql
mysql -u root -p
USE LMS;
SHOW TABLES;
```

### 📁 **File Upload Errors (Lỗi upload file)**

#### ❌ **Lỗi 8: "File quá lớn"**
```json
{
    "code": 1005,
    "message": "File quá lớn. Kích thước tối đa cho phép là 200MB"
}
```

**🔍 Nguyên nhân:**
- File upload vượt quá giới hạn cho phép (200MB)

**✅ Cách khắc phục:**
1. Nén/giảm kích thước file
2. Tăng giới hạn trong application.yaml:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB
```

#### ❌ **Lỗi 9: "File rỗng"**
```json
{
    "code": 1007,
    "message": "File rỗng"
}
```

**🔍 Nguyên nhân:**
- Không chọn file trong form-data
- File có kích thước 0 bytes

**✅ Cách khắc phục:**
- Kiểm tra đã chọn đúng file chưa
- Đảm bảo file không bị corrupt

### 📝 **Business Logic Errors (Lỗi logic nghiệp vụ)**

#### ❌ **Lỗi 10: "Đã đăng ký khóa học này rồi"**
```json
{
    "code": 2108,
    "message": "Đã đăng ký khóa học này rồi"
}
```

**🔍 Nguyên nhân:**
- Student đã enroll course này rồi (status = ACTIVE)

**✅ Cách khắc phục:**
1. Kiểm tra enrollment:
```sql
SELECT * FROM enrollment 
WHERE student_id = 1 AND course_id = 1 AND status = 'ACTIVE';
```

2. Nếu muốn re-enroll, xóa record cũ:
```sql
DELETE FROM enrollment 
WHERE student_id = 1 AND course_id = 1;
```

#### ❌ **Lỗi 11: "Chưa đăng ký khóa học"**
```json
{
    "code": 2106,
    "message": "Chưa đăng ký khóa học"
}
```

**🔍 Nguyên nhân:**
- Student cố truy cập lesson/quiz mà chưa enroll course

**✅ Cách khắc phục:**
1. Enroll course trước:
```http
POST /api/enrollment/enroll
{
    "courseId": 1
}
```

2. Kiểm tra enrollment status:
```http
GET /api/enrollment/my-enrollments
```

#### ❌ **Lỗi 12: "Đã hết số lần làm bài"**
```json
{
    "code": 1017,
    "message": "Đã hết số lần làm bài"
}
```

**🔍 Nguyên nhân:**
- Student đã làm quiz vượt quá maxAttempts

**✅ Cách khắc phục:**
1. Kiểm tra số lần đã làm:
```sql
SELECT COUNT(*) FROM quiz_results 
WHERE quiz_id = 1 AND student_id = 1;
```

2. Lecturer có thể tăng maxAttempts:
```http
PUT /api/quiz/1
{
    "maxAttempts": 5
}
```

### 🔧 **Configuration Errors (Lỗi cấu hình)**

#### ❌ **Lỗi 13: Server không khởi động**
```
APPLICATION FAILED TO START
***************************

Description:
Failed to configure a DataSource
```

**🔍 Nguyên nhân:**
- Database connection thất bại
- MySQL service không chạy
- Credentials sai

**✅ Cách khắc phục:**
1. Kiểm tra MySQL service:
```bash
# Check status
sudo systemctl status mysql

# Start MySQL
sudo systemctl start mysql
```

2. Test connection thủ công:
```bash
mysql -h localhost -u root -p
```

3. Kiểm tra application.yaml:
```yaml
spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/LMS"  # ✅ Đảm bảo database tồn tại
    username: root                          # ✅ Username đúng
    password: abc123-                       # ✅ Password đúng
```

#### ❌ **Lỗi 14: CORS Policy Error (Frontend)**
```
Access to XMLHttpRequest at 'http://localhost:8083/api/course' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**🔍 Nguyên nhân:**
- CORS chưa được cấu hình đúng cho frontend origin

**✅ Cách khắc phục:**
1. Cập nhật application.yaml:
```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000}
```

2. Khởi động lại server sau khi thay đổi config

---

## 🛠️ **Debugging Guide (Hướng dẫn Debug)**

### 🔍 **Log Analysis (Phân tích Log)**

#### 1. **Enable Debug Logging**
```yaml
# application.yaml
logging:
  level:
    com.app.lms: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

#### 2. **Common Log Patterns**
```bash
# JWT Authentication Success
JWT Authentication thành công cho user: student@example.com

# JWT Authentication Failed  
JWT Authentication thất bại: Token đã hết hạn

# Authorization Denied
Authorization DENIED for user: john_doe - Resource: hasRole('ADMIN')

# Business Logic Error
Student 1 cannot take quiz 2: Chưa đăng ký khóa học
```

### 🗄️ **Database Debugging**

#### Useful SQL Queries:
```sql
-- Kiểm tra user enrollment
SELECT 
    e.id, e.student_id, c.title as course_name, e.status, e.enrolled_at
FROM enrollment e 
JOIN courses c ON e.course_id = c.id
WHERE e.student_id = 1;

-- Kiểm tra quiz results
SELECT 
    qr.*, q.title as quiz_title, qr.score, qr.is_passed
FROM quiz_results qr
JOIN quizzes q ON qr.quiz_id = q.id
WHERE qr.student_id = 1
ORDER BY qr.taken_at DESC;

-- Kiểm tra course structure
SELECT 
    c.title as course_title,
    l.title as lesson_title,
    q.title as quiz_title,
    COUNT(qs.id) as question_count
FROM courses c
LEFT JOIN lesson l ON c.id = l.course_id
LEFT JOIN quizzes q ON l.id = q.lesson_id
LEFT JOIN questions qs ON q.id = qs.quiz_id
WHERE c.id = 1
GROUP BY c.id, l.id, q.id;
```

---

## 🎯 **Best Practices (Thực hành tốt nhất)**

### 🔐 **Security Best Practices**

#### 1. **JWT Token Management**
```javascript
// ✅ GOOD - Store token securely
localStorage.setItem('jwt_token', token);

// ❌ BAD - Don't log tokens
console.log('Token:', token); // Có thể bị lộ trong production
```

#### 2. **API Call Pattern**
```javascript
// ✅ GOOD - Always handle errors
try {
    const response = await apiClient.get('/course/1');
    if (response.data.code === 1000) {
        return response.data.result;
    } else {
        throw new Error(response.data.message);
    }
} catch (error) {
    if (error.response?.status === 401) {
        // Token expired, redirect to login
        window.location.href = '/login';
    }
    throw error;
}
```

### 📊 **Testing Best Practices**

#### 1. **Postman Collection Organization**
```
LMS API Tests/
├── 📁 01. Public APIs/
│   ├── Health Check
│   ├── Browse Categories  
│   └── Browse Courses
├── 📁 02. Authentication/
│   ├── Test Valid Token
│   └── Test Invalid Token
├── 📁 03. Course Management/
│   ├── Create Category
│   ├── Create Course
│   └── Update Course
├── 📁 04. Student Flow/
│   ├── Enroll Course
│   ├── View Enrolled Courses
│   └── Take Quiz
└── 📁 05. Error Scenarios/
    ├── Invalid IDs
    ├── Permission Denied
    └── Business Logic Violations
```

#### 2. **Test Data Setup**
```javascript
// Pre-request Script - Setup test data
pm.globals.set("test_category_id", 1);
pm.globals.set("test_course_id", 1);
pm.globals.set("test_student_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...");
pm.globals.set("test_lecturer_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...");
```

---

## 📚 **API Reference Quick Guide**

### 🔓 **Public Endpoints (Không cần token)**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Kiểm tra sức khỏe hệ thống |
| GET | `/api/category` | Lấy danh sách danh mục |
| GET | `/api/category/{id}` | Chi tiết danh mục |
| GET | `/api/course` | Lấy danh sách khóa học |
| GET | `/api/test/simple` | Test API đơn giản |

### 👨‍🎓 **Student Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/enrollment/enroll` | Đăng ký khóa học |
| GET | `/api/enrollment/my-enrollments` | Xem khóa học đã đăng ký |
| GET | `/api/course/{id}` | Chi tiết khóa học (đã enroll) |
| GET | `/api/lesson/{id}` | Chi tiết bài học (đã enroll) |
| POST | `/api/quiz-results/submit` | Nộp bài kiểm tra |
| GET | `/api/quiz-results/my-results/{quizId}` | Xem kết quả bài làm |
| GET | `/api/quiz-results/can-take/{quizId}` | Kiểm tra có thể làm bài không |

### 👨‍🏫 **Lecturer Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/category/createCategory` | Tạo danh mục |
| POST | `/api/course/createCourse` | Tạo khóa học |
| PUT | `/api/course/updateCourse/{id}` | Cập nhật khóa học |
| POST | `/api/lesson/createLesson` | Tạo bài học |
| PUT | `/api/lesson/updateLesson/{id}` | Cập nhật bài học |
| POST | `/api/quiz` | Tạo bài kiểm tra |
| PUT | `/api/quiz/{id}` | Cập nhật bài kiểm tra |
| POST | `/api/question` | Tạo câu hỏi |
| POST | `/api/answerOption` | Tạo đáp án |
| GET | `/api/quiz-results/quiz/{quizId}/all-results` | Xem kết quả tất cả học viên |

### 👨‍💼 **Admin Endpoints**
| Method | Endpoint | Description |
|--------|----------|-------------|
| DELETE | `/api/category/{id}` | Xóa danh mục |
| DELETE | `/api/course/{id}` | Xóa khóa học |
| DELETE | `/api/lesson/{id}` | Xóa bài học |
| DELETE | `/api/quiz/{id}` | Xóa bài kiểm tra |
| DELETE | `/api/question/{id}` | Xóa câu hỏi |
| DELETE | `/api/answerOption/{id}` | Xóa đáp án |
| DELETE | `/api/enrollment/{id}` | Xóa đăng ký |
| GET | `/api/enrollment` | Xem tất cả đăng ký |

---

## 🔧 **Environment Configuration (Cấu hình môi trường)**

### 🏠 **Development Environment**
```yaml
# application-dev.yaml
server:
  port: 8083

spring:
  profiles:
    active: dev
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    com.app.lms: DEBUG
    root: INFO

cors:
  allowed-origins: http://localhost:3000,http://127.0.0.1:3000
```

### 🚀 **Production Environment**
```yaml
# application-prod.yaml
server:
  port: 8080

spring:
  profiles:
    active: prod
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: none

logging:
  level:
    com.app.lms: WARN
    root: ERROR

cors:
  allowed-origins: https://yourdomain.com,https://www.yourdomain.com
```

### 🧪 **Testing Environment**  
```yaml
# application-test.yaml
spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/LMS_TEST"
  jpa:
    hibernate:
      ddl-auto: create-drop
```

---

## 🎓 **Learning Path (Lộ trình học tập)**

### **Cho Developer mới:**

#### **Bước 1: Hiểu cơ bản (1-2 tuần)**
1. 📚 Spring Boot fundamentals
2. 🗄️ JPA/Hibernate basics
3. 🔐 Spring Security concepts
4. 📡 REST API principles

#### **Bước 2: Thực hành với dự án (2-3 tuần)**
1. 🚀 Setup và chạy project
2. 📋 Test các API với Postman  
3. 🔍 Đọc và hiểu code flow
4. 🛠️ Fix bugs và thêm features nhỏ

#### **Bước 3: Nâng cao (3-4 tuần)**
1. 📈 Performance optimization
2. 🧪 Viết unit tests
3. 🐳 Docker deployment
4. 📊 Monitoring và logging

---

## 🤝 **Contributing Guide (Hướng dẫn đóng góp)**

### **Git Workflow:**
```bash
# 1. Tạo feature branch
git checkout -b feature/add-course-rating

# 2. Implement feature
# ... code changes ...

# 3. Test thoroughly
mvn test
# Manual testing with Postman

# 4. Commit with descriptive message
git commit -m "feat: add course rating system

- Add Rating entity and repository
- Implement rating CRUD APIs  
- Add authorization for rating actions
- Update course response to include average rating"

# 5. Push và tạo Pull Request
git push origin feature/add-course-rating
```

### **Code Review Checklist:**
- [ ] ✅ Code compile và test pass
- [ ] 🔒 Security đúng (authentication/authorization)
- [ ] 📝 API documentation đầy đủ
- [ ] 🧪 Test cases cover happy path và error scenarios
- [ ] 📊 Performance impact acceptable
- [ ] 🎨 Code style consistent

---

## 📞 **Support & Contact**

### **Khi gặp vấn đề:**

1. **🔍 Kiểm tra logs** trong console
2. **📖 Đọc error message** cẩn thận
3. **🔧 Thử các bước khắc phục** trong tài liệu này
4. **🌐 Search** trên StackOverflow
5. **💬 Liên hệ team** nếu vẫn không giải quyết được

### **Resources hữu ích:**
- 📚 [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- 🔐 [Spring Security Reference](https://spring.io/projects/spring-security)
- 🗄️ [JPA/Hibernate Guide](https://hibernate.org/orm/documentation/)
- 🔧 [Postman Learning Center](https://learning.postman.com/)

---

## 🎯 **Kết luận**

Hệ thống LMS này cung cấp một foundation mạnh mẽ cho việc xây dựng ứng dụng học tập trực tuyến. Với kiến trúc rõ ràng, security chặt chẽ và API đầy đủ, bạn có thể:

- **👨‍💻 Phát triển frontend** với confidence
- **📱 Xây dựng mobile app** dễ dàng  
- **⚡ Scale hệ thống** khi cần thiết
- **🔧 Maintain code** hiệu quả

**Chúc bạn phát triển thành công! 🚀**
