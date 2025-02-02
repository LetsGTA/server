package net.letsgta.server.api.post.application;

import net.letsgta.server.api.post.dto.request.PostDeleteRequest;

public interface PostDelService {

    void deletePost(PostDeleteRequest request);
}
