package net.letsgta.server.api.comment.application.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import net.letsgta.server.api.comment.dto.request.CommentUpdateRequest;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.repository.CommentRepository;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentUpdateServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentUpdateServiceImpl commentUpdateService;

    private final String currentUsername = "user@example.com";
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(currentUsername, null));

        user = User.builder()
                .userId(1L)
                .email(currentUsername)
                .build();

        comment = Mockito.spy(
                Comment.builder()
                        .commentId(1L)
                        .content("old comment")
                        .user(user)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void testUpdateComment_Success() {
        // given
        Long commentId = 1L;
        String newContent = "new comment";
        CommentUpdateRequest request = new CommentUpdateRequest(commentId, newContent);
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when & then
        commentUpdateService.updateComment(request);
        Mockito.verify(comment).updateContent(newContent);
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (사용자 미존재)")
    void testUpdateComment_UserNotFound() {
        // given
        Long commentId = 1L;
        String newContent = "new comment";
        CommentUpdateRequest request = new CommentUpdateRequest(commentId, newContent);
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserException.class, () -> commentUpdateService.updateComment(request));
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (댓글 미존재)")
    void testUpdateComment_CommentNotFound() {
        // given
        Long commentId = 1L;
        String newContent = "new comment";
        CommentUpdateRequest request = new CommentUpdateRequest(commentId, newContent);
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentException.class, () -> commentUpdateService.updateComment(request));
    }

    @Test
    @DisplayName("댓글 수정 - 실패 (잘못된 접근)")
    void testUpdateComment_InvalidAccess() {
        // given
        Long commentId = 1L;
        String newContent = "new comment";
        CommentUpdateRequest request = new CommentUpdateRequest(commentId, newContent);
        User differentUser = User.builder()
                .userId(2L)
                .email("other@example.com")
                .build();

        Comment commentWithOtherUser = Mockito.spy(
                Comment.builder()
                        .commentId(commentId)
                        .content("old comment")
                        .user(differentUser)
                        .build()
        );

        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentWithOtherUser));

        // when & then
        assertThrows(CommentException.class, () -> commentUpdateService.updateComment(request));
    }
}
