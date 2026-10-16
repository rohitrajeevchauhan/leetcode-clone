package com.rohit.leetcode.problem.repository;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.problem.entity.Problem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

    // =========================================================
    // ADVANCED SEARCH
    // =========================================================

    @Query("""
            SELECT p
            FROM Problem p
            WHERE
                (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.topic) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :difficulty IS NULL
                    OR p.difficulty = :difficulty
                )
            AND
                (
                    :topic IS NULL
                    OR :topic = ''
                    OR LOWER(p.topic) = LOWER(:topic)
                )
            AND
                (
                    :tag IS NULL
                    OR :tag = ''
                    OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))
                )
            """)
    Page<Problem> searchProblems(
            @Param("keyword") String keyword,
            @Param("difficulty") Difficulty difficulty,
            @Param("topic") String topic,
            @Param("tag") String tag,
            Pageable pageable
    );


    // =========================================================
    // TOPIC
    // =========================================================

    Page<Problem> findByTopicIgnoreCase(
            String topic,
            Pageable pageable
    );


    // =========================================================
    // DIFFICULTY
    // =========================================================

    Page<Problem> findByDifficulty(
            Difficulty difficulty,
            Pageable pageable
    );


    // =========================================================
    // TOPIC + DIFFICULTY
    // =========================================================

    Page<Problem> findByTopicIgnoreCaseAndDifficulty(
            String topic,
            Difficulty difficulty,
            Pageable pageable
    );


    // =========================================================
    // TITLE SEARCH
    // =========================================================

    Page<Problem> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );
}