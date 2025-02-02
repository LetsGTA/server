package net.letsgta.server.api.category.application;

import net.letsgta.server.api.category.dto.request.CategoryCreateRequest;

public interface CategoryCreateService {

    void createCategory(CategoryCreateRequest request);
}
