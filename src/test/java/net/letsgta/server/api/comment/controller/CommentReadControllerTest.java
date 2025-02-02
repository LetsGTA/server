package net.letsgta.server.api.comment.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import net.letsgta.server.api.comment.application.CommentGetService;
import net.letsgta.server.api.comment.dto.response.CommentGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@ActiveProfiles("test")
@WebMvcTest(controllers = CommentReadController.class)
@ContextConfiguration(classes = {CommentReadController.class, CommentReadControllerTest.MockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class CommentReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentGetService commentGetService;

    private static final String TEST_CONTENT = "Test Comment";
    private static final String TEST_CREATED_AT = "2023-01-01T00:00:00";
    private static final String TEST_UPDATED_AT = "2023-01-01T00:00:00";
    private static final Long TEST_USER_ID = 100L;
    private static final Long TEST_POST_ID = 200L;
    private static final Long DEFAULT_PARENT_ID = 0L;
    private static final String TEST_EMAIL = "user@example.com";

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CommentGetService commentGetService() {
            return Mockito.mock(CommentGetService.class);
        }
    }

    private CommentGetResponse createDummyComment(Long commentId, Long postId, String email) {
        return CommentGetResponse.builder()
                .commentId(commentId)
                .content(TEST_CONTENT)
                .createdAt(TEST_CREATED_AT)
                .updatedAt(TEST_UPDATED_AT)
                .userId(TEST_USER_ID)
                .email(email)
                .postId(postId)
                .parentId(DEFAULT_PARENT_ID)
                .replies(List.of())
                .build();
    }

    @Test
    @DisplayName("댓글 단건 조회 - 성공")
    void testGetComment_Success() throws Exception {
        // given
        Long commentId = 1L;
        CommentGetResponse dummyResponse = createDummyComment(commentId, TEST_POST_ID, TEST_EMAIL);
        when(commentGetService.getComment(commentId)).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(get("/api/v1/comment/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(commentId))
                .andExpect(jsonPath("$.data.content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.data.createdAt").value(TEST_CREATED_AT))
                .andExpect(jsonPath("$.data.updatedAt").value(TEST_UPDATED_AT))
                .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.postId").value(TEST_POST_ID))
                .andExpect(jsonPath("$.data.parentId").value(DEFAULT_PARENT_ID))
                .andExpect(jsonPath("$.data.replies").isArray())
                .andExpect(jsonPath("$.data.replies").isEmpty());
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록 조회 - 성공")
    void testGetCommentsByPost_Success() throws Exception {
        // given
        Long postId = TEST_POST_ID;
        CommentGetResponse dummyResponse = createDummyComment(1L, postId, TEST_EMAIL);
        when(commentGetService.getCommentsByPostId(postId)).thenReturn(List.of(dummyResponse));

        // when & then
        mockMvc.perform(get("/api/v1/comment/post/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].commentId").value(dummyResponse.commentId()))
                .andExpect(jsonPath("$.data[0].content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.data[0].postId").value(postId));
    }

    @Test
    @DisplayName("이메일로 댓글 목록 조회 - 성공")
    void testGetCommentsByUser_Success() throws Exception {
        // given
        String email = TEST_EMAIL;
        CommentGetResponse dummyResponse = createDummyComment(1L, TEST_POST_ID, email);
        when(commentGetService.getCommentsByEmail(email)).thenReturn(List.of(dummyResponse));

        // when & then
        mockMvc.perform(get("/api/v1/comment/user/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].commentId").value(dummyResponse.commentId()))
                .andExpect(jsonPath("$.data[0].email").value(email));
    }
}
