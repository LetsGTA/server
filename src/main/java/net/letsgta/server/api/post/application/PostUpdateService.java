package net.letsgta.server.api.post.application;

import net.letsgta.server.api.post.dto.request.PostUpdateRequest;

public interface PostUpdateService {
    void updatePost(PostUpdateRequest request);
}
