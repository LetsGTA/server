package net.letsgta.server.api.category.application.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.letsgta.server.api.category.dto.request.CategoryCreateRequest;
import net.letsgta.server.api.category.entity.Category;
import net.letsgta.server.api.category.exception.CategoryException;
import net.letsgta.server.api.category.exception.CategoryExceptionResult;
import net.letsgta.server.api.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CategoryCreateServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryCreateServiceImpl categoryCreateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카테고리 이름이 이미 존재하면 예외를 던진다")
    void createCategory_shouldThrowException_whenCategoryNameAlreadyExists() {
        // given
        String categoryName = "Existing Category";
        int parentId = 1;
        CategoryCreateRequest request = new CategoryCreateRequest(categoryName, parentId);

        when(categoryRepository.existsByName(categoryName)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryCreateService.createCategory(request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("categoryExceptionResult", CategoryExceptionResult.ALREADY_EXISTS);


        verify(categoryRepository, times(1)).existsByName(categoryName);
        verify(categoryRepository, never()).findById(anyInt());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("부모 카테고리가 존재하지 않으면 예외를 던진다")
    void createCategory_shouldThrowException_whenParentCategoryDoesNotExist() {
        // given
        String categoryName = "New Category";
        int parentId = 999;
        CategoryCreateRequest request = new CategoryCreateRequest(categoryName, parentId);

        when(categoryRepository.existsByName(categoryName)).thenReturn(false);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryCreateService.createCategory(request))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("categoryExceptionResult", CategoryExceptionResult.NOT_EXISTS);

        verify(categoryRepository, times(1)).existsByName(categoryName);
        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("유효한 요청이면 카테고리를 저장한다")
    void createCategory_shouldSaveCategory_whenValidRequest() {
        // given
        String categoryName = "New Category";
        int parentId = 1;
        CategoryCreateRequest request = new CategoryCreateRequest(categoryName, parentId);
        Category parentCategory = mock(Category.class);

        when(categoryRepository.existsByName(categoryName)).thenReturn(false);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));

        // when
        categoryCreateService.createCategory(request);

        // then
        verify(categoryRepository, times(1)).existsByName(categoryName);
        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
