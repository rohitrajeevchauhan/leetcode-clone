package com.rohit.leetcode.dashboard.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyActivityResponse {

    private LocalDate date;

    private long submissionCount;

    private long acceptedCount;
}