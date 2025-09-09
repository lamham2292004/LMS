package com.app.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@Table(name = "answer_options")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Khóa ngoại tới Question
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "question_id", nullable = false)
    Question question;

    @Column(name = "question_id", nullable = false, insertable = false, updatable = false)
    Long questionId;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    String answerText;

    @Column(name = "is_correct")
    @Builder.Default
    Boolean isCorrect = false;

    @Column(name = "order_index")
    Integer orderIndex = 0; // Thứ tự đáp án
}
