package net.letsgta.server.api.post.repository;

import net.letsgta.server.api.post.dto.response.PostGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
    Page<PostGetResponse> findActivePosts(Pageable pageable);
}
