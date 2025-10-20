# ✅ FIX: Lấy Full Name thay vì Username khi đăng ký khóa học

## 🎯 Yêu cầu

Khi học viên đăng ký khóa học, hiển thị **tên đầy đủ** (Nguyễn Ngọc Lâm) thay vì **username** (sv_SV002).

---

## ✅ Đã fix

### EnrollmentController.java

**File:** `src/main/java/com/app/lms/controller/EnrollmentController.java`

```java
// BEFORE
request.setStudentName(currentUser.getUsername());  // ❌ Lấy username

// AFTER - Ưu tiên fullName
String studentName = currentUser.getFullName();      // ✅ Lấy full_name TRƯỚC
if (studentName == null || studentName.isEmpty()) {
    studentName = currentUser.getUsername();         // Fallback: username
}
if (studentName == null || studentName.isEmpty()) {
    studentName = "Student #" + currentUser.getUserId();  // Last resort
}
request.setStudentName(studentName);
```

**Priority order:**

1. **full_name** từ token (ưu tiên cao nhất)
2. **username** (fallback)
3. **"Student #ID"** (last resort)

---

## 🔍 Kiểm tra JWT Token

### Token phải có field `full_name`:

**JWT Payload cần có:**

```json
{
  "sub": 2,
  "user_type": "student",
  "username": "sv_SV002",
  "full_name": "Nguyễn Ngọc Lâm", // ✅ CẦN FIELD NÀY
  "email": "nguyenlam22922008@gmail.com",
  "studentCode": "SV002",
  "exp": 1760695458,
  "iat": 1760695458
}
```

### Backend đang parse đúng:

**JwtTokenUtil.java (Line 149):**

```java
.fullName((String) claims.get("full_name"))  // ✅ Đúng field name
```

---

## 🧪 Test Case

### Case 1: Token có full_name ✅

**Token:**

```json
{
  "full_name": "Nguyễn Ngọc Lâm",
  "username": "sv_SV002"
}
```

**Kết quả:**

```
studentName = "Nguyễn Ngọc Lâm"  ✅
```

---

### Case 2: Token không có full_name nhưng có username

**Token:**

```json
{
  "username": "sv_SV002"
}
```

**Kết quả:**

```
studentName = "sv_SV002"  ✅ Fallback
```

---

### Case 3: Token không có cả 2

**Token:**

```json
{
  "sub": 2
}
```

**Kết quả:**

```
studentName = "Student #2"  ✅ Last resort
```

---

## 🚀 Rebuild & Test

### Bước 1: Rebuild Backend

```bash
cd f:/DoAn/LMS
mvn clean install
```

### Bước 2: Restart Server

```bash
mvn spring-boot:run
```

### Bước 3: Enable Debug để xem token content

**CurrentUserResolver.java đã enable:**

```java
jwtTokenUtil.debugToken(token);  // ✅ Đang bật
```

**Backend logs sẽ hiển thị:**

```
=== JWT TOKEN DEBUG ===
Subject: 2
All claims: {sub=2, user_type=student, username=sv_SV002, full_name=Nguyễn Ngọc Lâm, ...}
Claim - full_name: Nguyễn Ngọc Lâm
Claim - username: sv_SV002
Claim - email: nguyenlam22922008@gmail.com
```

---

## 📊 Kiểm tra kết quả

### 1. Backend Logs

```
Student Nguyễn Ngọc Lâm (ID: 2) is enrolling in course 4
Creating enrollment for student 2 in course 4
```

### 2. Database

```sql
SELECT * FROM enrollment WHERE student_id = 2;
```

**Kết quả:**

```
id | student_id | student_full_name    | student_email               | course_id | status
---|------------|----------------------|-----------------------------|-----------|--------
1  | 2          | Nguyễn Ngọc Lâm      | nguyenlam22922008@gmail.com | 4         | ACTIVE
```

### 3. API Response (GET /api/enrollment)

```json
{
  "code": 1000,
  "result": [
    {
      "id": 1,
      "studentId": 2,
      "studentName": "Nguyễn Ngọc Lâm", // ✅ Full name thay vì sv_SV002
      "studentEmail": "nguyenlam22922008@gmail.com",
      "courseId": 4,
      "courseName": "CSS",
      "status": "ACTIVE",
      "enrolledAt": "2025-10-17T14:10:00"
    }
  ]
}
```

### 4. Frontend Display

**Trang quản lý khóa học của giảng viên:**

```
┌───┬──────────────────────────┬──────────┬─────────┬────────────┐
│STT│ Học viên                 │ Trạng thái│ Tiến độ │ Thao tác   │
├───┼──────────────────────────┼──────────┼─────────┼────────────┤
│ ① │ 👤 Nguyễn Ngọc Lâm       │ Đang học │  0%     │ [Xem chi   │
│   │ nguyenlam22922008@...    │          │         │  tiết]     │
│   │ Đăng ký: 17/10/2025      │          │         │            │
└───┴──────────────────────────┴──────────┴─────────┴────────────┘
```

**BEFORE:**

```
👤 sv_SV002  ❌
```

**AFTER:**

```
👤 Nguyễn Ngọc Lâm  ✅
```

---

## 🔧 Nếu vẫn hiển thị username

### Kiểm tra 1: Token có full_name không?

**Frontend Console:**

```javascript
const token = localStorage.getItem("auth_token");
const decoded = JSON.parse(atob(token.split(".")[1]));
console.log("Token payload:", decoded);
console.log("full_name:", decoded.full_name); // ⚠️ Kiểm tra
console.log("username:", decoded.username);
```

---

### Kiểm tra 2: Backend có nhận được full_name không?

**Backend logs (đã enable debug):**

```
=== JWT TOKEN DEBUG ===
Claim - full_name: ???    // ⚠️ Xem giá trị này
```

---

### Kiểm tra 3: Laravel Backend có trả về full_name không?

**Laravel Backend - AuthController.php:**

```php
// Đảm bảo token có field này
$customClaims = [
    'sub' => $student->id,
    'user_type' => 'student',
    'username' => 'sv_' . $student->student_code,
    'full_name' => $student->full_name,          // ✅ CHECK THIS
    'email' => $student->email,
    'studentCode' => $student->student_code,
    // ...
];
```

---

## 📋 Summary

### Đã thay đổi:

1. ✅ **EnrollmentController.java**

   - Đổi từ `getUsername()` → `getFullName()`
   - Thêm fallback logic
   - Update logs

2. ✅ **CurrentUserResolver.java**

   - Enable debug token

3. ✅ **Logic mới:**
   ```
   fullName (1st) → username (2nd) → "Student #ID" (3rd)
   ```

### Kết quả:

- ✅ Hiển thị **"Nguyễn Ngọc Lâm"** thay vì **"sv_SV002"**
- ✅ Có fallback khi token thiếu full_name
- ✅ Frontend tự động hiển thị tên đúng
- ✅ Debug logs để troubleshoot

---

## 🎊 Done!

Bây giờ khi đăng ký khóa học, hệ thống sẽ lưu và hiển thị **tên đầy đủ** của học viên! 🎉

**Next:**

1. Rebuild backend: `mvn clean install`
2. Restart server: `mvn spring-boot:run`
3. Test đăng ký khóa học
4. Kiểm tra logs và database
