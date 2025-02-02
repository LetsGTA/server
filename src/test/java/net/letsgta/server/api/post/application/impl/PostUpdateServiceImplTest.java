package net.letsgta.server.api.post.application.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import net.letsgta.server.api.post.application.PostUpdateService;
import net.letsgta.server.api.post.dto.request.PostUpdateRequest;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.exception.PostExceptionResult;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(PostUpdateServiceImpl.class)
class PostUpdateServiceImplTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostUpdateService postUpdateService;

    @Autowired
    private UserRepository userRepository;

    private Post savedPost;

    @BeforeEach
    void setUp() {
        // 테스트용 유저
        User testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("Test")
                .role(RoleName.ROLE_USER)
                .build();
        userRepository.save(testUser);

        // 테스트용 게시글 저장
        savedPost = postRepository.save(Post.builder()
                .title("Original Title")
                .content("Original Content")
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build());
    }

    @Test
    @Transactional
    @DisplayName("게시글 업데이트 - 성공 케이스")
    void updatePost_ShouldUpdatePost_WhenPostExists() {
        // given
        PostUpdateRequest request = new PostUpdateRequest(savedPost.getPostId(), "Updated Title", "Updated Content");

        // when
        postUpdateService.updatePost(request);

        // then
        Post updatedPost = postRepository.findById(savedPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent()).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("게시글 업데이트 - 실패 케이스 (게시글 존재하지 않음)")
    void updatePost_ShouldThrowException_WhenPostDoesNotExist() {
        // given
        PostUpdateRequest request = new PostUpdateRequest(999L, "Updated Title", "Updated Content");

        // when
        PostException exception = assertThrows(PostException.class, () -> postUpdateService.updatePost(request));

        // then
        assertEquals(PostExceptionResult.NOT_EXISTS, exception.getPostExceptionResult());
    }
}
