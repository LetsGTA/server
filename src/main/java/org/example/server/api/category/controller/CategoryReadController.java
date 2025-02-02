package org.example.server.api.category.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.api.category.application.CategoryGetService;
import org.example.server.api.category.dto.response.CategoryGetResponse;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryReadController {

    private final CategoryGetService categoryGetService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponseEntity<CategoryGetResponse>> getCategory(@PathVariable int categoryId) {
        return ApiResponseEntity.successResponseEntity(categoryGetService.getCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<ApiResponseEntity<List<CategoryGetResponse>>> getCategories() {
        return ApiResponseEntity.successResponseEntity(categoryGetService.getAllCategories());
    }
}
