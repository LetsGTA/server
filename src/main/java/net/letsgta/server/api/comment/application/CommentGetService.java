package net.letsgta.server.api.comment.application;

import java.util.List;
import net.letsgta.server.api.comment.dto.response.CommentGetResponse;

public interface CommentGetService {

    CommentGetResponse getComment(Long commentId);
    List<CommentGetResponse> getCommentsByPostId(Long postId);
    List<CommentGetResponse> getCommentsByEmail(String email);
}
