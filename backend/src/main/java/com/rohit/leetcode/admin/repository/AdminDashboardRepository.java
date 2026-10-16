package com.rohit.leetcode.admin.repository;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.submission.entity.Submission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminDashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;


    // =========================================================
    // PROBLEM COUNTS
    // =========================================================

    public long countProblemsByDifficulty(
            Difficulty difficulty) {

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
    // MOST ATTEMPTED
    // =========================================================

    public List<Object[]> findMostAttemptedProblems(
            int limit) {

        return entityManager
                .createQuery(
                        """
                        SELECT
                            p.id,
                            p.title,
                            p.difficulty,
                            COUNT(s.id)
                        FROM Problem p
                        LEFT JOIN Submission s
                            ON s.problem.id = p.id
                        GROUP BY
                            p.id,
                            p.title,
                            p.difficulty
                        ORDER BY COUNT(s.id) DESC
                        """,
                        Object[].class
                )
                .setMaxResults(limit)
                .getResultList();
    }


    // =========================================================
    // MOST SOLVED
    // =========================================================

    public List<Object[]> findMostSolvedProblems(
            int limit) {

        return entityManager
                .createQuery(
                        """
                        SELECT
                            p.id,
                            p.title,
                            p.difficulty,
                            COUNT(s.id)
                        FROM Problem p
                        JOIN Submission s
                            ON s.problem.id = p.id
                        WHERE s.status =
                            com.rohit.leetcode.submission.entity.Submission.SubmissionStatus.ACCEPTED
                        GROUP BY
                            p.id,
                            p.title,
                            p.difficulty
                        ORDER BY COUNT(s.id) DESC
                        """,
                        Object[].class
                )
                .setMaxResults(limit)
                .getResultList();
    }


    // =========================================================
    // ACCEPTED COUNT FOR PROBLEM
    // =========================================================

    public long countAcceptedForProblem(
            Long problemId) {

        return entityManager
                .createQuery(
                        """
                        SELECT COUNT(s)
                        FROM Submission s
                        WHERE s.problem.id = :problemId
                        AND s.status =
                            com.rohit.leetcode.submission.entity.Submission.SubmissionStatus.ACCEPTED
                        """,
                        Long.class
                )
                .setParameter(
                        "problemId",
                        problemId
                )
                .getSingleResult();
    }
}