package net.letsgta.server.api.comment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.letsgta.server.api.comment.application.CommentCreateService;
import net.letsgta.server.api.comment.application.CommentDelService;
import net.letsgta.server.api.comment.application.CommentUpdateService;
import net.letsgta.server.api.comment.dto.request.CommentCreateRequest;
import net.letsgta.server.api.comment.dto.request.CommentUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = CommentController.class)
@ContextConfiguration(classes = {CommentController.class, CommentControllerTest.MockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentCreateService commentCreateService;

    @Autowired
    private CommentUpdateService commentUpdateService;

    @Autowired
    private CommentDelService commentDelService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CommentCreateService commentCreateService() {
            return Mockito.mock(CommentCreateService.class);
        }

        @Bean
        public CommentUpdateService commentUpdateService() {
            return Mockito.mock(CommentUpdateService.class);
        }

        @Bean
        public CommentDelService commentDelService() {
            return Mockito.mock(CommentDelService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    @DisplayName("댓글 생성 - 성공")
    @WithMockUser
    void testCreateComment_Success() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, "Test Comment");
        doNothing().when(commentCreateService).createComment(request);

        // when & then
        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(commentCreateService).createComment(request);
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    @WithMockUser
    void testUpdateComment_Success() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(1L, "Updated Comment");
        doNothing().when(commentUpdateService).updateComment(request);

        // when & then
        mockMvc.perform(put("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(commentUpdateService).updateComment(request);
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    @WithMockUser
    void testDeleteComment_Success() throws Exception {
        // given
        Long commentId = 1L;
        doNothing().when(commentDelService).deleteComment(commentId);

        // when & then
        mockMvc.perform(delete("/api/v1/comment/{commentId}", commentId))
                .andExpect(status().isOk());
        verify(commentDelService).deleteComment(commentId);
    }
}
