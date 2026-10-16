package com.rohit.leetcode.editorial.service;

import com.rohit.leetcode.editorial.dto.EditorialRequest;
import com.rohit.leetcode.editorial.dto.EditorialResponse;

import java.util.List;

public interface EditorialService {

    EditorialResponse create(EditorialRequest request);

    EditorialResponse getByProblem(Long problemId);

    EditorialResponse update(
            Long id,
            EditorialRequest request
    );

    void delete(Long id);

    List<EditorialResponse> getAll();
}