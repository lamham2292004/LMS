package com.app.lms.entity;

import com.app.lms.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@Table(name = "enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "student_id", nullable = false)
    Long studentId; // ID học viên từ Identity Service

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false, insertable = false, updatable = false)
    Course course;

    @Column(name = "course_id", nullable = false)
    Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    EnrollmentStatus status = EnrollmentStatus.ACTIVE; //active,completed,cancelled

    @CreationTimestamp
    OffsetDateTime enrolledAt;
}
