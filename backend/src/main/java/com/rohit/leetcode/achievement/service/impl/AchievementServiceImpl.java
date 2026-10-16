package com.rohit.leetcode.achievement.service.impl;

import com.rohit.leetcode.achievement.dto.*;
import com.rohit.leetcode.achievement.entity.Achievement;
import com.rohit.leetcode.achievement.entity.UserAchievement;
import com.rohit.leetcode.achievement.entity.UserGamification;
import com.rohit.leetcode.achievement.repository.*;
import com.rohit.leetcode.achievement.service.AchievementService;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementServiceImpl
        implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserGamificationRepository gamificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {

        User user = getCurrentUser();

        return achievementRepository.findAll()
                .stream()
                .map(achievement ->
                        AchievementResponse.builder()
                                .id(achievement.getId())
                                .code(achievement.getCode())
                                .name(achievement.getName())
                                .description(
                                        achievement.getDescription()
                                )
                                .icon(achievement.getIcon())
                                .xpReward(
                                        achievement.getXpReward()
                                )
                                .unlocked(
                                        userAchievementRepository
                                                .existsByUserIdAndAchievementId(
                                                        user.getId(),
                                                        achievement.getId()
                                                )
                                )
                                .build()
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getMyAchievements() {

        User user = getCurrentUser();

        return userAchievementRepository
                .findByUserIdOrderByEarnedAtDesc(user.getId())
                .stream()
                .map(item ->
                        UserAchievementResponse.builder()
                                .achievementId(
                                        item.getAchievement().getId()
                                )
                                .code(
                                        item.getAchievement().getCode()
                                )
                                .name(
                                        item.getAchievement().getName()
                                )
                                .description(
                                        item.getAchievement()
                                                .getDescription()
                                )
                                .icon(
                                        item.getAchievement().getIcon()
                                )
                                .xpReward(
                                        item.getAchievement()
                                                .getXpReward()
                                )
                                .earnedAt(
                                        item.getEarnedAt()
                                )
                                .build()
                )
                .toList();
    }

    @Override
    public void checkAchievements(Long userId) {

        UserGamification gamification =
                gamificationRepository
                        .findByUserId(userId)
                        .orElse(null);

        if (gamification == null) {
            return;
        }

        int solved =
                gamification.getProblemsSolved();

        int streak =
                gamification.getCurrentStreak();

        int contests =
                gamification.getContestsParticipated();

        if (solved >= 1) {
            unlock(
                    userId,
                    "FIRST_SOLVE"
            );
        }

        if (solved >= 10) {
            unlock(
                    userId,
                    "TEN_SOLVES"
            );
        }

        if (solved >= 50) {
            unlock(
                    userId,
                    "FIFTY_SOLVES"
            );
        }

        if (solved >= 100) {
            unlock(
                    userId,
                    "HUNDRED_SOLVES"
            );
        }

        if (streak >= 3) {
            unlock(
                    userId,
                    "THREE_DAY_STREAK"
            );
        }

        if (streak >= 7) {
            unlock(
                    userId,
                    "SEVEN_DAY_STREAK"
            );
        }

        if (streak >= 30) {
            unlock(
                    userId,
                    "THIRTY_DAY_STREAK"
            );
        }

        if (contests >= 1) {
            unlock(
                    userId,
                    "FIRST_CONTEST"
            );
        }

        if (contests >= 5) {
            unlock(
                    userId,
                    "FIVE_CONTESTS"
            );
        }
    }

    @Override
    public void awardAchievement(
            Long userId,
            String achievementCode) {

        unlock(
                userId,
                achievementCode
        );
    }

    private void unlock(
            Long userId,
            String achievementCode) {

        Achievement achievement =
                achievementRepository
                        .findByCode(achievementCode)
                        .orElse(null);

        if (achievement == null ||
                !achievement.isActive()) {

            return;
        }

        if (userAchievementRepository
                .existsByUserIdAndAchievementId(
                        userId,
                        achievement.getId()
                )) {

            return;
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                ));

        UserAchievement userAchievement =
                UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .build();

        userAchievementRepository.save(
                userAchievement
        );

        /*
         * Achievement XP is added directly here.
         */
        UserGamification gamification =
                gamificationRepository
                        .findByUserId(userId)
                        .orElse(null);

        if (gamification != null) {

            gamification.setTotalXp(
                    gamification.getTotalXp()
                            + achievement.getXpReward()
            );

            gamificationRepository.save(
                    gamification
            );
        }
    }

    private User getCurrentUser() {

        var authentication =
                org.springframework.security.core.context
                        .SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found"
                        ));
    }
}