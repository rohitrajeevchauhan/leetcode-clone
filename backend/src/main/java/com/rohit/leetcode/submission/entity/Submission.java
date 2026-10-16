package com.rohit.leetcode.submission.entity;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.user.entity.User;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "submissions",
        indexes = {
                @Index(
                        name = "idx_submission_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_submission_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_submission_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_submission_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // SOURCE CODE
    // =========================================================

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String code;


    // =========================================================
    // PROGRAMMING LANGUAGE
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ProgrammingLanguage language;


    // =========================================================
    // SUBMISSION STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;


    // =========================================================
    // EXECUTION RESULT
    // =========================================================

    @Column(columnDefinition = "TEXT")
    private String output;


    @Column(columnDefinition = "TEXT")
    private String errorMessage;


    // Execution time in milliseconds
    private Long executionTime;


    // Memory consumed in KB
    private Long memoryUsed;


    // =========================================================
    // TIMESTAMPS
    // =========================================================

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(nullable = false)
    private LocalDateTime updatedAt;


    // =========================================================
    // RELATIONSHIPS
    // =========================================================

    /*
     * Many submissions -> One user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    /*
     * Many submissions -> One problem
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "problem_id",
            nullable = false
    )
    private Problem problem;


    // =========================================================
    // JPA LIFECYCLE
    // =========================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }


    // =========================================================
    // PROGRAMMING LANGUAGES
    // =========================================================

    public enum ProgrammingLanguage {

        JAVA,

        CPP,

        PYTHON,

        JAVASCRIPT
    }


    // =========================================================
    // SUBMISSION STATUS
    // =========================================================

    public enum SubmissionStatus {

        PENDING,

        RUNNING,

        ACCEPTED,

        WRONG_ANSWER,

        COMPILATION_ERROR,

        RUNTIME_ERROR,

        TIME_LIMIT_EXCEEDED,

        MEMORY_LIMIT_EXCEEDED
    }
}