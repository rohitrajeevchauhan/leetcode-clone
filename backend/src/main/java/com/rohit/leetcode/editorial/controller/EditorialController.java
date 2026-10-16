package com.rohit.leetcode.editorial.controller;

import com.rohit.leetcode.editorial.dto.EditorialRequest;
import com.rohit.leetcode.editorial.dto.EditorialResponse;
import com.rohit.leetcode.editorial.service.EditorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editorials")
@RequiredArgsConstructor
public class EditorialController {

    private final EditorialService editorialService;

    @PostMapping
    public EditorialResponse create(
            @Valid @RequestBody EditorialRequest request) {

        return editorialService.create(request);
    }

    @GetMapping("/problem/{problemId}")
    public EditorialResponse getByProblem(
            @PathVariable Long problemId) {

        return editorialService.getByProblem(problemId);
    }

    @GetMapping
    public List<EditorialResponse> getAll() {

        return editorialService.getAll();
    }

    @PutMapping("/{id}")
    public EditorialResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EditorialRequest request) {

        return editorialService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        editorialService.delete(id);
    }
}