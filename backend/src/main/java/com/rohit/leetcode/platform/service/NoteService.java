package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.ProblemNoteRequest;
import com.rohit.leetcode.platform.dto.ProblemNoteResponse;

import java.util.List;

public interface NoteService {

    ProblemNoteResponse createNote(
            Long problemId,
            ProblemNoteRequest request
    );

    ProblemNoteResponse getNote(Long problemId);

    List<ProblemNoteResponse> getMyNotes();

    ProblemNoteResponse updateNote(
            Long problemId,
            ProblemNoteRequest request
    );

    void deleteNote(Long problemId);
}