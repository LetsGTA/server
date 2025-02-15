package net.letsgta.server.api.post.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.post.application.PostCreateService;
import net.letsgta.server.api.post.dto.request.PostCreateRequest;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreateServiceImpl implements PostCreateService {

    private final PostRepository postRepository;
    private final UserGetService userGetService;

    // TODO : 카테고리 추가 로직 구현
    @Transactional
    @Override
    public void createPost(PostCreateRequest request) {
        // 유저 존재 여부 확인
        if (!userGetService.isUserExist(request.userId())) {
            throw new UserException(UserExceptionResult.NOT_EXISTS);
        }

        // 유저가 존재할 경우 게시글 저장
        postRepository.save(request.to());
    }
}
