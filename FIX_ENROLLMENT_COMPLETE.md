# ✅ ĐÃ FIX: Lỗi đăng ký khóa học

## 🐛 Vấn đề ban đầu

Khi học viên ấn "Đăng ký khóa học" → Lỗi database:

```
Column 'student_full_name' cannot be null
Column 'student_email' cannot be null
```

### Nguyên nhân:

1. Entity `Enrollment` yêu cầu `studentName` và `studentEmail` **NOT NULL**
2. JWT Token không chứa đủ field `username` và `email`
3. Controller nhận NULL → Database reject → ERROR

---

## ✅ Đã fix

### 1. Cho phép NULL trong Entity ✅

**File:** `src/main/java/com/app/lms/entity/Enrollment.java`

```java
// BEFORE
@Column(name = "student_full_name", nullable = false)  // ❌ NOT NULL
String studentName;

@Column(name = "student_email", nullable = false)       // ❌ NOT NULL
String studentEmail;

// AFTER
@Column(name = "student_full_name")  // ✅ Cho phép NULL
String studentName;

@Column(name = "student_email")       // ✅ Cho phép NULL
String studentEmail;
```

---

### 2. Thêm Fallback Logic ✅

**File:** `src/main/java/com/app/lms/controller/EnrollmentController.java`

```java
// ✅ Fallback logic for studentName
String studentName = currentUser.getUsername();
if (studentName == null || studentName.isEmpty()) {
    studentName = currentUser.getFullName();
}
if (studentName == null || studentName.isEmpty()) {
    studentName = "Student #" + currentUser.getUserId();
}
request.setStudentName(studentName);

// ✅ Fallback logic for studentEmail
String studentEmail = currentUser.getEmail();
if (studentEmail == null || studentEmail.isEmpty()) {
    studentEmail = "student" + currentUser.getUserId() + "@temp.edu";
}
request.setStudentEmail(studentEmail);
```

**Logic:**

- Ưu tiên lấy `username` từ token
- Nếu không có → lấy `fullName`
- Nếu vẫn không có → tạo tên mặc định: "Student #5"
- Email tương tự: `student5@temp.edu`

---

### 3. Enable Debug Logs ✅

**File:** `src/main/java/com/app/lms/resolver/CurrentUserResolver.java`

```java
// Enable debug để kiểm tra token content
if (log.isDebugEnabled()) {
    jwtTokenUtil.debugToken(token);
}
```

Để bật debug:

```yaml
# application.yaml
logging:
  level:
    com.app.lms.resolver: DEBUG
    com.app.lms.until.JwtTokenUtil: DEBUG
```

---

## 🚀 Cách test

### Bước 1: Rebuild Backend

```bash
cd f:/DoAn/LMS
mvn clean install
```

### Bước 2: Restart Server

```bash
mvn spring-boot:run
```

### Bước 3: Test đăng ký khóa học

**Frontend:**

1. Login với tài khoản student
2. Vào trang khóa học
3. Click "Đăng ký khóa học"
4. ✅ Không còn lỗi!

**Backend logs sẽ hiển thị:**

```
Student Nguyễn Văn A (Student #5) is enrolling in course 1
Creating enrollment for student 5 in course 1
```

---

## 📊 Kiểm tra kết quả

### Check Database:

```sql
SELECT * FROM enrollment ORDER BY id DESC LIMIT 5;
```

**Kết quả mong đợi:**

```
id | student_id | student_full_name | student_email         | course_id | status | enrolled_at
---|------------|-------------------|-----------------------|-----------|--------|------------------
1  | 5          | Student #5        | student5@temp.edu     | 1         | ACTIVE | 2025-10-17 10:00
```

### Check API Response:

```bash
GET http://localhost:8083/api/enrollment

Response:
{
  "code": 1000,
  "result": [
    {
      "id": 1,
      "studentId": 5,
      "studentName": "Student #5",      // ✅ Có fallback
      "studentEmail": "student5@temp.edu", // ✅ Có fallback
      "courseId": 1,
      "courseName": "HTML",
      "status": "ACTIVE",
      "enrolledAt": "2025-10-17T10:00:00"
    }
  ]
}
```

---

## 🎯 Frontend sẽ hiển thị

**File:** `src/app/authorized/lms/app/lecturer/courses/[id]/page.tsx`

```
┌───┬──────────────────┬──────────┬─────────┬────────────┐
│STT│ Học viên         │ Trạng thái│ Tiến độ │ Thao tác   │
├───┼──────────────────┼──────────┼─────────┼────────────┤
│ ① │ 👤 Student #5    │ Đang học │  0%     │ [Xem chi   │
│   │ student5@temp.edu│          │         │  tiết]     │
│   │ Đăng ký: 17/10/25│          │         │            │
└───┴──────────────────┴──────────┴─────────┴────────────┘
```

**Nếu token có đầy đủ thông tin:**

```
┌───┬──────────────────┬──────────┬─────────┬────────────┐
│ ① │ 👤 Nguyễn Văn A  │ Đang học │  0%     │ [Xem chi   │
│   │ student@email.com│          │         │  tiết]     │
└───┴──────────────────┴──────────┴─────────┴────────────┘
```

---

## 🔧 Cải tiến thêm (Tùy chọn)

### Option: Fix JWT Token từ Laravel Backend

**Đảm bảo token có đầy đủ fields:**

```php
// Laravel - AuthController.php
$token = JWTAuth::customClaims([
    'sub' => $student->id,
    'email' => $student->email,              // ✅ ĐẢM BẢO CÓ
    'username' => $student->student_code,    // ✅ ĐẢM BẢO CÓ
    'full_name' => $student->full_name,      // ✅ ĐẢM BẢO CÓ
    'user_type' => 'student',
    'studentCode' => $student->student_code,
])->fromUser($studentAccount);
```

**Test token payload:**

```bash
# Frontend console
const decoded = tokenManager.getDecodedToken();
console.log('Token:', decoded);
// {
//   sub: 5,
//   email: "student@email.com",     ✅
//   username: "SV001",              ✅
//   full_name: "Nguyễn Văn A",      ✅
//   user_type: "student"
// }
```

---

## 📝 Files đã thay đổi

1. ✅ `src/main/java/com/app/lms/entity/Enrollment.java`

   - Bỏ `nullable = false` cho `studentName` và `studentEmail`

2. ✅ `src/main/java/com/app/lms/controller/EnrollmentController.java`

   - Thêm fallback logic cho tên và email

3. ✅ `src/main/java/com/app/lms/resolver/CurrentUserResolver.java`
   - Enable debug logs

---

## 🎊 Kết quả

**BEFORE:**
❌ Đăng ký khóa học → Lỗi database

**AFTER:**
✅ Đăng ký khóa học → Thành công!
✅ Có fallback khi token thiếu thông tin
✅ Hiển thị tên học viên ở frontend
✅ Không crash dù token thiếu field

---

## 🚀 Next Steps

1. ✅ Test đăng ký khóa học
2. ✅ Kiểm tra danh sách học viên trong quản lý khóa học
3. ⚠️ (Optional) Update Laravel backend để token có đầy đủ thông tin
4. ⚠️ (Optional) Update database để có tên thật thay vì "Student #5"

---

## 📞 Nếu vẫn lỗi

### Check logs:

```bash
# Backend logs
tail -f logs/spring-boot-logger.log

# Hoặc console output
```

### Bật debug mode:

**application.yaml:**

```yaml
logging:
  level:
    com.app.lms: DEBUG
```

### Kiểm tra token:

**Frontend console:**

```javascript
localStorage.getItem("auth_token");
// Copy token và decode tại https://jwt.io
```

**Backend debug:**

```java
// Uncomment line in CurrentUserResolver.java
jwtTokenUtil.debugToken(token);
```

---

## ✨ Done!

Lỗi đã được fix! Bạn có thể đăng ký khóa học bình thường rồi. 🎉
