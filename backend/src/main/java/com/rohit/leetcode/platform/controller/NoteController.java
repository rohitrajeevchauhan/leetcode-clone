package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.ProblemNoteRequest;
import com.rohit.leetcode.platform.dto.ProblemNoteResponse;
import com.rohit.leetcode.platform.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/problem/{problemId}")
    public ProblemNoteResponse createNote(
            @PathVariable Long problemId,
            @Valid @RequestBody ProblemNoteRequest request) {

        return noteService.createNote(problemId, request);
    }

    @GetMapping("/problem/{problemId}")
    public ProblemNoteResponse getNote(
            @PathVariable Long problemId) {

        return noteService.getNote(problemId);
    }

    @GetMapping
    public List<ProblemNoteResponse> getMyNotes() {
        return noteService.getMyNotes();
    }

    @PutMapping("/problem/{problemId}")
    public ProblemNoteResponse updateNote(
            @PathVariable Long problemId,
            @Valid @RequestBody ProblemNoteRequest request) {

        return noteService.updateNote(problemId, request);
    }

    @DeleteMapping("/problem/{problemId}")
    public void deleteNote(
            @PathVariable Long problemId) {

        noteService.deleteNote(problemId);
    }
}