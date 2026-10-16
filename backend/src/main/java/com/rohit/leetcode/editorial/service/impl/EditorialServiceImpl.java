package com.rohit.leetcode.editorial.service.impl;

import com.rohit.leetcode.editorial.dto.EditorialRequest;
import com.rohit.leetcode.editorial.dto.EditorialResponse;
import com.rohit.leetcode.editorial.entity.Editorial;
import com.rohit.leetcode.editorial.repository.EditorialRepository;
import com.rohit.leetcode.editorial.service.EditorialService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EditorialServiceImpl
        implements EditorialService {

    private final EditorialRepository editorialRepository;
    private final ProblemRepository problemRepository;

    @Override
    public EditorialResponse create(
            EditorialRequest request) {

        if (editorialRepository.existsByProblemId(
                request.getProblemId())) {

            throw new IllegalStateException(
                    "Editorial already exists for this problem"
            );
        }

        Problem problem =
                problemRepository.findById(
                        request.getProblemId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Problem not found"
                        ));

        Editorial editorial =
                Editorial.builder()
                        .problem(problem)
                        .title(request.getTitle())
                        .explanation(request.getExplanation())
                        .approach(request.getApproach())
                        .algorithm(request.getAlgorithm())
                        .complexity(request.getComplexity())
                        .javaCode(request.getJavaCode())
                        .cppCode(request.getCppCode())
                        .pythonCode(request.getPythonCode())
                        .javascriptCode(
                                request.getJavascriptCode()
                        )
                        .published(request.isPublished())
                        .build();

        return map(
                editorialRepository.save(editorial)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResponse getByProblem(
            Long problemId) {

        Editorial editorial =
                editorialRepository
                        .findByProblemId(problemId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Editorial not found"
                                ));

        if (!editorial.isPublished()) {
            throw new IllegalStateException(
                    "Editorial is not published"
            );
        }

        return map(editorial);
    }

    @Override
    public EditorialResponse update(
            Long id,
            EditorialRequest request) {

        Editorial editorial =
                editorialRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Editorial not found"
                                ));

        editorial.setTitle(request.getTitle());
        editorial.setExplanation(
                request.getExplanation()
        );
        editorial.setApproach(request.getApproach());
        editorial.setAlgorithm(request.getAlgorithm());
        editorial.setComplexity(request.getComplexity());
        editorial.setJavaCode(request.getJavaCode());
        editorial.setCppCode(request.getCppCode());
        editorial.setPythonCode(request.getPythonCode());
        editorial.setJavascriptCode(
                request.getJavascriptCode()
        );
        editorial.setPublished(request.isPublished());

        return map(
                editorialRepository.save(editorial)
        );
    }

    @Override
    public void delete(Long id) {

        if (!editorialRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Editorial not found"
            );
        }

        editorialRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EditorialResponse> getAll() {

        return editorialRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private EditorialResponse map(
            Editorial editorial) {

        return EditorialResponse.builder()
                .id(editorial.getId())
                .problemId(
                        editorial.getProblem().getId()
                )
                .problemTitle(
                        editorial.getProblem().getTitle()
                )
                .title(editorial.getTitle())
                .explanation(
                        editorial.getExplanation()
                )
                .approach(editorial.getApproach())
                .algorithm(editorial.getAlgorithm())
                .complexity(editorial.getComplexity())
                .javaCode(editorial.getJavaCode())
                .cppCode(editorial.getCppCode())
                .pythonCode(
                        editorial.getPythonCode()
                )
                .javascriptCode(
                        editorial.getJavascriptCode()
                )
                .published(editorial.isPublished())
                .createdAt(editorial.getCreatedAt())
                .updatedAt(editorial.getUpdatedAt())
                .build();
    }
}