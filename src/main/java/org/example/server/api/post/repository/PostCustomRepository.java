package org.example.server.api.post.repository;

import org.example.server.api.post.dto.response.PostGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
    Page<PostGetResponse> findActivePosts(Pageable pageable);
}
