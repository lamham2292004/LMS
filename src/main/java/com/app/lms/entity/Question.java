package com.app.lms.entity;

import com.app.lms.enums.QuestionType;
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
@Table(name = "questions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Question {
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

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    QuestionType questionType; // multiple_choice, true_false, short_answer

    @Column(name = "order_index")
    Integer orderIndex = 0; // Thứ tự câu hỏi

    @Column(name = "points")
    Double points = 1.0; // Điểm của câu hỏi

    @CreationTimestamp
    OffsetDateTime createdAt;

    @UpdateTimestamp
    OffsetDateTime updatedAt;

    // Quan hệ 1-n với AnswerOptions
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<AnswerOption> answerOptions = new ArrayList<>();
}
