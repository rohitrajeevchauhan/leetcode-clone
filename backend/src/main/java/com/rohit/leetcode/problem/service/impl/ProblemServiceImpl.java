package com.rohit.leetcode.problem.service.impl;

import com.rohit.leetcode.problem.dto.ProblemRequest;
import com.rohit.leetcode.problem.dto.ProblemResponse;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.service.ProblemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    // CREATE
    @Override
    public ProblemResponse createProblem(ProblemRequest request) {

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .difficulty(request.getDifficulty())
                .description(request.getDescription())
                .constraints(request.getConstraints())
                .example(request.getExample())
                .starterCode(request.getStarterCode())
                .topic(request.getTopic())
                .tags(request.getTags())
                .javaStarterCode(request.getJavaStarterCode())
                .cppStarterCode(request.getCppStarterCode())
                .pythonStarterCode(request.getPythonStarterCode())
                .javascriptStarterCode(request.getJavascriptStarterCode())
                .functionName(request.getFunctionName())
                .returnType(request.getReturnType())
                .parameterDefinition(request.getParameterDefinition())
                .build();

        Problem savedProblem = problemRepository.save(problem);

        return mapToResponse(savedProblem);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<ProblemResponse> getAllProblems() {

        return problemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(Long id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Problem not found with id: " + id
                        )
                );

        return mapToResponse(problem);
    }

    // UPDATE
    @Override
    public ProblemResponse updateProblem(
            Long id,
            ProblemRequest request) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Problem not found with id: " + id
                        )
                );

        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setDescription(request.getDescription());
        problem.setConstraints(request.getConstraints());
        problem.setExample(request.getExample());
        problem.setStarterCode(request.getStarterCode());
        problem.setTopic(request.getTopic());
        problem.setTags(request.getTags());

        problem.setJavaStarterCode(
                request.getJavaStarterCode()
        );

        problem.setCppStarterCode(
                request.getCppStarterCode()
        );

        problem.setPythonStarterCode(
                request.getPythonStarterCode()
        );

        problem.setJavascriptStarterCode(
                request.getJavascriptStarterCode()
        );

        problem.setFunctionName(
                request.getFunctionName()
        );

        problem.setReturnType(
                request.getReturnType()
        );

        problem.setParameterDefinition(
                request.getParameterDefinition()
        );

        Problem updatedProblem =
                problemRepository.save(problem);

        return mapToResponse(updatedProblem);
    }

    // DELETE
    @Override
    public void deleteProblem(Long id) {

        if (!problemRepository.existsById(id)) {

            throw new RuntimeException(
                    "Problem not found with id: " + id
            );
        }

        problemRepository.deleteById(id);
    }

    // ENTITY → RESPONSE
    private ProblemResponse mapToResponse(Problem problem) {

        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .description(problem.getDescription())
                .constraints(problem.getConstraints())
                .example(problem.getExample())
                .starterCode(problem.getStarterCode())
                .topic(problem.getTopic())
                .tags(problem.getTags())
                .javaStarterCode(problem.getJavaStarterCode())
                .cppStarterCode(problem.getCppStarterCode())
                .pythonStarterCode(problem.getPythonStarterCode())
                .javascriptStarterCode(
                        problem.getJavascriptStarterCode()
                )
                .functionName(problem.getFunctionName())
                .returnType(problem.getReturnType())
                .parameterDefinition(
                        problem.getParameterDefinition()
                )
                .build();
    }
}