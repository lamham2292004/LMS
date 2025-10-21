package com.app.lms.entity;


import com.app.lms.enums.ApprovalStatus;
import com.app.lms.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "courses")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title; // tên khóa học

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    BigDecimal price = BigDecimal.ZERO;

    @Column(name = "teacher_id", nullable = false)
    Long teacherId; // ID giảng viên (từ Identity Service)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    CourseStatus status = CourseStatus.UPCOMING; // upcoming | open | closed

    // THÊM TRẠNG THÁI PHÊ DUYỆT
    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    // THÊM NGƯỜI PHÊ DUYỆT
    @Column(name = "approved_by")
    Long approvedBy;

    // THỜI GIAN PHÊ DUYỆT
    @Column(name = "approved_at")
    OffsetDateTime approvedAt;

    //  GHI CHÚ TỪ CHỐI (NẾU CÓ)
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    String rejectionReason;

    @Column(name = "start_time")
    OffsetDateTime startTime; // Thời gian bắt đầu khóa học

    @Column(name = "end_time")
    OffsetDateTime endTime; // Thời gian kết thúc khóa học

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;

    // Quan hệ 1-n với Lessons
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Lesson> lessons = new ArrayList<>();

    // Quan hệ 1-n với Enrollments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Enrollment> enrollments = new ArrayList<>();

    @Column(name = "img_path")
    String img; // Đường dẫn ảnh đại diện (lưu local)

    // Quan hệ với Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = true)
    Category category;
 }

