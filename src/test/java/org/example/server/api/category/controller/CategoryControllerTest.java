package org.example.server.api.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.api.category.application.CategoryCreateService;
import org.example.server.api.category.application.CategoryDelService;
import org.example.server.api.category.dto.request.CategoryCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = CategoryController.class)
@ContextConfiguration(classes = {CategoryController.class, CategoryControllerTest.MockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryCreateService categoryCreateService;

    @Autowired
    private CategoryDelService categoryDelService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CategoryCreateService categoryCreateService() {
            return Mockito.mock(CategoryCreateService.class);
        }

        @Bean
        public CategoryDelService categoryDelService() {
            return Mockito.mock(CategoryDelService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    @DisplayName("카테고리 생성 - 성공")
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory() throws Exception {
        // given
        CategoryCreateRequest request = new CategoryCreateRequest("Test Category", 1);
        doNothing().when(categoryCreateService).createCategory(any(CategoryCreateRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(categoryCreateService).createCategory(any(CategoryCreateRequest.class));
    }

    @DisplayName("카테고리 생성 - 실패")
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testCreateCategory_ValidationError() throws Exception {
        // given
        CategoryCreateRequest invalidRequest = new CategoryCreateRequest("", 1);

        // when & then
        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory() throws Exception {
        // given
        int categoryId = 1;
        doNothing().when(categoryDelService).delCategory(anyInt());

        // when & then
        mockMvc.perform(delete("/api/v1/category/{categoryId}", categoryId))
                .andExpect(status().isOk());

        verify(categoryDelService).delCategory(categoryId);
    }
}
