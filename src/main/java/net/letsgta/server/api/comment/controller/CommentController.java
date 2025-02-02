package net.letsgta.server.api.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.comment.application.CommentCreateService;
import net.letsgta.server.api.comment.application.CommentDelService;
import net.letsgta.server.api.comment.application.CommentUpdateService;
import net.letsgta.server.api.comment.dto.request.CommentCreateRequest;
import net.letsgta.server.api.comment.dto.request.CommentUpdateRequest;
import net.letsgta.server.api.common.response.entity.ApiResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentCreateService commentCreateService;
    private final CommentUpdateService commentUpdateService;
    private final CommentDelService commentDelService;

    @PostMapping
    public ResponseEntity<ApiResponseEntity<Void>> create(@Valid @RequestBody CommentCreateRequest request) {
        commentCreateService.createComment(request);
        return ApiResponseEntity.successResponseEntity();
    }

    @PutMapping
    public ResponseEntity<ApiResponseEntity<Void>> update(@Valid @RequestBody CommentUpdateRequest request) {
        commentUpdateService.updateComment(request);
        return ApiResponseEntity.successResponseEntity();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponseEntity<Void>> delete(@PathVariable Long commentId) {
        commentDelService.deleteComment(commentId);
        return ApiResponseEntity.successResponseEntity();

    }
}
