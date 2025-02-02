package net.letsgta.server.api.comment.application;

import net.letsgta.server.api.comment.dto.request.CommentCreateRequest;

public interface CommentCreateService {

    void createComment(CommentCreateRequest request);
}
