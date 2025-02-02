package org.example.server.api.category.application;

import org.example.server.api.category.dto.request.CategoryCreateRequest;

public interface CategoryCreateService {

    void createCategory(CategoryCreateRequest request);
}
