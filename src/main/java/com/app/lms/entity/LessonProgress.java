package com.app.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "lesson_progress", uniqueConstraints = @UniqueConstraint(columnNames = { "student_id", "lesson_id" }))
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "student_id", nullable = false)
    Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Lesson lesson;

    @Column(name = "lesson_id", nullable = false)
    Long lessonId;

    // Số giây đã xem (tích lũy, không trùng lặp)
    @Column(name = "watched_seconds", nullable = false)
    @Builder.Default
    Integer watchedSeconds = 0;

    // Tổng độ dài video (giây)
    @Column(name = "total_seconds")
    Integer totalSeconds;

    // Vị trí cuối cùng của video (giây) — để resume
    @Column(name = "last_position", nullable = false)
    @Builder.Default
    Integer lastPosition = 0;

    // Đã hoàn thành chưa (watchedSeconds >= 90% totalSeconds)
    @Column(nullable = false)
    @Builder.Default
    Boolean completed = false;

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;
}
