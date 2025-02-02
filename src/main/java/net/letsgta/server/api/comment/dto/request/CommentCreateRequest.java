package net.letsgta.server.api.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull(message = "게시글 ID는 필수입니다.")
        Long postId,

        Long parentId,

        // TODO : 댓글 최대 길이 논의
        @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
        String content
) {
}
