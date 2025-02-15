package net.letsgta.server.api.post.controller;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.common.response.entity.ApiResponseEntity;
import net.letsgta.server.api.post.application.PostGetService;
import net.letsgta.server.api.post.dto.response.PostGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostReadController {

    private final PostGetService postGetService;

    @GetMapping
    public ResponseEntity<ApiResponseEntity<Page<PostGetResponse>>> getPosts(Pageable pageable) {
        return ApiResponseEntity.successResponseEntity(postGetService.getPosts(pageable));
    }
}
