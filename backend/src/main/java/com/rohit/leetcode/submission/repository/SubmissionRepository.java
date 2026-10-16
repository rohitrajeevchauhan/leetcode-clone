package com.rohit.leetcode.submission.repository;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    // =========================================================
    // USER SUBMISSIONS
    // =========================================================

    Page<Submission> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<Submission> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );


    // =========================================================
    // USER + PROBLEM
    // =========================================================

    Page<Submission> findByUserIdAndProblemId(
            Long userId,
            Long problemId,
            Pageable pageable
    );

    List<Submission> findByUserIdAndProblemIdOrderByCreatedAtDesc(
            Long userId,
            Long problemId
    );


    // =========================================================
    // PROBLEM SUBMISSIONS
    // =========================================================

    Page<Submission> findByProblemId(
            Long problemId,
            Pageable pageable
    );

    List<Submission> findByProblemIdOrderByCreatedAtDesc(
            Long problemId
    );


    // =========================================================
    // USER COUNTS
    // =========================================================

    long countByUserId(
            Long userId
    );

    long countByUserIdAndStatus(
            Long userId,
            SubmissionStatus status
    );


    // =========================================================
    // PROBLEM COUNTS
    // =========================================================

    long countByProblemId(
            Long problemId
    );

    long countByProblemIdAndStatus(
            Long problemId,
            SubmissionStatus status
    );


    // =========================================================
    // USER + PROBLEM COUNTS
    // =========================================================

    long countByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    long countByUserIdAndProblemIdAndStatus(
            Long userId,
            Long problemId,
            SubmissionStatus status
    );


    // =========================================================
    // GLOBAL STATUS COUNT
    // =========================================================

    long countByStatus(
            SubmissionStatus status
    );


    // =========================================================
    // DISTINCT SOLVED PROBLEMS
    // =========================================================

    @Query("""
            SELECT COUNT(DISTINCT s.problem.id)
            FROM Submission s
            WHERE s.user.id = :userId
            AND s.status = :status
            """)
    long countDistinctProblemsSolvedByUser(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status
    );


    // =========================================================
    // DISTINCT SOLVED PROBLEMS BY DIFFICULTY
    // =========================================================

    @Query("""
            SELECT COUNT(DISTINCT s.problem.id)
            FROM Submission s
            WHERE s.user.id = :userId
            AND s.status = :status
            AND s.problem.difficulty = :difficulty
            """)


    long countDistinctProblemsSolvedByUserAndDifficulty(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status,
            @Param("difficulty") Difficulty difficulty
    );
}