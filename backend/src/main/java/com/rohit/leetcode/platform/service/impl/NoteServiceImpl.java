package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.dto.ProblemNoteRequest;
import com.rohit.leetcode.platform.dto.ProblemNoteResponse;
import com.rohit.leetcode.platform.entity.ProblemNote;
import com.rohit.leetcode.platform.repository.ProblemNoteRepository;
import com.rohit.leetcode.platform.service.NoteService;
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
public class NoteServiceImpl implements NoteService {

    private final ProblemNoteRepository noteRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Override
    public ProblemNoteResponse createNote(
            Long problemId,
            ProblemNoteRequest request) {

        User user = getCurrentUser();

        Problem problem =
                problemRepository.findById(problemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Problem not found"
                                ));

        ProblemNote note =
                noteRepository
                        .findByUserIdAndProblemId(
                                user.getId(),
                                problemId
                        )
                        .orElse(
                                ProblemNote.builder()
                                        .user(user)
                                        .problem(problem)
                                        .build()
                        );

        note.setContent(request.getContent());

        return mapToResponse(
                noteRepository.save(note)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemNoteResponse getNote(
            Long problemId) {

        User user = getCurrentUser();

        ProblemNote note =
                noteRepository
                        .findByUserIdAndProblemId(
                                user.getId(),
                                problemId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Note not found"
                                ));

        return mapToResponse(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemNoteResponse> getMyNotes() {

        return noteRepository
                .findByUserIdOrderByUpdatedAtDesc(
                        getCurrentUser().getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProblemNoteResponse updateNote(
            Long problemId,
            ProblemNoteRequest request) {

        User user = getCurrentUser();

        ProblemNote note =
                noteRepository
                        .findByUserIdAndProblemId(
                                user.getId(),
                                problemId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Note not found"
                                ));

        note.setContent(request.getContent());

        return mapToResponse(
                noteRepository.save(note)
        );
    }

    @Override
    public void deleteNote(Long problemId) {

        User user = getCurrentUser();

        ProblemNote note =
                noteRepository
                        .findByUserIdAndProblemId(
                                user.getId(),
                                problemId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Note not found"
                                ));

        noteRepository.delete(note);
    }

    private ProblemNoteResponse mapToResponse(
            ProblemNote note) {

        return ProblemNoteResponse.builder()
                .id(note.getId())
                .problemId(note.getProblem().getId())
                .problemTitle(note.getProblem().getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        ));
    }
}