package net.letsgta.server.api.category.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.category.application.CategoryDelService;
import net.letsgta.server.api.category.entity.Category;
import net.letsgta.server.api.category.exception.CategoryException;
import net.letsgta.server.api.category.exception.CategoryExceptionResult;
import net.letsgta.server.api.category.repository.CategoryRepository;
import net.letsgta.server.api.post.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryDelServiceImpl implements CategoryDelService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public void delCategory(int categoryId) {
        if(categoryId < 0) {
            throw new CategoryException(CategoryExceptionResult.INVALID_PARAMETER);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionResult.NOT_EXISTS));

        Category tempCategory = categoryRepository.findByName("임시")
                .orElseGet(this::createTempCategory);

        for(Post post : category.getPostList()) {
            post.assignCategory(tempCategory);
        }

        categoryRepository.delete(category);
    }

    private Category createTempCategory() {
        Category tempCategory = Category.builder()
                .name("임시")
                .build();

        return categoryRepository.save(tempCategory);
    }
}
