package org.example.server.api.category.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.category.application.CategoryCreateService;
import org.example.server.api.category.dto.request.CategoryCreateRequest;
import org.example.server.api.category.entity.Category;
import org.example.server.api.category.exception.CategoryException;
import org.example.server.api.category.exception.CategoryExceptionResult;
import org.example.server.api.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCreateServiceImpl implements CategoryCreateService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public void createCategory(CategoryCreateRequest request) {
        if(categoryRepository.existsByName(request.name())) {
            throw new CategoryException(CategoryExceptionResult.ALREADY_EXISTS);
        }

        Category category;
        if (request.parentId() == 0) {
            category = Category.builder()
                    .name(request.name())
                    .parent(null)
                    .build();
        } else {
            Category parentCategory = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new CategoryException(CategoryExceptionResult.NOT_EXISTS));
            category = Category.builder()
                    .name(request.name())
                    .parent(parentCategory)
                    .build();
        }

        categoryRepository.save(category);
    }
}