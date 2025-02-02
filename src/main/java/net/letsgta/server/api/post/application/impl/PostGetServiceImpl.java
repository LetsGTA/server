package net.letsgta.server.api.post.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.post.application.PostGetService;
import net.letsgta.server.api.post.dto.response.PostGetResponse;
import net.letsgta.server.api.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostGetServiceImpl implements PostGetService {

    private final PostRepository postRepository;

    @Override
    public Page<PostGetResponse> getPosts(Pageable pageable) {
        return postRepository.findActivePosts(pageable);
    }
}
