package net.letsgta.server.api.comment.repository;

import java.util.List;
import net.letsgta.server.api.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostPostId(Long postId);
    List<Comment> findAllByUserEmail(String email);
}
