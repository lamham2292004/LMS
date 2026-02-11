-- ============================================
-- SEED DATA CHO LMS DATABASE (dùng để test)
-- Chạy script này sau khi database đã có bảng
-- ============================================

-- =====================
-- 1. CATEGORIES (Danh mục)
-- =====================
INSERT INTO categories (id, name, description, created_at, updated_at) VALUES
(1, 'Lập trình Web', 'Các khóa học về phát triển web frontend và backend', NOW(), NOW()),
(2, 'Lập trình Mobile', 'Các khóa học về phát triển ứng dụng di động', NOW(), NOW()),
(3, 'Cơ sở dữ liệu', 'Các khóa học về SQL, NoSQL và quản trị CSDL', NOW(), NOW()),
(4, 'DevOps', 'Các khóa học về Docker, CI/CD, Cloud', NOW(), NOW());

-- =====================
-- 2. COURSES (Khóa học)
-- teacherId = 1 giả định là ID giảng viên từ Identity Service
-- =====================
INSERT INTO courses (id, title, description, price, teacher_id, status, approval_status, category_id, created_at, updated_at) VALUES
(1, 'Spring Boot từ A đến Z', 'Khóa học toàn diện về Spring Boot, Spring Security, JPA, REST API', 500000.00, 2, 'OPEN', 'APPROVED', 1, NOW(), NOW()),
(2, 'React.js cho người mới bắt đầu', 'Học React từ cơ bản đến nâng cao, hooks, state management', 350000.00, 2 , 'OPEN', 'APPROVED', 1, NOW(), NOW()),
(3, 'MySQL & Database Design', 'Thiết kế CSDL, tối ưu truy vấn, indexing', 200000.00, 2, 'UPCOMING', 'PENDING', 3, NOW(), NOW());

-- =====================
-- 3. LESSONS (Bài học) - Đã sử dụng youtube_url thay cho video_path
-- =====================
INSERT INTO lesson (id, course_id, title, description, order_index, status, duration, youtube_url, created_at, updated_at) VALUES
-- Khóa học 1: Spring Boot
(1, 1, 'Giới thiệu Spring Boot', 'Tổng quan về Spring Boot và cài đặt môi trường', 1, 'OPEN', 15, 'https://www.youtube.com/watch?v=9SGDpanrc8U', NOW(), NOW()),
(2, 1, 'Spring Boot REST API', 'Xây dựng REST API cơ bản với Spring Boot', 2, 'OPEN', 30, 'https://www.youtube.com/watch?v=pcdpbKimXhQ', NOW(), NOW()),
(3, 1, 'Spring Data JPA', 'Làm việc với database sử dụng JPA & Hibernate', 3, 'UPCOMING', 25, 'https://www.youtube.com/watch?v=8SGI_XS5OPw', NOW(), NOW()),
-- Khóa học 2: React.js
(4, 2, 'Giới thiệu React.js', 'React là gì? Tại sao nên học React?', 1, 'OPEN', 20, 'https://www.youtube.com/watch?v=Tn6-PIqc4UM', NOW(), NOW()),
(5, 2, 'React Components & Props', 'Tìm hiểu về Components, Props và State', 2, 'OPEN', 35, 'https://www.youtube.com/watch?v=PHaECbrKgs0', NOW(), NOW());

-- =====================
-- 4. ENROLLMENTS (Đăng ký khóa học)
-- studentId = 2 giả định là ID học viên từ Identity Service
-- =====================
INSERT INTO enrollment (id, student_id, student_full_name, student_email, course_id, status, enrolled_at) VALUES
(1, 2, 'Sinh Viên Mẫu', 'sinhvien@test.com', 1, 'ACTIVE', NOW()),
(2, 2, 'Nguyễn Ngọc Lâm', 'nguyenlam2292004@gmail.com', 2, 'ACTIVE', NOW());

-- =====================
-- 5. QUIZZES (Bài kiểm tra)
-- =====================
INSERT INTO quizzes (id, lesson_id, title, description, time_limit, max_attempts, pass_score, created_at, updated_at) VALUES
(1, 1, 'Quiz: Giới thiệu Spring Boot', 'Kiểm tra kiến thức cơ bản về Spring Boot', 10, 3, 70.0, NOW(), NOW()),
(2, 2, 'Quiz: REST API', 'Kiểm tra kiến thức về REST API', 15, 2, 60.0, NOW(), NOW());

-- =====================
-- 6. QUESTIONS (Câu hỏi)
-- =====================
INSERT INTO questions (id, quiz_id, question_text, question_type, order_index, points, created_at, updated_at) VALUES
-- Quiz 1: Giới thiệu Spring Boot
(1, 1, 'Spring Boot được phát triển bởi công ty nào?', 'MULTIPLE_CHOICE', 1, 1.0, NOW(), NOW()),
(2, 1, 'Spring Boot có hỗ trợ embedded server không?', 'TRUE_FALSE', 2, 1.0, NOW(), NOW()),
(3, 1, 'Spring Boot dùng file cấu hình mặc định nào?', 'MULTIPLE_CHOICE', 3, 1.0, NOW(), NOW()),
-- Quiz 2: REST API
(4, 2, 'HTTP Method nào dùng để tạo resource mới?', 'MULTIPLE_CHOICE', 1, 1.0, NOW(), NOW()),
(5, 2, 'REST API chỉ hỗ trợ JSON?', 'TRUE_FALSE', 2, 1.0, NOW(), NOW());

-- =====================
-- 7. ANSWER OPTIONS (Đáp án)
-- =====================
INSERT INTO answer_options (id, question_id, answer_text, is_correct, order_index) VALUES
-- Câu 1: Spring Boot được phát triển bởi?
(1, 1, 'Google', false, 1),
(2, 1, 'Pivotal (VMware)', true, 2),
(3, 1, 'Facebook', false, 3),
(4, 1, 'Microsoft', false, 4),
-- Câu 2: Embedded server?
(5, 2, 'Đúng', true, 1),
(6, 2, 'Sai', false, 2),
-- Câu 3: File cấu hình mặc định?
(7, 3, 'config.xml', false, 1),
(8, 3, 'application.properties hoặc application.yml', true, 2),
(9, 3, 'settings.json', false, 3),
(10, 3, 'pom.xml', false, 4),
-- Câu 4: HTTP Method tạo resource?
(11, 4, 'GET', false, 1),
(12, 4, 'POST', true, 2),
(13, 4, 'DELETE', false, 3),
(14, 4, 'PATCH', false, 4),
-- Câu 5: REST chỉ hỗ trợ JSON?
(15, 5, 'Đúng', false, 1),
(16, 5, 'Sai', true, 2);

-- =====================
-- 8. COUPONS (Mã giảm giá)
-- =====================
INSERT INTO coupons (id, code, discount_type, discount_value, min_order_value, max_discount, usage_limit, used_count, start_date, end_date, status, description, created_by, created_at, updated_at) VALUES
(1, 'WELCOME2026', 'PERCENTAGE', 20.00, 100000.00, 200000.00, 100, 0, '2026-01-01 00:00:00+07:00', '2026-12-31 23:59:59+07:00', 'ACTIVE', 'Giảm 20% cho học viên mới', 1, NOW(), NOW()),
(2, 'SALE50K', 'FIXED_AMOUNT', 50000.00, 200000.00, NULL, 50, 0, '2026-01-01 00:00:00+07:00', '2026-06-30 23:59:59+07:00', 'ACTIVE', 'Giảm 50,000 VND', 1, NOW(), NOW());

-- ============================================
-- DONE! Dữ liệu test đã sẵn sàng.
-- 
-- Tóm tắt:
--   4 Categories
--   3 Courses (2 APPROVED, 1 PENDING)
--   5 Lessons (với YouTube URL)
--   2 Enrollments
--   2 Quizzes
--   5 Questions
--   16 Answer Options
--   2 Coupons
-- ============================================
