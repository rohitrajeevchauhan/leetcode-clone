package com.rohit.leetcode.achievement.repository;

import com.rohit.leetcode.achievement.entity.XpTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpTransactionRepository
        extends JpaRepository<XpTransaction, Long> {

    Page<XpTransaction> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserId(Long userId);
}