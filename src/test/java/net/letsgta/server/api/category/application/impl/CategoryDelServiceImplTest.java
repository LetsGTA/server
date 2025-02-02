package net.letsgta.server.api.category.application.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import net.letsgta.server.api.category.entity.Category;
import net.letsgta.server.api.category.exception.CategoryException;
import net.letsgta.server.api.category.exception.CategoryExceptionResult;
import net.letsgta.server.api.category.repository.CategoryRepository;
import net.letsgta.server.api.post.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CategoryDelServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryDelServiceImpl categoryDelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카테고리 삭제 - 유효하지 않은 카테고리 ID 입력 시 예외 발생")
    void delCategory_ShouldThrowException_WhenCategoryIdIsInvalid() {
        // given
        int invalidCategoryId = -1;

        // when & then
        assertThatThrownBy(() -> categoryDelService.delCategory(invalidCategoryId))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("categoryExceptionResult", CategoryExceptionResult.INVALID_PARAMETER);

        verify(categoryRepository, never()).findById(anyInt());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("카테고리 삭제 - 존재하지 않는 카테고리를 삭제 시 예외 발생")
    void delCategory_ShouldThrowException_WhenCategoryDoesNotExist() {
        // given
        int categoryId = 999;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryDelService.delCategory(categoryId))
                .isInstanceOf(CategoryException.class)
                .hasFieldOrPropertyWithValue("categoryExceptionResult", CategoryExceptionResult.NOT_EXISTS);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("카테고리 삭제 - 해당 카테고리의 게시글을 임시 카테고리로 이동")
    void delCategory_ShouldMovePostsToTempCategory_WhenCategoryHasPosts() {
        // given
        int categoryId = 1;
        Category category = mock(Category.class);
        Category tempCategory = mock(Category.class);
        Post post1 = mock(Post.class);
        Post post2 = mock(Post.class);
        List<Post> postList = List.of(post1, post2);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(category.getPostList()).thenReturn(postList);
        when(categoryRepository.findByName("임시")).thenReturn(Optional.of(tempCategory));

        // when
        categoryDelService.delCategory(categoryId);

        // then
        verify(category, times(1)).getPostList();
        verify(post1, times(1)).assignCategory(tempCategory);
        verify(post2, times(1)).assignCategory(tempCategory);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("카테고리 삭제 - 임시 카테고리가 없으면 생성 후 이동")
    void delCategory_ShouldCreateTempCategory_WhenTempCategoryDoesNotExist() {
        // given
        int categoryId = 1;
        Category category = mock(Category.class);
        Category tempCategory = mock(Category.class);
        Post post1 = mock(Post.class);
        List<Post> postList = List.of(post1);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(category.getPostList()).thenReturn(postList);
        when(categoryRepository.findByName("임시")).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenReturn(tempCategory);

        // when
        categoryDelService.delCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(post1, times(1)).assignCategory(tempCategory);
        verify(categoryRepository, times(1)).delete(category);
    }
}