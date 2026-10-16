package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.dto.DailyChallengeResponse;
import com.rohit.leetcode.platform.entity.DailyChallenge;
import com.rohit.leetcode.platform.entity.DailyChallengeAttempt;
import com.rohit.leetcode.platform.repository.DailyChallengeAttemptRepository;
import com.rohit.leetcode.platform.repository.DailyChallengeRepository;
import com.rohit.leetcode.platform.service.DailyChallengeService;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyChallengeServiceImpl
        implements DailyChallengeService {

    private final DailyChallengeRepository challengeRepository;
    private final DailyChallengeAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DailyChallengeResponse getTodayChallenge() {

        return getChallengeByDate(
                LocalDate.now().toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DailyChallengeResponse getChallengeByDate(
            String date) {

        LocalDate challengeDate;

        try {
            challengeDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid date format. Use yyyy-MM-dd"
            );
        }

        DailyChallenge challenge =
                challengeRepository
                        .findByChallengeDate(challengeDate)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Daily challenge not found"
                                ));

        return mapToResponse(
                challenge,
                getCurrentUser()
        );
    }

    @Override
    public DailyChallengeResponse completeChallenge(
            Long challengeId) {

        User user = getCurrentUser();

        DailyChallenge challenge =
                challengeRepository.findById(challengeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Daily challenge not found"
                                ));

        DailyChallengeAttempt attempt =
                attemptRepository
                        .findByChallengeIdAndUserId(
                                challengeId,
                                user.getId()
                        )
                        .orElseGet(() ->
                                DailyChallengeAttempt.builder()
                                        .challenge(challenge)
                                        .user(user)
                                        .completed(false)
                                        .build()
                        );

        if (!Boolean.TRUE.equals(attempt.getCompleted())) {

            attempt.setCompleted(true);
            attempt.setCompletedAt(
                    LocalDateTime.now()
            );

            attemptRepository.save(attempt);
        }

        return mapToResponse(
                challenge,
                user
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyChallengeResponse> getHistory() {

        User user = getCurrentUser();

        return attemptRepository
                .findByUserIdAndCompletedTrueOrderByCompletedAtDesc(
                        user.getId()
                )
                .stream()
                .map(attempt ->
                        mapToResponse(
                                attempt.getChallenge(),
                                user
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCurrentStreak() {

        User user = getCurrentUser();

        List<DailyChallengeAttempt> attempts =
                attemptRepository
                        .findByUserIdAndCompletedTrueOrderByCompletedAtDesc(
                                user.getId()
                        );

        if (attempts.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        for (DailyChallengeAttempt attempt : attempts) {

            LocalDate date =
                    attempt.getChallenge()
                            .getChallengeDate();

            if (date.equals(expectedDate)) {

                streak++;
                expectedDate = expectedDate.minusDays(1);

            } else if (date.isBefore(expectedDate)) {

                break;
            }
        }

        return streak;
    }

    private DailyChallengeResponse mapToResponse(
            DailyChallenge challenge,
            User user) {

        DailyChallengeAttempt attempt =
                attemptRepository
                        .findByChallengeIdAndUserId(
                                challenge.getId(),
                                user.getId()
                        )
                        .orElse(null);

        boolean completed =
                attempt != null &&
                        Boolean.TRUE.equals(attempt.getCompleted());

        return DailyChallengeResponse.builder()
                .id(challenge.getId())
                .challengeDate(
                        challenge.getChallengeDate()
                )
                .problemId(
                        challenge.getProblem().getId()
                )
                .problemTitle(
                        challenge.getProblem().getTitle()
                )
                .difficulty(
                        challenge.getProblem()
                                .getDifficulty()
                                .name()
                )
                .topic(
                        challenge.getProblem().getTopic()
                )
                .completed(completed)
                .completedAt(
                        attempt == null
                                ? null
                                : attempt.getCompletedAt()
                )
                .currentStreak(getCurrentStreak())
                .longestStreak(getCurrentStreak())
                .xpEarned(
                        completed
                                ? challenge.getXpReward()
                                : 0
                )
                .build();
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        ));
    }
}