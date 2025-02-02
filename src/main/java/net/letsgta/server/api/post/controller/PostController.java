package net.letsgta.server.api.post.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.common.response.entity.ApiResponseEntity;
import net.letsgta.server.api.post.application.PostCreateService;
import net.letsgta.server.api.post.application.PostDelService;
import net.letsgta.server.api.post.application.PostUpdateService;
import net.letsgta.server.api.post.dto.request.PostCreateRequest;
import net.letsgta.server.api.post.dto.request.PostDeleteRequest;
import net.letsgta.server.api.post.dto.request.PostUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostCreateService postCreateService;
    private final PostUpdateService postUpdateService;
    private final PostDelService postDelService;

    @PostMapping
    public ResponseEntity<ApiResponseEntity<Object>> create(@Valid @RequestBody PostCreateRequest request) {
        postCreateService.createPost(request);
        return ApiResponseEntity.successResponseEntity();
    }

    @PutMapping
    public ResponseEntity<ApiResponseEntity<Object>> update(@Valid @RequestBody PostUpdateRequest request) {
        postUpdateService.updatePost(request);
        return ApiResponseEntity.successResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseEntity<Object>> delete(@Valid @RequestBody PostDeleteRequest request) {
        postDelService.deletePost(request);
        return ApiResponseEntity.successResponseEntity();
    }
}
