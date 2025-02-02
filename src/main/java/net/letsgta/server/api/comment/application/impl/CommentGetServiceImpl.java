package net.letsgta.server.api.comment.application.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.comment.application.CommentGetService;
import net.letsgta.server.api.comment.dto.response.CommentGetResponse;
import net.letsgta.server.api.comment.entity.Comment;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.api.comment.exception.CommentExceptionResult;
import net.letsgta.server.api.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentGetServiceImpl implements CommentGetService {

    private final CommentRepository commentRepository;

    @Override
    public CommentGetResponse getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionResult.NOT_EXISTS));

        return CommentGetResponse.from(comment);
    }

    @Override
    public List<CommentGetResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostPostId(postId);

        return comments.stream()
                .map(CommentGetResponse::from)
                .toList();
    }

    @Override
    public List<CommentGetResponse> getCommentsByEmail(String email) {
        List<Comment> comments = commentRepository.findAllByUserEmail(email);

        return comments.stream()
                .map(CommentGetResponse::from)
                .toList();
    }
}
