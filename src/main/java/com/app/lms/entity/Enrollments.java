package com.app.lms.entity;

import com.app.lms.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Enrollments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "student_id", nullable = false)
    Long studentId; // ID học viên từ Identity Service

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, insertable = false, updatable = false)
    Courses course;

    @Column(name = "course_id", nullable = false)
    Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EnrollmentStatus status; //active,completed,cancelled

    @CreationTimestamp
    Instant enrolledAt;


}


