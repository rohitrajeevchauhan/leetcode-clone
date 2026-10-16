package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.ProblemNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemNoteRepository
        extends JpaRepository<ProblemNote, Long> {

    Optional<ProblemNote>
    findByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    List<ProblemNote>
    findByUserIdOrderByUpdatedAtDesc(Long userId);

    boolean existsByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    long countByUserId(Long userId);
}