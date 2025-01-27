package org.example.server.api.category.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.category.application.CategoryCreateService;
import org.example.server.api.category.dto.request.CategoryCreateRequest;
import org.example.server.api.category.entity.Category;
import org.example.server.api.category.exception.CategoryException;
import org.example.server.api.category.exception.CategoryExceptionResult;
import org.example.server.api.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryCreateServiceImpl implements CategoryCreateService {

    private final CategoryRepository categoryRepository;

    @Override
    public void createCategory(CategoryCreateRequest request) {
        if(categoryRepository.existsByName(request.name())) {
            throw new CategoryException(CategoryExceptionResult.ALREADY_EXISTS);
        }

        Category parentCategory = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionResult.NOT_EXISTS));

        categoryRepository.save(Category.builder()
                        .name(request.name())
                        .parent(parentCategory)
                .build());
    }
}