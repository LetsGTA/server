package net.letsgta.server.api.comment.application;

import net.letsgta.server.api.comment.dto.request.CommentUpdateRequest;

public interface CommentUpdateService {

    void updateComment(CommentUpdateRequest request);
}
