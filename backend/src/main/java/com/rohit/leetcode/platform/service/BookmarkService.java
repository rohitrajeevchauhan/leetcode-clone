
package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.BookmarkResponse;

import java.util.List;

public interface BookmarkService {

    BookmarkResponse bookmarkProblem(Long problemId);

    BookmarkResponse removeProblemBookmark(Long problemId);

    List<BookmarkResponse> getMyBookmarks();

    boolean isBookmarked(Long problemId);
}
