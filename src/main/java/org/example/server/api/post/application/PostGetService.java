package org.example.server.api.post.application;

import org.example.server.api.post.dto.response.PostGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostGetService {

    Page<PostGetResponse> getPosts(Pageable pageable);
}
