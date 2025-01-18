package org.example.server.api.post.dto.response;

import lombok.Builder;
import org.example.server.api.post.entity.Post;

@Builder
public record PostGetResponse(
        long postId,
        String title,
        String content,
        String created_at,
        String updated_at
) {
    public static PostGetResponse from(Post post) {
        return PostGetResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .created_at(post.getCreatedAt().toString())
                .updated_at(post.getUpdatedAt().toString())
                .build();
    }
}
