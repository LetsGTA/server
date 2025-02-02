package net.letsgta.server.api.comment.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.comment.application.CommentDelService;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.exception.CommentExceptionResult;
import net.letsgta.server.api.comment.repository.CommentRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import net.letsgta.server.api.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentDelServiceImpl implements CommentDelService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        long currentUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new UserException(UserExceptionResult.NOT_EXISTS));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionResult.NOT_EXISTS));

        if (!comment.getUser().equals(user) && !user.getRole().equals(RoleName.ROLE_ADMIN)) {
            throw new CommentException(CommentExceptionResult.INVALID_ACCESS);
        }

        comment.delete();
    }
}
