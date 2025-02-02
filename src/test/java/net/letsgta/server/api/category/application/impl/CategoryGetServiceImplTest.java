package net.letsgta.server.api.category.application.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.letsgta.server.api.category.dto.response.CategoryGetResponse;
import net.letsgta.server.api.category.entity.Category;
import net.letsgta.server.api.category.exception.CategoryException;
import net.letsgta.server.api.category.exception.CategoryExceptionResult;
import net.letsgta.server.api.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryGetServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryGetServiceImpl categoryGetService;

    private Category createCategory(int categoryId, String name, LocalDateTime createdAt, List<Category> subCategories) {
        return Category.builder()
                .categoryId(categoryId)
                .name(name)
                .createdAt(createdAt)
                .subCategories(subCategories)
                .build();
    }

    private void assertCategoryGetResponse(CategoryGetResponse response, int expectedCategoryId, String expectedName,
                                           LocalDateTime expectedCreatedAt) {
        assertNotNull(response, "응답 객체는 null 이 아니어야 합니다.");
        assertEquals(expectedCategoryId, response.categoryId(), "카테고리 ID가 일치하지 않습니다.");
        assertEquals(expectedName, response.name(), "카테고리 이름이 일치하지 않습니다.");
        assertEquals(expectedCreatedAt, response.createdAt(), "생성일자가 일치하지 않습니다.");
    }

    @DisplayName("카테고리 조회 - 정상 케이스")
    @Test
    void testGetCategory_Success() {
        // given
        int categoryId = 1;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        Category category = createCategory(categoryId, "Test Category", createdAt, Collections.emptyList());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        CategoryGetResponse response = categoryGetService.getCategory(categoryId);

        // then
        assertCategoryGetResponse(response, categoryId, "Test Category", createdAt);
        assertNotNull(response.subCategories(), "하위 카테고리 리스트는 null 이 아니어야 합니다.");
        assertTrue(response.subCategories().isEmpty(), "하위 카테고리 리스트는 비어있어야 합니다.");
    }

    @DisplayName("카테고리 삭제 - 유효하지 않은 카테고리 ID 입력 시 예외 발생")
    @Test
    void testGetCategory_InvalidParameter() {
        // given
        int negativeId = -1;

        // when & then
        CategoryException exception = assertThrows(CategoryException.class, () -> categoryGetService.getCategory(negativeId));
        assertEquals(CategoryExceptionResult.INVALID_PARAMETER, exception.getCategoryExceptionResult());
    }

    @DisplayName("카테고리 조회 - 존재하지 않는 카테고리 ID 입력 시 예외 발생")
    @Test
    void testGetCategory_NotExists() {
        // given
        int categoryId = 100;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        CategoryException exception = assertThrows(CategoryException.class, () -> categoryGetService.getCategory(categoryId));
        assertEquals(CategoryExceptionResult.NOT_EXISTS, exception.getCategoryExceptionResult());
    }

    @DisplayName("전체 카테고리 조회 - 정상 케이스")
    @Test
    void testGetAllCategories() {
        // given
        LocalDateTime parentCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime subCreatedAt = LocalDateTime.of(2023, 1, 2, 0, 0);

        Category subCategory = createCategory(2, "Sub Category", subCreatedAt, Collections.emptyList());
        Category parentCategory = createCategory(1, "Parent Category", parentCreatedAt, List.of(subCategory));

        when(categoryRepository.findAll()).thenReturn(List.of(parentCategory));

        // when
        List<CategoryGetResponse> responses = categoryGetService.getAllCategories();

        // then
        assertNotNull(responses, "전체 카테고리 리스트는 null 이 아니어야 합니다.");
        assertEquals(1, responses.size(), "카테고리 개수가 일치하지 않습니다.");

        CategoryGetResponse parentResponse = responses.get(0);
        assertCategoryGetResponse(parentResponse, 1, "Parent Category", parentCreatedAt);

        List<CategoryGetResponse> subResponses = parentResponse.subCategories();
        assertNotNull(subResponses, "하위 카테고리 리스트는 null 이 아니어야 합니다.");
        assertEquals(1, subResponses.size(), "하위 카테고리 개수가 일치하지 않습니다.");

        CategoryGetResponse subResponse = subResponses.get(0);
        assertCategoryGetResponse(subResponse, 2, "Sub Category", subCreatedAt);
        assertNotNull(subResponse.subCategories(), "서브 카테고리의 하위 리스트는 null 이 아니어야 합니다.");
        assertTrue(subResponse.subCategories().isEmpty(), "서브 카테고리의 하위 리스트는 비어있어야 합니다.");
    }
}
