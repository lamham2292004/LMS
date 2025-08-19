package com.app.lms.entity;


import com.app.lms.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "courses")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    BigDecimal price = BigDecimal.ZERO;

    @Column(name = "teacher_id", nullable = false)
    Long teacherId; // ID giảng viên (từ Identity Service)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    CourseStatus status; // upcoming | open | closed

    @Column(name = "start_time")
    Instant startTime; // Thời gian bắt đầu khóa học

    @Column(name = "end_time")
    Instant endTime; // Thời gian kết thúc khóa học

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    // Quan hệ 1-n với Lessons
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Lessons> lessons;

    // Quan hệ 1-n với Enrollments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Enrollments> enrollments;
}

