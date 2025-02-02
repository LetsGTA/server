package net.letsgta.server.api.comment.application.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.letsgta.server.api.comment.dto.request.CommentCreateRequest;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.repository.CommentRepository;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentCreateServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentCreateServiceImpl commentCreateService;

    private final String currentUsername = "user@example.com";
    private User user;
    private Post post;
    private Comment parentComment;

    @BeforeEach
    void setUp() {
        // given
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(currentUsername, null));

        user = User.builder()
                .email(currentUsername)
                .build();

        post = Post.builder()
                .postId(1)
                .build();

        parentComment = Comment.builder()
                .commentId(2)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 생성 - 성공 (부모 없음)")
    void testCreateCommentWithoutParent() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, "Test comment");
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(postRepository.findById(request.postId())).thenReturn(Optional.of(post));

        // when & then
        commentCreateService.createComment(request);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 - 성공 (부모 있음)")
    void testCreateCommentWithParent() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, 2L, "Test comment with parent");
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(postRepository.findById(request.postId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(request.parentId())).thenReturn(Optional.of(parentComment));

        // when & then
        commentCreateService.createComment(request);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 - 실패 (사용자 미존재)")
    void testCreateComment_UserNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, "Test comment");
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserException.class, () -> commentCreateService.createComment(request));
    }

    @Test
    @DisplayName("댓글 생성 - 실패 (게시글 미존재)")
    void testCreateComment_PostNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, null, "Test comment");
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(postRepository.findById(request.postId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostException.class, () -> commentCreateService.createComment(request));
    }

    @Test
    @DisplayName("댓글 생성 - 실패 (부모 댓글 미존재)")
    void testCreateComment_ParentNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(1L, 2L, "Test comment with invalid parent");
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(postRepository.findById(request.postId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(request.parentId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentException.class, () -> commentCreateService.createComment(request));
    }
}
