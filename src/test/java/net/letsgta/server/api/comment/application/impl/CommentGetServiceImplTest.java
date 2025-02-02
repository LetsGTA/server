package net.letsgta.server.api.comment.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import net.letsgta.server.api.comment.dto.response.CommentGetResponse;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.repository.CommentRepository;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentGetServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentGetServiceImpl commentGetService;

    private User dummyUser;
    private Post dummyPost;
    private Comment parentComment;
    private Comment childComment;

    @BeforeEach
    void setUp() {
        dummyUser = User.builder()
                .userId(1L)
                .email("user@example.com")
                .build();

        dummyPost = Post.builder()
                .postId(100L)
                .build();

        parentComment = Comment.builder()
                .commentId(10L)
                .content("Parent comment")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .user(dummyUser)
                .post(dummyPost)
                .parent(null)
                .commentList(List.of())
                .build();

        childComment = Comment.builder()
                .commentId(1L)
                .content("Child comment")
                .createdAt(LocalDateTime.of(2023, 1, 1, 1, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 1, 1, 0))
                .user(dummyUser)
                .post(dummyPost)
                .parent(parentComment)
                .commentList(List.of())
                .build();
    }

    @Test
    @DisplayName("댓글 조회 - 성공")
    void testGetComment_Success() {
        // given
        when(commentRepository.findById(1L)).thenReturn(Optional.of(childComment));

        // when & then
        CommentGetResponse response = commentGetService.getComment(1L);

        assertNotNull(response);
        assertEquals(childComment.getCommentId(), response.commentId());
        assertEquals(childComment.getContent(), response.content());
        assertEquals(dummyUser.getUserId(), response.userId());
        assertEquals(dummyUser.getEmail(), response.email());
        assertEquals(dummyPost.getPostId(), response.postId());
        assertEquals(parentComment.getCommentId(), response.parentId());
        assertNotNull(response.replies());
        assertTrue(response.replies().isEmpty());
    }

    @Test
    @DisplayName("댓글 조회 - 댓글 없음 (예외 발생)")
    void testGetComment_NotFound() {
        // given
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentException.class, () -> commentGetService.getComment(1L));
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록 조회 - 성공")
    void testGetCommentsByPostId_Success() {
        // given
        when(commentRepository.findAllByPostPostId(100L)).thenReturn(List.of(childComment));

        // when & then
        List<CommentGetResponse> responses = commentGetService.getCommentsByPostId(100L);
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(childComment.getCommentId(), responses.get(0).commentId());
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록 조회 - 빈 리스트")
    void testGetCommentsByPostId_Empty() {
        // given
        when(commentRepository.findAllByPostPostId(100L)).thenReturn(List.of());

        // when & then
        List<CommentGetResponse> responses = commentGetService.getCommentsByPostId(100L);
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("이메일로 댓글 목록 조회 - 성공")
    void testGetCommentsByEmail_Success() {
        // given
        when(commentRepository.findAllByUserEmail("user@example.com")).thenReturn(List.of(childComment));

        // when & then
        List<CommentGetResponse> responses = commentGetService.getCommentsByEmail("user@example.com");
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(childComment.getCommentId(), responses.get(0).commentId());
    }

    @Test
    @DisplayName("이메일로 댓글 목록 조회 - 빈 리스트")
    void testGetCommentsByEmail_Empty() {
        // given
        when(commentRepository.findAllByUserEmail("nonexistent@example.com")).thenReturn(List.of());

        // when & then
        List<CommentGetResponse> responses = commentGetService.getCommentsByEmail("nonexistent@example.com");
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}
