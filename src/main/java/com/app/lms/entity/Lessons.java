package com.app.lms.entity;

import com.app.lms.enums.CourseStatus;
import com.app.lms.enums.LessonStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "lessons")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Lessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    // Khóa ngoại tới Courses
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, insertable = false, updatable = false)
    Courses course;

    @Column(name = "course_id", nullable = false)
    Long courseId;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "order_index")
    int orderIndex = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    LessonStatus status; // upcoming | open | closed

    // Thời lượng bài học (tính bằng phút)
    Integer duration;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}


