package net.letsgta.server.api.post.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.post.application.PostDelService;
import net.letsgta.server.api.post.dto.request.PostDeleteRequest;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.exception.PostExceptionResult;
import net.letsgta.server.api.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDelServiceImpl implements PostDelService {

    private final PostRepository postRepository;

    @Transactional
    @Override
    public void deletePost(PostDeleteRequest request) {
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new PostException(PostExceptionResult.NOT_EXISTS));

        post.delete();
    }
}
