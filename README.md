# 🎓 LMS — Learning Management System

> **Backend API** cho hệ thống quản lý học tập trực tuyến, xây dựng bằng **Spring Boot 3.2.5**.  
> Hỗ trợ đầy đủ luồng: tạo khóa học → phê duyệt → ghi danh → thanh toán VNPay → học bài → làm quiz.

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt & Chạy](#cài-đặt--chạy)
    - [Chạy với Docker (Khuyến nghị)](#chạy-với-docker-khuyến-nghị)
    - [Chạy thủ công (Local)](#chạy-thủ-công-local)
- [Cấu hình môi trường](#cấu-hình-môi-trường)
- [Tài liệu API](#tài-liệu-api)
- [Phân quyền hệ thống](#phân-quyền-hệ-thống)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Luồng nghiệp vụ chính](#luồng-nghiệp-vụ-chính)
- [Tích hợp ngoài](#tích-hợp-ngoài)

---

## Tổng quan

LMS là hệ thống quản lý học tập trực tuyến với 3 vai trò người dùng:

| Vai trò | Khả năng |
|---|---|
| **Student** | Duyệt khóa học, thanh toán, ghi danh, xem bài giảng (YouTube), làm quiz, theo dõi tiến độ |
| **Lecturer** | Tạo & quản lý khóa học/bài học/quiz/câu hỏi/đáp án, xem kết quả học viên |
| **Admin** | Phê duyệt/từ chối khóa học, quản lý toàn bộ hệ thống, quản lý mã giảm giá |

**Tính năng nổi bật:**

- Xác thực bằng **JWT** — không lưu session, stateless hoàn toàn
- Phân quyền chi tiết đến từng endpoint bằng **Spring Security + @PreAuthorize**
- Thanh toán qua **VNPay Payment Gateway**
- Hỗ trợ mã giảm giá (Coupon) — phần trăm hoặc số tiền cố định
- Cache dữ liệu bằng **Redis** để tăng hiệu năng
- Nhúng video bài giảng từ **YouTube** (không host video trực tiếp)
- Theo dõi tiến độ xem video theo giây (watchedSeconds)
- Hệ thống quiz tự động chấm điểm với feedback chi tiết
- Workflow phê duyệt khóa học: PENDING → APPROVED / REJECTED
- Gửi thông báo bất đồng bộ (@Async) qua **Notification Service** khi có sự kiện
- Tích hợp **Identity Service** qua OpenFeign để lấy thông tin giảng viên
- Hỗ trợ **Docker** với multi-stage build và health check

---

## Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────────┐
│                        Client (Frontend)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP / JWT
┌──────────────────────────▼──────────────────────────────────┐
│                  Spring Boot Application                     │
│                                                             │
│  Request → CORS Filter → JWT Filter → Security Config        │
│                                   ↓                         │
│            Controller → Service → Repository                 │
│                 ↓            ↓          ↓                    │
│               DTOs       Business    JPA/DB                  │
│             Mappers        Logic    (MySQL)                   │
└──────┬──────────────┬──────────────────┬────────────────────┘
       │              │                  │
  ┌────▼────┐   ┌─────▼──────┐   ┌──────▼──────┐
  │  Redis  │   │  VNPay GW  │   │ Notification│
  │ (Cache) │   │ (Payment)  │   │  Service    │
  └─────────┘   └────────────┘   └─────────────┘
```

**Layered Architecture:**

```
Controller Layer   →  Nhận request, validate input, trả response
Service Layer      →  Xử lý business logic, transaction
Repository Layer   →  Tương tác database qua Spring Data JPA
Entity Layer       →  Mapping với bảng database
DTO Layer          →  Request/Response objects (tách biệt với Entity)
Mapper Layer       →  Chuyển đổi giữa Entity ↔ DTO (MapStruct)
```

---

## Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Security | Spring Security + JJWT | 0.11.5 |
| ORM | Spring Data JPA / Hibernate | — |
| Database | MySQL | 8.0 |
| Cache | Redis | Alpine |
| Mapper | MapStruct | 1.6.3 |
| HTTP Client | OpenFeign (Spring Cloud) | 2023.0.1 |
| Async HTTP | Spring WebFlux (WebClient) | — |
| Build Tool | Maven | 3.9.x |
| Container | Docker + Docker Compose | — |
| Payment | VNPay Payment Gateway | v2.1.0 |

---

## Yêu cầu hệ thống

**Chạy bằng Docker (khuyến nghị):**
- Docker Engine 20.10+
- Docker Compose 2.0+
- RAM tối thiểu: 4GB

**Chạy thủ công:**
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis (bất kỳ phiên bản nào)

---

## Cài đặt & Chạy

### Chạy với Docker (Khuyến nghị)

**Bước 1:** Clone repository và chuẩn bị file cấu hình

```bash
git clone <repository-url>
cd lms

# Tạo file .env từ template
cp .env.example .env
```

**Bước 2:** Chỉnh sửa file `.env` — **bắt buộc điền các giá trị sau:**

```env
MYSQL_ROOT_PASSWORD=your_strong_password
JWT_SECRET=your_jwt_secret_key_base64
VNPAY_TMN_CODE=your_vnpay_tmn_code
VNPAY_SECRET_KEY=your_vnpay_secret_key
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://your-domain/api/payment/vnpay-return
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

**Bước 3:** Build và khởi động

```bash
# Khởi động đầy đủ stack (MySQL + Redis + App)
docker compose up -d --build

# Xem log ứng dụng
docker compose logs -f lms-app

# Kiểm tra health
curl http://localhost:8083/api/health
```

**Bước 4 (Tùy chọn):** Nạp dữ liệu mẫu để test

```bash
# Chờ app khởi động xong (~60 giây), rồi chạy:
docker exec -i lms-mysql mysql -uroot -p<MYSQL_ROOT_PASSWORD> LMS < seed-data.sql
```

> **Nếu đã có MySQL sẵn ở local**, dùng cấu hình app-only để tránh xung đột port:
> ```bash
> docker compose -f docker-compose.app-only.yml up -d --build
> ```

---

### Chạy thủ công (Local)

**Bước 1:** Tạo database MySQL

```sql
CREATE DATABASE LMS CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Bước 2:** Cấu hình `src/main/resources/application.yaml`

Các biến môi trường cần thiết (có thể đặt trong shell hoặc file `.env` với thư viện `spring-dotenv`):

```bash
export MYSQL_ROOT_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret
export VNPAY_TMN_CODE=your_tmn_code
export VNPAY_SECRET_KEY=your_secret_key
export VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
export VNPAY_RETURN_URL=http://localhost:8083/api/payment/vnpay-return
export CORS_ALLOWED_ORIGINS=http://localhost:3000
export NOTIFICATION_SERVICE_URL=http://localhost:8082
export IDENTITY_SERVICE_URL=http://localhost:8082
```

**Bước 3:** Build và chạy

```bash
# Build (bỏ qua test)
mvn clean package -DskipTests

# Chạy ứng dụng
java -jar target/LMS-0.0.1-SNAPSHOT.jar

# Hoặc dùng Maven directly
mvn spring-boot:run
```

Ứng dụng khởi động tại: `http://localhost:8083`

---

## Cấu hình môi trường

Xem file [`.env.example`](.env.example) để biết đầy đủ các biến cấu hình. Dưới đây là các biến **quan trọng nhất**:

| Biến | Mô tả | Ví dụ |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | Mật khẩu MySQL root | `StrongPass123!` |
| `MYSQL_DATABASE` | Tên database | `LMS` |
| `JWT_SECRET` | Secret key ký JWT (base64, tối thiểu 32 ký tự) | `2nIXqNA3pjyrUvlwxmd8...` |
| `CORS_ALLOWED_ORIGINS` | Các origin được phép CORS | `http://localhost:3000` |
| `VNPAY_TMN_CODE` | Mã merchant VNPay | `DEMO1234` |
| `VNPAY_SECRET_KEY` | Secret key VNPay | `DEMOSECRETKEY` |
| `VNPAY_RETURN_URL` | URL callback sau thanh toán | `http://domain/api/payment/vnpay-return` |
| `NOTIFICATION_SERVICE_URL` | URL của Notification Service | `http://localhost:8082` |
| `IDENTITY_SERVICE_URL` | URL của Identity Service | `http://localhost:8082` |
| `REDIS_HOST` | Host Redis | `localhost` |

> ⚠️ **Lưu ý bảo mật**: Không commit file `.env` chứa giá trị thật lên Git. File `.env` đã được thêm vào `.gitignore`.

---

## Tài liệu API

**Base URL:** `http://localhost:8083/api`

**Authentication:** Tất cả API (trừ Public) đều yêu cầu header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

### 🌐 Public Endpoints (Không cần đăng nhập)

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/health` | Kiểm tra trạng thái server |
| `GET` | `/test/simple` | Test endpoint đơn giản |
| `GET` | `/category` | Lấy danh sách tất cả danh mục |
| `GET` | `/category/{id}` | Lấy chi tiết danh mục |
| `GET` | `/course` | Lấy danh sách khóa học đã phê duyệt |
| `GET` | `/payment/vnpay-return` | Callback redirect từ VNPay |
| `POST` | `/payment/vnpay-ipn` | Server-to-server notification từ VNPay |

---

### 🔐 Authentication Test

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/test/me` | Xem thông tin user hiện tại từ JWT |
| `GET` | `/test/my-id` | Lấy ID user hiện tại |

---

### 📂 Category Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/category/createCategory` | ADMIN, LECTURER | Tạo danh mục mới |
| `PUT` | `/category/{id}` | ADMIN, LECTURER | Cập nhật danh mục |
| `DELETE` | `/category/{id}` | ADMIN | Xóa danh mục |

---

### 📚 Course Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/course/createCourse` | ADMIN, LECTURER | Tạo khóa học (upload ảnh, multipart) |
| `GET` | `/course/{id}` | Student đã enroll, LECTURER, ADMIN | Xem chi tiết khóa học |
| `PUT` | `/course/updateCourse/{id}` | ADMIN, Lecturer chủ sở hữu | Cập nhật khóa học |
| `DELETE` | `/course/{id}` | ADMIN | Xóa khóa học |
| `GET` | `/course/admin/all` | ADMIN | Xem tất cả khóa học (kể cả PENDING) |
| `GET` | `/course/admin/pending` | ADMIN | Danh sách chờ phê duyệt |
| `POST` | `/course/{id}/approve` | ADMIN | Phê duyệt hoặc từ chối khóa học |
| `GET` | `/course/lecturer/my-courses` | LECTURER | Xem khóa học của mình |

**Tạo khóa học (multipart/form-data):**
```
POST /api/course/createCourse
Content-Type: multipart/form-data

course: {"title":"Java Spring Boot","description":"...","price":500000,"categoryId":1}
file: [image.jpg]
```

**Phê duyệt / từ chối:**
```json
POST /api/course/{id}/approve
{
  "approvalStatus": "APPROVED"   // hoặc "REJECTED"
  "rejectionReason": "Nội dung chưa đạt"   // bắt buộc nếu REJECTED
}
```

---

### 📖 Lesson Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/lesson/createLesson` | ADMIN, Lecturer chủ sở hữu course | Tạo bài học |
| `GET` | `/lesson/{id}` | Student đã enroll, LECTURER, ADMIN | Xem bài học |
| `GET` | `/lesson` | ADMIN, LECTURER | Xem tất cả bài học |
| `PUT` | `/lesson/updateLesson/{id}` | ADMIN, Lecturer sở hữu | Cập nhật bài học |
| `DELETE` | `/lesson/{id}` | ADMIN, Lecturer sở hữu | Xóa bài học (không xóa được bài đang OPEN) |

**Tạo bài học:**
```json
POST /api/lesson/createLesson
{
  "courseId": 1,
  "title": "Giới thiệu Spring Boot",
  "description": "Bài giảng đầu tiên",
  "orderIndex": 1,
  "status": "OPEN",
  "duration": 30,
  "youtubeUrl": "https://www.youtube.com/watch?v=xxxxx"
}
```

---

### 📊 Lesson Progress

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `PUT` | `/progress/save` | STUDENT | Lưu tiến độ xem video |
| `GET` | `/progress/lesson/{lessonId}` | STUDENT | Xem tiến độ 1 bài học |
| `GET` | `/progress/course/{courseId}` | STUDENT | Xem tiến độ toàn khóa học |

```json
PUT /api/progress/save
{
  "lessonId": 1,
  "watchedSeconds": 300,
  "totalSeconds": 600,
  "lastPosition": 300
}
```

> Bài học tự động được đánh dấu **hoàn thành** khi `watchedSeconds >= 90% totalSeconds`.

---

### 🎯 Quiz Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/quiz` | ADMIN, Lecturer sở hữu | Tạo quiz |
| `GET` | `/quiz/{id}` | Student đã enroll, LECTURER, ADMIN | Xem quiz |
| `GET` | `/quiz/lesson/{lessonId}` | Student đã enroll, LECTURER, ADMIN | Quiz theo bài học |
| `GET` | `/quiz/course/{courseId}` | Student đã enroll, LECTURER, ADMIN | Quiz theo khóa học |
| `PUT` | `/quiz/{id}` | ADMIN, Lecturer sở hữu | Cập nhật quiz |
| `DELETE` | `/quiz/{id}` | ADMIN, Lecturer sở hữu | Xóa quiz |

```json
POST /api/quiz
{
  "lessonId": 1,
  "title": "Kiểm tra Spring Boot",
  "timeLimit": 30,
  "maxAttempts": 3,
  "passScore": 70.0
}
```

---

### ❓ Question & Answer Option

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/question` | ADMIN, Lecturer sở hữu | Tạo câu hỏi |
| `GET` | `/question/quiz/{quizId}` | Student đã enroll, LECTURER, ADMIN | Câu hỏi theo quiz |
| `PUT` | `/question/{id}` | ADMIN, Lecturer sở hữu | Cập nhật câu hỏi |
| `DELETE` | `/question/{id}` | ADMIN | Xóa câu hỏi |
| `POST` | `/answerOption` | ADMIN, Lecturer sở hữu | Tạo đáp án |
| `GET` | `/answerOption/question/{questionId}` | Student đã enroll, LECTURER, ADMIN | Đáp án theo câu hỏi |
| `PUT` | `/answerOption/{id}` | ADMIN, Lecturer sở hữu | Cập nhật đáp án |
| `DELETE` | `/answerOption/{id}` | ADMIN | Xóa đáp án |

**Loại câu hỏi (questionType):** `MULTIPLE_CHOICE`, `TRUE_FALSE`, `SHORT_ANSWER`

> **Ràng buộc:** Câu TRUE_FALSE tối đa 2 đáp án. Chỉ 1 đáp án đúng cho MULTIPLE_CHOICE và TRUE_FALSE.

---

### 📝 Enrollment Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/enrollment/enroll` | STUDENT | Ghi danh khóa học (miễn phí) |
| `GET` | `/enrollment/my-enrollments` | STUDENT | Danh sách khóa học đã ghi danh |
| `GET` | `/enrollment/check/{courseId}` | STUDENT | Kiểm tra đã ghi danh chưa |
| `GET` | `/enrollment` | ADMIN, LECTURER | Xem tất cả enrollment |
| `POST` | `/enrollment/createEnrollment` | ADMIN, LECTURER | Tạo enrollment thủ công |
| `PUT` | `/enrollment/{id}` | ADMIN, LECTURER | Cập nhật trạng thái enrollment |
| `DELETE` | `/enrollment/{id}` | ADMIN | Xóa enrollment |

---

### 📊 Quiz Results

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/quiz-results/submit` | STUDENT | Nộp bài làm quiz |
| `GET` | `/quiz-results/my-results/{quizId}` | STUDENT | Tất cả lần làm của tôi |
| `GET` | `/quiz-results/my-best-result/{quizId}` | STUDENT | Kết quả tốt nhất |
| `GET` | `/quiz-results/my-course-results/{courseId}` | STUDENT | Kết quả theo khóa học |
| `GET` | `/quiz-results/my-all-results` | STUDENT | Tất cả kết quả của tôi |
| `GET` | `/quiz-results/can-take/{quizId}` | STUDENT | Kiểm tra còn được làm bài không |
| `GET` | `/quiz-results/quiz/{quizId}/all-results` | ADMIN, LECTURER | Kết quả tất cả học viên |

**Nộp bài:**
```json
POST /api/quiz-results/submit
{
  "quizId": 1,
  "answers": {
    "1": "2",   // questionId: answerOptionId
    "2": "5",
    "3": "8"
  },
  "timeTaken": 20
}
```

**Response:**
```json
{
  "code": 1000,
  "result": {
    "score": 66.67,
    "totalQuestions": 3,
    "correctAnswers": 2,
    "isPassed": false,
    "attemptNumber": 1,
    "feedback": "Câu 1: Đúng\nCâu 2: Đúng\nCâu 3: Sai (Đáp án đúng: Java)\n"
  }
}
```

---

### 💳 Payment (VNPay)

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/payment/create` | STUDENT | Tạo giao dịch, nhận link thanh toán |
| `GET` | `/payment/history` | STUDENT | Lịch sử giao dịch của tôi |
| `GET` | `/payment/{id}` | STUDENT, ADMIN | Chi tiết giao dịch |
| `GET` | `/payment/admin/all` | ADMIN | Tất cả giao dịch |

```json
POST /api/payment/create
{
  "courseId": 1,
  "couponCode": "WELCOME2026",   // optional
  "bankCode": "VNBANK"           // optional
}
```

**Response chứa `paymentUrl`** — frontend redirect user đến URL này để thanh toán.  
Sau thanh toán, VNPay redirect về `/payment/vnpay-return` và tự động tạo enrollment nếu thành công.

---

### 🏷️ Coupon Management

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| `POST` | `/coupon` | ADMIN | Tạo mã giảm giá |
| `GET` | `/coupon` | ADMIN | Xem tất cả mã giảm giá |
| `PUT` | `/coupon/{id}` | ADMIN | Cập nhật mã giảm giá |
| `DELETE` | `/coupon/{id}` | ADMIN | Xóa mã giảm giá |
| `POST` | `/coupon/validate` | STUDENT, ADMIN | Kiểm tra mã giảm giá |
| `GET` | `/coupon/available/{courseId}` | STUDENT, ADMIN | Mã giảm giá có thể dùng cho khóa học |

**Tạo coupon:**
```json
POST /api/coupon
{
  "code": "SALE20",
  "discountType": "PERCENTAGE",   // hoặc "FIXED_AMOUNT"
  "discountValue": 20,
  "minOrderValue": 100000,
  "maxDiscount": 200000,          // giới hạn tối đa (cho PERCENTAGE)
  "usageLimit": 100,
  "startDate": "2026-01-01T00:00:00+07:00",
  "endDate": "2026-12-31T23:59:59+07:00",
  "applicableCourseId": null      // null = áp dụng cho tất cả
}
```

---

### Response Format chuẩn

Tất cả API đều trả về format sau:

**Thành công:**
```json
{
  "code": 1000,
  "result": { ... }
}
```

**Lỗi:**
```json
{
  "code": 2001,
  "message": "Vui lòng đăng nhập để tiếp tục"
}
```

**Một số mã lỗi thường gặp:**

| Code | Ý nghĩa |
|---|---|
| `1000` | Thành công |
| `2001` | Token không tồn tại |
| `2002` | Token không hợp lệ |
| `2003` | Token hết hạn |
| `2101` | Không có quyền truy cập |
| `2103` | Chỉ dành cho học viên |
| `2106` | Chưa ghi danh khóa học |
| `2108` | Đã ghi danh rồi |
| `1017` | Hết số lần làm bài |

---

## Phân quyền hệ thống

Phân quyền hoạt động theo 2 tầng:

**Tầng 1 — Role-based (`@PreAuthorize`):**  
Kiểm tra vai trò (STUDENT / LECTURER / ADMIN) từ JWT token trước khi vào controller.

**Tầng 2 — Resource-based (`AuthorizationService`):**  
Kiểm tra quyền sở hữu tài nguyên cụ thể. Ví dụ: Lecturer chỉ được sửa khóa học **của chính mình**.

```
ADMIN      → Tất cả quyền
LECTURER   → Tạo course/lesson/quiz, chỉ sửa/xóa của mình
STUDENT    → Enroll, xem bài (đã enroll), làm quiz, thanh toán
```

**JWT Token cần chứa các claims:**

| Claim | Kiểu | Mô tả |
|---|---|---|
| `userId` hoặc `sub` | Long | ID người dùng |
| `userType` | String | `STUDENT` / `LECTURER` / `ADMIN` |
| `email` | String | Email |
| `full_name` | String | Họ tên |
| `is_admin` | Boolean | Có phải admin không |
| `account_id` | Long | ID tài khoản |

---

## Cấu trúc dự án

```
src/main/java/com/app/lms/
├── annotation/          # Custom annotations (@CurrentUser, @CurrentUserId)
├── config/              # Cấu hình (Security, CORS, Cache, VNPay, WebMVC)
├── controller/          # REST Controllers
├── dto/
│   ├── auth/            # UserTokenInfo (thông tin từ JWT)
│   ├── request/         # Request objects (phân loại theo feature)
│   └── response/        # Response objects
├── entity/              # JPA Entities
├── enums/               # Enumerations
├── exception/           # GlobalExceptionHandler + ErroCode
├── mapper/              # MapStruct mappers
├── repository/          # Spring Data JPA Repositories
├── resolver/            # Argument resolvers (@CurrentUser annotation)
├── service/             # Business logic
│   └── client/          # Feign clients (IdentityClient)
└── util/                # Utilities (JwtTokenUtil, VNPayUtil, YouTubeUtils)

src/main/resources/
└── application.yaml     # Cấu hình ứng dụng
```

---

## Luồng nghiệp vụ chính

### 1. Luồng tạo và học khóa học

```
Lecturer tạo course (PENDING)
    ↓
Notification Service nhận thông báo (async)
    ↓
Admin phê duyệt (APPROVED) hoặc từ chối (REJECTED)
    ↓
Student duyệt danh sách course đã APPROVED
    ↓
Student thanh toán VNPay (hoặc ghi danh miễn phí)
    ↓
Enrollment tự động được tạo khi payment thành công
    ↓
Student xem bài học (YouTube embed) + cập nhật tiến độ
    ↓
Student làm quiz → hệ thống tự động chấm điểm
```

### 2. Luồng thanh toán VNPay

```
POST /payment/create   →   Tạo Payment (PENDING) + sinh paymentUrl
    ↓
Frontend redirect user đến paymentUrl (VNPay)
    ↓
User thanh toán trên VNPay
    ↓
VNPay redirect về GET /payment/vnpay-return (user thấy kết quả)
VNPay gọi POST /payment/vnpay-ipn (server xác nhận)
    ↓
Verify chữ ký HMAC SHA512
    ↓
Payment SUCCESS → tự động tạo Enrollment
Payment FAILED  → giữ trạng thái FAILED
```

### 3. Luồng chấm điểm quiz

```
Student nộp bài (Map<questionId, answerOptionId>)
    ↓
Validate: đã enroll? còn lượt làm? (maxAttempts)
    ↓
Với mỗi câu: so sánh đáp án chọn với đáp án đúng
    ↓
Tính điểm = (earnedPoints / totalPossiblePoints) * 100
    ↓
isPassed = score >= passScore
    ↓
Lưu QuizResult + trả về kết quả kèm feedback chi tiết
```

---

## Tích hợp ngoài

### Identity Service (OpenFeign)
Được gọi để lấy thông tin giảng viên khi gửi notification.
```yaml
identity:
  service:
    url: ${IDENTITY_SERVICE_URL}
```

### Notification Service (WebClient - bất đồng bộ)
Nhận webhook khi: Lecturer tạo course, Admin phê duyệt/từ chối course.
```yaml
notification:
  service:
    url: ${NOTIFICATION_SERVICE_URL}
```
Các event được gửi đến endpoint `POST /api/v1/events/publish` với topic:
- `lecturer.create.course`
- `admin.approve.course`
- `admin.reject.course`

### VNPay Payment Gateway
Tích hợp đầy đủ luồng tạo URL, redirect và xác thực callback.  
Tài liệu VNPay: https://sandbox.vnpayment.vn/apis/

---

## Seed Data

File `seed-data.sql` cung cấp dữ liệu mẫu để test nhanh:

- 4 Categories (Lập trình Web, Mobile, Database, DevOps)
- 3 Courses (2 APPROVED, 1 PENDING)
- 5 Lessons (có YouTube URL thật)
- 2 Enrollments (studentId=2 đã ghi danh)
- 2 Quizzes + 5 Questions + 16 Answer Options
- 2 Coupons (WELCOME2026 giảm 20%, SALE50K giảm 50,000đ)

---

## Docker

Xem file [`DOCKER_GUIDE.md`](DOCKER_GUIDE.md) để biết đầy đủ hướng dẫn Docker bao gồm:
- Development mode với remote debug port 5005
- Production mode với Nginx reverse proxy + Redis
- Các lệnh backup/restore database
- Troubleshooting thường gặp

**Lệnh Docker hay dùng:**

```bash
# Xem status các service
docker compose ps

# Xem log realtime
docker compose logs -f lms-app

# Restart app sau khi sửa code
docker compose up -d --build lms-app

# Backup database
docker compose exec mysql mysqldump -uroot -p<PASSWORD> LMS > backup.sql

# Vào container debug
docker compose exec lms-app sh
```

---

## Ghi chú phát triển

- **Không dùng `@Transactional` ở Controller** — chỉ dùng ở Service layer
- **Cache eviction** — mỗi operation ghi (create/update/delete) đều `@CacheEvict` để tránh stale data
- **StudentId không trust từ client** — luôn lấy từ JWT token trong Controller
- **YouTube URL** — hệ thống tự extract video ID và sinh embed URL
- **Bài học OPEN không thể xóa** — phải đổi sang UPCOMING/CLOSED trước
- **Course có enrollment ACTIVE không thể xóa** — bảo vệ dữ liệu học viên
- **Coupon sau khi xóa** — Payment cũ giữ nguyên, chỉ unlink reference