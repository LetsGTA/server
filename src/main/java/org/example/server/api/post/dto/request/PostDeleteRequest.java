package org.example.server.api.post.dto.request;

import jakarta.validation.constraints.NotNull;

public record PostDeleteRequest(

        @NotNull(message = "게시글식별번호는 필수입니다.")
        long postId
) {
}
