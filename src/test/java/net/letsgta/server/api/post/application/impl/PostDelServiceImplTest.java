package net.letsgta.server.api.post.application.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.letsgta.server.api.post.application.PostDelService;
import net.letsgta.server.api.post.dto.request.PostDeleteRequest;
import net.letsgta.server.api.post.exception.PostExceptionResult;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class PostDelServiceImplTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostDelService postDelService;

    @Autowired
    private UserRepository userRepository;

    private Post savedPost;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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
                .build());
    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_ShouldMarkPostAsDeleted_WhenPostExists() {
        // given
        PostDeleteRequest request = new PostDeleteRequest(savedPost.getPostId());

        // when
        postDelService.deletePost(request);

        // then
        Post deletedPost = postRepository.findById(savedPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        assertThat(deletedPost.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("게시글 삭제 - 실패 (게시물 존재하지 않음)")
    void deletePost_ShouldThrowException_WhenPostDoesNotExist() {
        // given
        PostDeleteRequest request = new PostDeleteRequest(999L);

        // when
        PostException exception = assertThrows(PostException.class, () -> postDelService.deletePost(request));

        // then
        assertEquals(PostExceptionResult.NOT_EXISTS, exception.getPostExceptionResult());
    }
}
