package com.rohit.leetcode.achievement;

import com.rohit.leetcode.achievement.entity.Achievement;
import com.rohit.leetcode.achievement.repository.AchievementRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AchievementDataInitializer
        implements CommandLineRunner {

    private final AchievementRepository achievementRepository;

    @Override
    public void run(String... args) {

        create(
                "FIRST_SOLVE",
                "First Blood",
                "Solve your first problem",
                "🏆",
                50
        );

        create(
                "TEN_SOLVES",
                "Getting Started",
                "Solve 10 problems",
                "⭐",
                100
        );

        create(
                "FIFTY_SOLVES",
                "Problem Crusher",
                "Solve 50 problems",
                "🔥",
                250
        );

        create(
                "HUNDRED_SOLVES",
                "Century Club",
                "Solve 100 problems",
                "💯",
                500
        );

        create(
                "THREE_DAY_STREAK",
                "On Fire",
                "Maintain a 3 day solving streak",
                "🔥",
                75
        );

        create(
                "SEVEN_DAY_STREAK",
                "Weekly Warrior",
                "Maintain a 7 day solving streak",
                "⚡",
                150
        );

        create(
                "THIRTY_DAY_STREAK",
                "Consistency King",
                "Maintain a 30 day solving streak",
                "👑",
                500
        );

        create(
                "FIRST_CONTEST",
                "Contestant",
                "Participate in your first contest",
                "🏅",
                100
        );

        create(
                "FIVE_CONTESTS",
                "Contest Veteran",
                "Participate in 5 contests",
                "🎖️",
                300
        );
    }

    private void create(
            String code,
            String name,
            String description,
            String icon,
            int xp) {

        if (achievementRepository.existsByCode(code)) {
            return;
        }

        achievementRepository.save(
                Achievement.builder()
                        .code(code)
                        .name(name)
                        .description(description)
                        .icon(icon)
                        .xpReward(xp)
                        .active(true)
                        .build()
        );
    }
}