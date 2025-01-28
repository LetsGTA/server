package org.example.server.api.category.application;

import org.example.server.api.category.dto.response.CategoryGetResponse;

public interface CategoryGetService {

        CategoryGetResponse getCategory(int categoryId);
}
