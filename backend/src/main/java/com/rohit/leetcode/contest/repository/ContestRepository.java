package com.rohit.leetcode.contest.repository;

import com.rohit.leetcode.contest.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository
        extends JpaRepository<Contest, Long> {

    List<Contest> findByPublishedTrueOrderByStartTimeAsc();

    List<Contest> findAllByOrderByStartTimeDesc();
}