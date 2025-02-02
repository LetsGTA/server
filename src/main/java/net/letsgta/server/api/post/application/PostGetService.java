package net.letsgta.server.api.post.application;

import net.letsgta.server.api.post.dto.response.PostGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostGetService {

    Page<PostGetResponse> getPosts(Pageable pageable);
}
