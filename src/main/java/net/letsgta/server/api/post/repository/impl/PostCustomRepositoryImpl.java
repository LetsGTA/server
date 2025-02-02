package net.letsgta.server.api.post.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.post.dto.response.PostGetResponse;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.entity.QPost;
import net.letsgta.server.api.post.repository.PostCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostGetResponse> findActivePosts(Pageable pageable) {
        QPost post = QPost.post;

        // 게시글 조회
        List<Post> results = queryFactory.selectFrom(post)
                .where(post.isDeleted.eq(false))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이지 총 갯수 계산
        Long total = Optional.ofNullable(
                queryFactory.select(post.count())
                        .from(post)
                        .where(post.isDeleted.eq(false))
                        .fetchOne()
        ).orElse(0L);

        // dto 변환
        List<PostGetResponse> postGetResponseList = results.stream()
                .map(PostGetResponse::from)
                .toList();

        return new PageImpl<>(postGetResponseList, pageable, total);
    }
}
