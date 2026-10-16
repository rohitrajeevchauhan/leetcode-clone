package com.rohit.leetcode.problem.service.impl;

import com.rohit.leetcode.favorite.repository.FavoriteRepository;
import com.rohit.leetcode.problem.dto.ProblemSearchResponse;
import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.service.ProblemSearchService;
import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemSearchServiceImpl
        implements ProblemSearchService {

    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ProblemSearchResponse> search(
            String keyword,
            Difficulty difficulty,
            String topic,
            String tag,
            String status,
            boolean favoriteOnly,
            Pageable pageable
    ) {

        User user = getCurrentUserIfAvailable();

        Page<Problem> problems =
                problemRepository.searchProblems(
                        normalize(keyword),
                        difficulty,
                        normalize(topic),
                        normalize(tag),
                        pageable
                );

        return problems.map(problem ->
                mapProblem(
                        problem,
                        user
                )
        );
    }

    private ProblemSearchResponse mapProblem(
            Problem problem,
            User user
    ) {

        String status = "NOT_ATTEMPTED";
        boolean favorite = false;

        long totalSubmissions = 0;
        long acceptedSubmissions = 0;

        if (user != null) {

            totalSubmissions =
                    submissionRepository
                            .countByUserIdAndProblemId(
                                    user.getId(),
                                    problem.getId()
                            );

            acceptedSubmissions =
                    submissionRepository
                            .countByUserIdAndProblemIdAndStatus(
                                    user.getId(),
                                    problem.getId(),
                                    Submission.SubmissionStatus.ACCEPTED
                            );

            favorite =
                    favoriteRepository
                            .existsByUserIdAndProblemId(
                                    user.getId(),
                                    problem.getId()
                            );

            if (acceptedSubmissions > 0) {

                status = "SOLVED";

            } else if (totalSubmissions > 0) {

                status = "ATTEMPTED";
            }
        }

        return ProblemSearchResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(
                        problem.getDifficulty() != null
                                ? problem.getDifficulty().name()
                                : null
                )
                .topic(problem.getTopic())
                .tags(problem.getTags())
                .status(status)
                .favorite(favorite)
                .totalSubmissions(totalSubmissions)
                .acceptedSubmissions(acceptedSubmissions)
                .build();
    }

    private User getCurrentUserIfAvailable() {

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

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }

    private String normalize(String value) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}