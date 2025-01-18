package org.example.server.api.post.application;

import org.example.server.api.post.dto.request.PostUpdateRequest;

public interface PostUpdateService {
    void updatePost(PostUpdateRequest request);
}
