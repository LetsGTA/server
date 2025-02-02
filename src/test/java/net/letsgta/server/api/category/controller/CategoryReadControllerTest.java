package net.letsgta.server.api.category.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import net.letsgta.server.api.category.application.CategoryGetService;
import net.letsgta.server.api.category.dto.response.CategoryGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(CategoryReadController.class)
@ContextConfiguration(classes = {CategoryReadController.class, CategoryReadControllerTest.MockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class CategoryReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryGetService categoryGetService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CategoryGetService categoryGetService() {
            return Mockito.mock(CategoryGetService.class);
        }
    }

    @Test
    @DisplayName("전체 카테고리 조회")
    @WithAnonymousUser
    void testGetCategories() throws Exception {
        // given
        int categoryId = 1;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        CategoryGetResponse categoryGetResponse = CategoryGetResponse.builder()
                .categoryId(categoryId)
                .name("Test Category")
                .createdAt(createdAt)
                .subCategories(List.of())
                .build();
        List<CategoryGetResponse> responses = List.of(categoryGetResponse);

        when(categoryGetService.getAllCategories()).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].categoryId").value(categoryId))
                .andExpect(jsonPath("$.data[0].name").value("Test Category"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2023-01-01"))
                .andExpect(jsonPath("$.data[0].subCategories").isArray())
                .andExpect(jsonPath("$.data[0].subCategories").isEmpty());
    }

    @Test
    @DisplayName("카테고리 단건 조회")
    @WithAnonymousUser
    void testGetCategory() throws Exception {
        // given
        int categoryId = 1;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        CategoryGetResponse categoryGetResponse = CategoryGetResponse.builder()
                .categoryId(categoryId)
                .name("Test Category")
                .createdAt(createdAt)
                .subCategories(List.of())
                .build();

        when(categoryGetService.getCategory(categoryId)).thenReturn(categoryGetResponse);

        // when & then
        mockMvc.perform(get("/api/v1/category/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId").value(categoryId))
                .andExpect(jsonPath("$.data.name").value("Test Category"))
                .andExpect(jsonPath("$.data.createdAt").value("2023-01-01"))
                .andExpect(jsonPath("$.data.subCategories").isArray())
                .andExpect(jsonPath("$.data.subCategories").isEmpty());
    }
}
