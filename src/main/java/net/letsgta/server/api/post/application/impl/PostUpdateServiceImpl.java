package net.letsgta.server.api.post.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.post.application.PostUpdateService;
import net.letsgta.server.api.post.dto.request.PostUpdateRequest;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.api.post.exception.PostExceptionResult;
import net.letsgta.server.api.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateServiceImpl implements PostUpdateService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void updatePost(PostUpdateRequest request) {
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new PostException(PostExceptionResult.NOT_EXISTS));

        // 게시글 내용 업데이트
        post.update(request.title(), request.content());
    }
}
