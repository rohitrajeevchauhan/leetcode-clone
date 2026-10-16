
package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Page<Bookmark> findByUser_Id(Long userId, Pageable pageable);

    Optional<Bookmark> findByUser_IdAndProblem_Id(
            Long userId,
            Long problemId
    );

    boolean existsByUser_IdAndProblem_Id(
            Long userId,
            Long problemId
    );

    void deleteByUser_IdAndProblem_Id(
            Long userId,
            Long problemId
    );

    long countByUser_Id(Long userId);
}
