package org.example.server.api.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.api.category.application.CategoryCreateService;
import org.example.server.api.category.application.CategoryDelService;
import org.example.server.api.category.dto.request.CategoryCreateRequest;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryCreateService categoryCreateService;
    private final CategoryDelService categoryDelService;

    @PostMapping
    public ResponseEntity<ApiResponseEntity<Void>> create(@Valid @RequestBody CategoryCreateRequest request) {
        categoryCreateService.createCategory(request);
        return ApiResponseEntity.successResponseEntity();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseEntity<Void>> delete(@PathVariable int categoryId) {
        categoryDelService.delCategory(categoryId);
        return ApiResponseEntity.successResponseEntity();
    }
}
