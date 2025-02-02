package net.letsgta.server.api.comment.dto.response;

import java.util.List;
import lombok.Builder;
import net.letsgta.server.api.comment.entity.Comment;

@Builder
public record CommentGetResponse(

        Long commentId,
        String content,
        String createdAt,
        String updatedAt,
        Long userId,
        String email,
        Long postId,
        List<CommentGetResponse> replies
) {
    public static CommentGetResponse from(Comment comment) {
        return CommentGetResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .updatedAt(comment.getUpdatedAt().toString())
                .userId(comment.getUser().getUserId())
                .email(comment.getUser().getEmail())
                .postId(comment.getPost().getPostId())
                .replies(comment.getCommentList().stream().map(CommentGetResponse::from).toList())
                .build();
    }
}
