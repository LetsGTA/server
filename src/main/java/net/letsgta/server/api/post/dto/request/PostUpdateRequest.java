package net.letsgta.server.api.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostUpdateRequest(

        @NotNull(message = "게시물식별번호는 필수입니다.")
        long postId,

        @Size(max = 50, message = "게시글 제목은 최대 50자 입니다.")
        @NotBlank(message = "제목은 공백일 수 없습니다.")
        String title,

        @Size(max = 2000, message = "게시글의 내용은 최대 2000자까지 가능합니다.")
        @NotBlank(message = "내용은 공백일 수 없습니다.")
        String content
) {
}
