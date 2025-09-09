package com.app.lms.entity;

import com.app.lms.enums.LessonStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "lesson")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    // Khóa ngoại tới Courses
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Column(name = "course_id", nullable = false,insertable = false, updatable = false)
    Long courseId;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "order_index")
    int orderIndex = 0; //so thu tu

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    LessonStatus status = LessonStatus.UPCOMING; // upcoming | open | closed

    // Thời lượng bài học (tính bằng phút)
    @Column(name = "duration")
    Integer duration;

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;

    @Column(name = "video_path")
    String videoPath; // Đường dẫn video bài học (lưu local)
}
