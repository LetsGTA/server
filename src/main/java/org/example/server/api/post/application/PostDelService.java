package org.example.server.api.post.application;

import org.example.server.api.post.dto.request.PostDeleteRequest;

public interface PostDelService {

    void deletePost(PostDeleteRequest request);
}
