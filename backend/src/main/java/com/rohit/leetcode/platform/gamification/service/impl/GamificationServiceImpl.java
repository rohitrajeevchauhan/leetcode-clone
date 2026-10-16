package com.rohit.leetcode.platform.gamification.service.impl;

import com.rohit.leetcode.platform.dto.NotificationResponse;
import com.rohit.leetcode.platform.gamification.dto.GamificationAchievementResponse;
import com.rohit.leetcode.platform.gamification.dto.GamificationDashboardResponse;
import com.rohit.leetcode.platform.gamification.dto.LeaderboardEntryResponse;
import com.rohit.leetcode.platform.gamification.entity.GamificationAchievement;
import com.rohit.leetcode.platform.gamification.entity.GamificationProfile;
import com.rohit.leetcode.platform.gamification.entity.SolvedProblemReward;
import com.rohit.leetcode.platform.gamification.repository.GamificationAchievementRepository;
import com.rohit.leetcode.platform.gamification.repository.GamificationProfileRepository;
import com.rohit.leetcode.platform.gamification.repository.SolvedProblemRewardRepository;
import com.rohit.leetcode.platform.gamification.service.GamificationService;
import com.rohit.leetcode.platform.service.NotificationService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GamificationServiceImpl implements GamificationService {

    private static final int FIRST_SOLVE_XP = 30;

    private final GamificationProfileRepository profileRepository;
    private final SolvedProblemRewardRepository rewardRepository;
    private final GamificationAchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public GamificationDashboardResponse getDashboard() {
        User user = getCurrentUser();

        GamificationProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElse(null);

        List<GamificationAchievementResponse> achievements =
                achievementRepository
                        .findByUserIdOrderByUnlockedAtDesc(user.getId())
                        .stream()
                        .map(GamificationAchievementResponse::from)
                        .toList();

        if (profile == null) {
            return new GamificationDashboardResponse(
                    0,
                    1,
                    0,
                    100,
                    0,
                    0,
                    0,
                    achievements
            );
        }

        int progress = profile.getLevelProgress();

        return new GamificationDashboardResponse(
                profile.getTotalXp(),
                profile.getLevel(),
                progress,
                100 - progress,
                profile.getProblemsSolved(),
                profile.getContestsCompleted(),
                profile.getCorrectQuizAnswers(),
                achievements
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard() {
        List<GamificationProfile> profiles =
                profileRepository.findTop10ByOrderByTotalXpDesc();

        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();

        for (int i = 0; i < profiles.size(); i++) {
            GamificationProfile profile = profiles.get(i);
            User user = profile.getUser();

            leaderboard.add(new LeaderboardEntryResponse(
                    i + 1,
                    user.getId(),
                    user.getUsername(),
                    profile.getTotalXp(),
                    profile.getLevel(),
                    profile.getProblemsSolved()
            ));
        }

        return leaderboard;
    }

    @Override
    public void awardAcceptedSubmission(User user, Problem problem) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("A valid user is required");
        }

        if (problem == null || problem.getId() == null) {
            throw new IllegalArgumentException("A valid problem is required");
        }

        // A user receives this reward only once for each problem.
        if (rewardRepository.existsByUserIdAndProblemId(
                user.getId(),
                problem.getId()
        )) {
            return;
        }

        GamificationProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElseGet(() -> profileRepository.save(
                        GamificationProfile.builder()
                                .user(user)
                                .totalXp(0)
                                .problemsSolved(0)
                                .contestsCompleted(0)
                                .correctQuizAnswers(0)
                                .build()
                ));

        rewardRepository.save(
                SolvedProblemReward.builder()
                        .user(user)
                        .problem(problem)
                        .xpAwarded(FIRST_SOLVE_XP)
                        .build()
        );

        profile.setTotalXp(profile.getTotalXp() + FIRST_SOLVE_XP);
        profile.setProblemsSolved(profile.getProblemsSolved() + 1);

        profileRepository.save(profile);

        unlockEligibleAchievements(user, profile);
    }

    private void unlockEligibleAchievements(
            User user,
            GamificationProfile profile
    ) {
        int solved = profile.getProblemsSolved();

        if (solved >= 1) {
            unlockAchievement(
                    user,
                    profile,
                    "FIRST_SOLVE",
                    "First Blood",
                    "Solve your first coding problem.",
                    20
            );
        }

        if (solved >= 10) {
            unlockAchievement(
                    user,
                    profile,
                    "SOLVER_10",
                    "Getting Started",
                    "Solve 10 different coding problems.",
                    50
            );
        }

        if (solved >= 50) {
            unlockAchievement(
                    user,
                    profile,
                    "SOLVER_50",
                    "Problem Hunter",
                    "Solve 50 different coding problems.",
                    100
            );
        }

        if (solved >= 100) {
            unlockAchievement(
                    user,
                    profile,
                    "CENTURY_CLUB",
                    "Century Club",
                    "Solve 100 different coding problems.",
                    200
            );
        }
    }

    private void unlockAchievement(
            User user,
            GamificationProfile profile,
            String code,
            String title,
            String description,
            int bonusXp
    ) {
        if (achievementRepository.existsByUserIdAndAchievementCode(
                user.getId(),
                code
        )) {
            return;
        }

        GamificationAchievement achievement =
                GamificationAchievement.builder()
                        .user(user)
                        .achievementCode(code)
                        .title(title)
                        .description(description)
                        .xpReward(bonusXp)
                        .build();

        achievementRepository.save(achievement);

        profile.setTotalXp(profile.getTotalXp() + bonusXp);
        profileRepository.save(profile);

        notificationService.createNotification(
                user.getId(),
                "ACHIEVEMENT_UNLOCKED",
                "Achievement unlocked: " + title,
                description + " You earned " + bonusXp + " bonus XP.",
                "ACHIEVEMENT",
                achievement.getId()
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Please log in to access gamification");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new SecurityException("Authenticated user was not found"));
    }
}