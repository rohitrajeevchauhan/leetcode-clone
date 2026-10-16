package com.rohit.leetcode.editorial.repository;

import com.rohit.leetcode.editorial.entity.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditorialRepository
        extends JpaRepository<Editorial, Long> {

    Optional<Editorial> findByProblemId(Long problemId);

    boolean existsByProblemId(Long problemId);
}