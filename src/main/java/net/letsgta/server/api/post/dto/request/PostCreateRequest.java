package net.letsgta.server.api.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.user.entity.User;

public record PostCreateRequest(

        @NotNull(message = "유저식별번호는 필수입니다.")
        long userId,

        @Size(max = 50, message = "게시글 제목은 최대 50자 입니다.")
        @NotBlank(message = "게시글의 제목은 공백이 불가합니다.")
        String title,

        @Size(max = 65535, message = "게시글의 내용은 최대 65535자까지 가능합니다.")
        @NotBlank(message = "게시글의 내용은 공백이 불가합니다.")
        String content
) {
        public Post to() {
                return Post.builder()
                        .user(User.builder()
                                .userId(userId())
                                .build())
                        .title(title())
                        .content(content())
                        .build();
        }
}
