package net.letsgta.server.api.comment.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.comment.application.CommentGetService;
import net.letsgta.server.api.comment.dto.response.CommentGetResponse;
import net.letsgta.server.api.common.response.entity.ApiResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentReadController {

    private final CommentGetService commentGetService;

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponseEntity<CommentGetResponse>> getComment(@PathVariable Long commentId) {
        return ApiResponseEntity.successResponseEntity(commentGetService.getComment(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponseEntity<List<CommentGetResponse>>> getCommentByPost(@PathVariable Long postId) {
        return ApiResponseEntity.successResponseEntity(commentGetService.getCommentsByPostId(postId));
    }

    // TODO : security 추가 설정 필요
    @GetMapping("/user/{email}")
    public ResponseEntity<ApiResponseEntity<List<CommentGetResponse>>> getCommentByUser(@PathVariable String email) {
        return ApiResponseEntity.successResponseEntity(commentGetService.getCommentsByEmail(email));
    }
}
