package com.rohit.leetcode.achievement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XpTransactionResponse {

    private Long id;

    private Long amount;

    private String reason;

    private Long referenceId;

    private LocalDateTime createdAt;
}