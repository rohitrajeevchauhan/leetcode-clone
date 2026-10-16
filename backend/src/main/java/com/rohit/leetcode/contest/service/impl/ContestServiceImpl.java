package com.rohit.leetcode.contest.service.impl;

import com.rohit.leetcode.contest.dto.*;
import com.rohit.leetcode.contest.entity.*;
import com.rohit.leetcode.contest.repository.*;
import com.rohit.leetcode.contest.service.ContestService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestServiceImpl implements ContestService {

    private final ContestRepository contestRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Override
    public ContestResponse createContest(
            CreateContestRequest request) {

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (request.getDurationMinutes() <= 0) {

            throw new IllegalArgumentException(
                    "Duration must be greater than zero"
            );
        }

        Contest contest = Contest.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes(
                        request.getDurationMinutes()
                )
                .published(request.isPublished())
                .build();

        contest.updateStatus();

        contest = contestRepository.save(contest);

        return mapToResponse(
                contest,
                false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ContestResponse getContest(
            Long contestId) {

        Contest contest = getContestEntity(contestId);

        contest.updateStatus();

        boolean registered = false;

        User user = getCurrentUserOrNull();

        if (user != null) {

            registered =
                    registrationRepository
                            .existsByContestIdAndUserId(
                                    contestId,
                                    user.getId()
                            );
        }

        return mapToResponse(
                contest,
                registered
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestResponse> getAllContests() {

        User user = getCurrentUserOrNull();

        return contestRepository
                .findAllByOrderByStartTimeDesc()
                .stream()
                .map(contest -> {

                    contest.updateStatus();

                    boolean registered =
                            user != null &&
                                    registrationRepository
                                            .existsByContestIdAndUserId(
                                                    contest.getId(),
                                                    user.getId()
                                            );

                    return mapToResponse(
                            contest,
                            registered
                    );
                })
                .toList();
    }

    @Override
    public void addProblem(
            Long contestId,
            Long problemId,
            Integer order,
            Integer points) {

        Contest contest =
                getContestEntity(contestId);

        Problem problem =
                problemRepository
                        .findById(problemId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Problem not found"
                                )
                        );

        if (contestProblemRepository
                .existsByContestIdAndProblemId(
                        contestId,
                        problemId
                )) {

            throw new IllegalStateException(
                    "Problem is already part of this contest"
            );
        }

        if (order == null || order <= 0) {

            throw new IllegalArgumentException(
                    "Problem order must be greater than zero"
            );
        }

        if (points == null || points <= 0) {

            throw new IllegalArgumentException(
                    "Points must be greater than zero"
            );
        }

        ContestProblem contestProblem =
                ContestProblem.builder()
                        .contest(contest)
                        .problem(problem)
                        .problemOrder(order)
                        .points(points)
                        .build();

        contestProblemRepository.save(
                contestProblem
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestProblemResponse>
    getContestProblems(Long contestId) {

        getContestEntity(contestId);

        return contestProblemRepository
                .findByContestIdOrderByProblemOrderAsc(
                        contestId
                )
                .stream()
                .map(this::mapProblem)
                .toList();
    }

    @Override
    public void register(Long contestId) {

        Contest contest =
                getContestEntity(contestId);

        User user = getCurrentUser();

        contest.updateStatus();

        if (contest.getStatus()
                == ContestStatus.ENDED) {

            throw new IllegalStateException(
                    "Contest has already ended"
            );
        }

        if (registrationRepository
                .existsByContestIdAndUserId(
                        contestId,
                        user.getId()
                )) {

            throw new IllegalStateException(
                    "Already registered for this contest"
            );
        }

        ContestRegistration registration =
                ContestRegistration.builder()
                        .contest(contest)
                        .user(user)
                        .build();

        registrationRepository.save(
                registration
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRegistered(Long contestId) {

        User user = getCurrentUser();

        return registrationRepository
                .existsByContestIdAndUserId(
                        contestId,
                        user.getId()
                );
    }

    private ContestResponse mapToResponse(
            Contest contest,
            boolean registered) {

        return ContestResponse.builder()
                .id(contest.getId())
                .title(contest.getTitle())
                .description(contest.getDescription())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .durationMinutes(
                        contest.getDurationMinutes()
                )
                .status(
                        contest.getStatus().name()
                )
                .published(
                        contest.isPublished()
                )
                .problemCount(
                        contestProblemRepository
                                .countByContestId(
                                        contest.getId()
                                )
                )
                .participantCount(
                        registrationRepository
                                .countByContestId(
                                        contest.getId()
                                )
                )
                .registered(registered)
                .createdAt(contest.getCreatedAt())
                .build();
    }

    private ContestProblemResponse mapProblem(
            ContestProblem contestProblem) {

        Problem problem =
                contestProblem.getProblem();

        return ContestProblemResponse.builder()
                .contestProblemId(
                        contestProblem.getId()
                )
                .problemId(
                        problem.getId()
                )
                .title(
                        problem.getTitle()
                )
                .difficulty(
                        problem.getDifficulty() == null
                                ? null
                                : problem.getDifficulty().name()
                )
                .topic(
                        problem.getTopic()
                )
                .problemOrder(
                        contestProblem.getProblemOrder()
                )
                .points(
                        contestProblem.getPoints()
                )
                .build();
    }

    private Contest getContestEntity(
            Long contestId) {

        return contestRepository
                .findById(contestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contest not found with id: "
                                        + contestId
                        )
                );
    }

    private User getCurrentUser() {

        User user = getCurrentUserOrNull();

        if (user == null) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        return user;
    }

    private User getCurrentUserOrNull() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(
                        authentication.getPrincipal()
                )) {

            return null;
        }

        return userRepository
                .findByEmail(
                        authentication.getName()
                )
                .orElse(null);
    }
}