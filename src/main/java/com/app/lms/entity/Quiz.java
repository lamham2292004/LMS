package com.app.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "quizzes")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Khóa ngoại tới Lesson
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Column(name = "lesson_id", nullable = false, insertable = false, updatable = false)
    Long lessonId;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "time_limit")
    Integer timeLimit; // Thời gian làm bài (phút)

    @Column(name = "max_attempts")
    Integer maxAttempts; // Số lần làm tối đa

    @Column(name = "pass_score")
    Double passScore; // Điểm đạt tối thiểu

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;

    // Quan hệ 1-n với Questions
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Question> questions = new ArrayList<>();

    // Quan hệ 1-n với QuizResults
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<QuizResult> quizResults = new ArrayList<>();
}
