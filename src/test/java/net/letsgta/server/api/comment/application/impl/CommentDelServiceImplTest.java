package net.letsgta.server.api.comment.application.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.repository.CommentRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.enums.RoleName;
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
class CommentDelServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentDelServiceImpl commentDelService;

    private final String currentUsername = "user@example.com";
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // given
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(currentUsername, null));

        user = User.builder()
                .userId(1L)
                .email(currentUsername)
                .build();

        comment = Mockito.spy(
                Comment.builder()
                        .commentId(1L)
                        .user(user)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void testDeleteComment_Success() {
        // given
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(comment).delete();

        // when & then
        commentDelService.deleteComment(1L);
        verify(comment).delete();
    }

    @Test
    @DisplayName("댓글 삭제 - 성공 (관리자 권한)")
    void testDeleteComment_AdminSuccess() {
        // given
        User adminUser = User.builder()
                .userId(1L)
                .email(currentUsername)
                .role(RoleName.ROLE_ADMIN)
                .build();

        User otherUser = User.builder()
                .userId(2L)
                .email("other@example.com")
                .build();

        Comment commentWithOtherUser = Mockito.spy(
                Comment.builder()
                        .commentId(1L)
                        .user(otherUser)
                        .build()
        );

        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(adminUser));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentWithOtherUser));
        doNothing().when(commentWithOtherUser).delete();

        // when & then
        commentDelService.deleteComment(1L);
        verify(commentWithOtherUser).delete();
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (사용자 미존재)")
    void testDeleteComment_UserNotFound() {
        // given
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserException.class, () -> commentDelService.deleteComment(1L));
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (댓글 미존재)")
    void testDeleteComment_CommentNotFound() {
        // given
        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommentException.class, () -> commentDelService.deleteComment(1L));
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (잘못된 접근)")
    void testDeleteComment_InvalidAccess() {
        // given
        User otherUser = User.builder()
                .userId(2L)
                .email("other@example.com")
                .build();

        Comment commentWithOtherUser = Mockito.spy(
                Comment.builder()
                        .commentId(1L)
                        .user(otherUser)
                        .build()
        );

        when(userRepository.findByEmail(currentUsername)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentWithOtherUser));

        // when & then
        assertThrows(CommentException.class, () -> commentDelService.deleteComment(1L));
    }
}
