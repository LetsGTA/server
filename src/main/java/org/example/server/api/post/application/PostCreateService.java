package org.example.server.api.post.application;

import org.example.server.api.post.dto.request.PostCreateRequest;

public interface PostCreateService {
    void createPost(PostCreateRequest request);
}
