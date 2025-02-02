package net.letsgta.server.api.post.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.letsgta.server.api.post.application.PostCreateService;
import net.letsgta.server.api.post.application.PostDelService;
import net.letsgta.server.api.post.application.PostUpdateService;
import net.letsgta.server.api.post.dto.request.PostCreateRequest;
import net.letsgta.server.api.post.dto.request.PostDeleteRequest;
import net.letsgta.server.api.post.dto.request.PostUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
class PostControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PostCreateService postCreateService;
    private PostUpdateService postUpdateService;
    private PostDelService postDelService;

    @BeforeEach
    void setUp() {
        postCreateService = Mockito.mock(PostCreateService.class);
        postUpdateService = Mockito.mock(PostUpdateService.class);
        postDelService = Mockito.mock(PostDelService.class);

        PostController postController = new PostController(postCreateService, postUpdateService, postDelService);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createPost_Success() throws Exception {
        // given
        PostCreateRequest request = new PostCreateRequest(1L, "Test Title", "Test Content");

        doNothing().when(postCreateService).createPost(request);

        // when & then
        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(postCreateService).createPost(request);
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost_Success() throws Exception {
        // given
        PostUpdateRequest request = new PostUpdateRequest(1L, "Updated Title", "Updated Content");

        doNothing().when(postUpdateService).updatePost(request);

        // when & then
        mockMvc.perform(put("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(postUpdateService).updatePost(request);
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success() throws Exception {
        // given
        PostDeleteRequest request = new PostDeleteRequest(1L);

        doNothing().when(postDelService).deletePost(request);

        // when & then
        mockMvc.perform(delete("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(postDelService).deletePost(request);
    }
}
