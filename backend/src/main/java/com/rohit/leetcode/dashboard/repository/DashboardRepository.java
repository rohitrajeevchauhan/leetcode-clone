package com.rohit.leetcode.dashboard.repository;

import com.rohit.leetcode.favorite.entity.Favorite;
import com.rohit.leetcode.submission.entity.Submission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;


    // ==========================================
    // ALL USER SUBMISSION DATES
    // ==========================================

    public List<Submission> findUserSubmissions(
            Long userId
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT s
                        FROM Submission s
                        WHERE s.user.id = :userId
                        ORDER BY s.createdAt ASC
                        """,
                        Submission.class
                )
                .setParameter("userId", userId)
                .getResultList();
    }


    // ==========================================
    // RECENT SUBMISSIONS
    // ==========================================

    public List<Submission> findRecentSubmissions(
            Long userId,
            int limit
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT s
                        FROM Submission s
                        JOIN FETCH s.problem
                        WHERE s.user.id = :userId
                        ORDER BY s.createdAt DESC
                        """,
                        Submission.class
                )
                .setParameter("userId", userId)
                .setMaxResults(limit)
                .getResultList();
    }


    // ==========================================
    // RECENT FAVORITES
    // ==========================================

    public List<Favorite> findRecentFavorites(
            Long userId,
            int limit
    ) {

        return entityManager
                .createQuery(
                        """
                        SELECT f
                        FROM Favorite f
                        JOIN FETCH f.problem
                        WHERE f.user.id = :userId
                        ORDER BY f.createdAt DESC
                        """,
                        Favorite.class
                )
                .setParameter("userId", userId)
                .setMaxResults(limit)
                .getResultList();
    }
}