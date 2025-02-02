package net.letsgta.server.api.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateRequest(

        @NotNull(message = "댓글식별번호는 비어있을 수 없습니다.")
        Long commentId,

        @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
        String content
) {
}
