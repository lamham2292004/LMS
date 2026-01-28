package com.app.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "quiz_results")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Khóa ngoại tới Quiz
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "quiz_id", nullable = false)
    Quiz quiz;

    @Column(name = "quiz_id", nullable = false, insertable = false, updatable = false)
    Long quizId;

    @Column(name = "student_id", nullable = false)
    Long studentId; // ID học viên từ Identity Service

    @Column(nullable = false, precision = 5, scale = 2)
    BigDecimal score; // Điểm số

    @Column(name = "total_questions")
    Integer totalQuestions; // Tổng số câu hỏi

    @Column(name = "correct_answers")
    Integer correctAnswers; // Số câu trả lời đúng

    @Column(name = "time_taken")
    Integer timeTaken; // Thời gian làm bài (phút)

    @Column(name = "attempt_number")
    @Builder.Default
    Integer attemptNumber = 1; // Lần thử thứ mấy

    @Column(name = "is_passed")
    @Builder.Default
    Boolean isPassed = false; // Có đạt hay không

    @CreationTimestamp
    @Column(name = "taken_at")
    OffsetDateTime takenAt; // Thời gian làm bài
}
