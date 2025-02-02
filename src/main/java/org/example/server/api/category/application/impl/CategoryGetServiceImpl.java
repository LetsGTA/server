package org.example.server.api.category.application.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.api.category.application.CategoryGetService;
import org.example.server.api.category.dto.response.CategoryGetResponse;
import org.example.server.api.category.entity.Category;
import org.example.server.api.category.exception.CategoryException;
import org.example.server.api.category.exception.CategoryExceptionResult;
import org.example.server.api.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryGetServiceImpl implements CategoryGetService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryGetResponse getCategory(int categoryId) {
        if(categoryId < 0) {
            throw new CategoryException(CategoryExceptionResult.INVALID_PARAMETER);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionResult.NOT_EXISTS));

        return CategoryGetResponse.from(category);
    }

    @Override
    public List<CategoryGetResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryGetResponse::from)
                .toList();
    }
}
