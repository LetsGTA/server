package net.letsgta.server.api.post.application;

import net.letsgta.server.api.post.dto.request.PostCreateRequest;

public interface PostCreateService {
    void createPost(PostCreateRequest request);
}
