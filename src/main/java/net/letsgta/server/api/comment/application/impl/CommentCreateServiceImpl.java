package net.letsgta.server.api.comment.application.impl;


import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.comment.application.CommentCreateService;
import net.letsgta.server.api.comment.dto.request.CommentCreateRequest;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.exception.CommentExceptionResult;
import net.letsgta.server.api.comment.repository.CommentRepository;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.exception.PostExceptionResult;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import net.letsgta.server.api.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCreateServiceImpl implements CommentCreateService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public void createComment(CommentCreateRequest request) {
        long currentUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new UserException(UserExceptionResult.NOT_EXISTS));

        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new PostException(PostExceptionResult.NOT_EXISTS));

        Comment parentComment = null;
        if (request.parentId() != null) {
            parentComment = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new CommentException(CommentExceptionResult.PARENT_COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parentComment)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }
}
