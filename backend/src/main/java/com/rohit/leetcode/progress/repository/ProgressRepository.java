package com.rohit.leetcode.progress.repository;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.submission.entity.Submission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProgressRepository {

    @PersistenceContext
    private EntityManager entityManager;


    // =========================================================
    // TOTAL PROBLEMS
    // =========================================================

    public long countProblems() {

        return entityManager
                .createQuery(
                        "SELECT COUNT(p) FROM Problem p",
                        Long.class
                )
                .getSingleResult();
    }


    // =========================================================
    // PROBLEMS BY DIFFICULTY
    // =========================================================

    public long countProblemsByDifficulty(
            Difficulty difficulty
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(p)
                        FROM Problem p
                        WHERE p.difficulty = :difficulty
                        """,
                        Long.class
                )
                .setParameter(
                        "difficulty",
                        difficulty
                )
                .getSingleResult();
    }


    // =========================================================
    // USER ATTEMPTED PROBLEMS
    // =========================================================

    public long countAttemptedProblems(
            Long userId
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .getSingleResult();
    }


    // =========================================================
    // USER SOLVED PROBLEMS
    // =========================================================

    public long countSolvedProblems(
            Long userId
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.status = :status
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter(
                        "status",
                        Submission.SubmissionStatus.ACCEPTED
                )
                .getSingleResult();
    }


    // =========================================================
    // SOLVED BY DIFFICULTY
    // =========================================================

    public long countSolvedProblemsByDifficulty(
            Long userId,
            Difficulty difficulty
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.problem.difficulty = :difficulty
                        AND s.status = :status
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter(
                        "difficulty",
                        difficulty
                )
                .setParameter(
                        "status",
                        Submission.SubmissionStatus.ACCEPTED
                )
                .getSingleResult();
    }


    // =========================================================
    // ATTEMPTED BY DIFFICULTY
    // =========================================================

    public long countAttemptedProblemsByDifficulty(
            Long userId,
            Difficulty difficulty
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.problem.difficulty = :difficulty
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter(
                        "difficulty",
                        difficulty
                )
                .getSingleResult();
    }


    // =========================================================
    // TOTAL SUBMISSIONS
    // =========================================================

    public long countSubmissions(
            Long userId
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(s)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .getSingleResult();
    }


    // =========================================================
    // SUBMISSIONS BY STATUS
    // =========================================================

    public long countSubmissionsByStatus(
            Long userId,
            Submission.SubmissionStatus status
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(s)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.status = :status
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter("status", status)
                .getSingleResult();
    }


    // =========================================================
    // TOPIC STATISTICS
    // =========================================================

    public List<Object[]> findTopicStatistics(
            Long userId
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT
                            p.topic,
                            COUNT(DISTINCT p.id)
                        FROM Problem p
                        GROUP BY p.topic
                        ORDER BY p.topic
                        """,
                        Object[].class
                )
                .getResultList();
    }


    // =========================================================
    // SOLVED TOPICS
    // =========================================================

    public long countSolvedProblemsByTopic(
            Long userId,
            String topic
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.problem.topic = :topic
                        AND s.status = :status
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter("topic", topic)
                .setParameter(
                        "status",
                        Submission.SubmissionStatus.ACCEPTED
                )
                .getSingleResult();
    }


    // =========================================================
    // ATTEMPTED TOPICS
    // =========================================================

    public long countAttemptedProblemsByTopic(
            Long userId,
            String topic
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(DISTINCT s.problem.id)
                        FROM Submission s
                        WHERE s.user.id = :userId
                        AND s.problem.topic = :topic
                        """,
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter("topic", topic)
                .getSingleResult();
    }
}