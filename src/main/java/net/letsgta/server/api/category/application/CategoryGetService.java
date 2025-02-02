package net.letsgta.server.api.category.application;

import java.util.List;
import net.letsgta.server.api.category.dto.response.CategoryGetResponse;

public interface CategoryGetService {

        CategoryGetResponse getCategory(int categoryId);
        List<CategoryGetResponse> getAllCategories();
}
