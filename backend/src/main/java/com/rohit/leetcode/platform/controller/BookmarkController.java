package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.BookmarkResponse;
import com.rohit.leetcode.platform.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/problem/{problemId}")
    public BookmarkResponse bookmarkProblem(
            @PathVariable Long problemId) {

        return bookmarkService.bookmarkProblem(problemId);
    }

    @DeleteMapping("/problem/{problemId}")
    public BookmarkResponse removeProblemBookmark(
            @PathVariable Long problemId) {

        return bookmarkService.removeProblemBookmark(problemId);
    }

    @GetMapping
    public List<BookmarkResponse> getMyBookmarks() {
        return bookmarkService.getMyBookmarks();
    }

    @GetMapping("/problem/{problemId}")
    public boolean isBookmarked(
            @PathVariable Long problemId) {

        return bookmarkService.isBookmarked(problemId);
    }
}