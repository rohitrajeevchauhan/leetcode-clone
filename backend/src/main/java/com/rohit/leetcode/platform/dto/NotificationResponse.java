package com.rohit.leetcode.platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private String type;

    private String title;

    private String message;

    private String referenceType;

    private Long referenceId;

    private boolean read;

    private LocalDateTime createdAt;
}