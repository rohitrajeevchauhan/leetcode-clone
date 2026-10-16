
package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.dto.BookmarkResponse;
import com.rohit.leetcode.platform.entity.Bookmark;
import com.rohit.leetcode.platform.repository.BookmarkRepository;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import com.rohit.leetcode.platform.service.BookmarkService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Please log in to manage bookmarks."
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found.")
                );
    }

    @Override
    public BookmarkResponse bookmarkProblem(Long problemId) {
        User user = getCurrentUser();

        Bookmark existing = bookmarkRepository
                .findByUser_IdAndProblem_Id(user.getId(), problemId)
                .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Problem not found with ID: " + problemId
                        )
                );

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .problem(problem)
                .build();

        return toResponse(bookmarkRepository.save(bookmark));
    }

    @Override
    public BookmarkResponse removeProblemBookmark(Long problemId) {
        User user = getCurrentUser();

        Bookmark bookmark = bookmarkRepository
                .findByUser_IdAndProblem_Id(user.getId(), problemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Bookmark not found for problem ID: " + problemId
                        )
                );

        BookmarkResponse response = toResponse(bookmark);

        bookmarkRepository.delete(bookmark);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getMyBookmarks() {
        User user = getCurrentUser();

        return bookmarkRepository
                .findByUser_Id(
                        user.getId(),
                        org.springframework.data.domain.PageRequest
                                .of(0, 100,
                                        org.springframework.data.domain.Sort
                                                .by("createdAt").descending())
                )
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long problemId) {
        User user = getCurrentUser();

        return bookmarkRepository.existsByUser_IdAndProblem_Id(
                user.getId(),
                problemId
        );
    }

    private BookmarkResponse toResponse(Bookmark bookmark) {
        Problem problem = bookmark.getProblem();

        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .problemId(problem.getId())
                .problemTitle(problem.getTitle())
                .difficulty(
                        problem.getDifficulty() == null
                                ? null
                                : problem.getDifficulty().toString()
                )
                .topic(problem.getTopic())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
