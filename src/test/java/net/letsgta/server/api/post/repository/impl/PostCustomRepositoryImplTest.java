package net.letsgta.server.api.post.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import net.letsgta.server.api.post.dto.response.PostGetResponse;
import net.letsgta.server.api.post.entity.Post;
import net.letsgta.server.api.post.repository.PostCustomRepository;
import net.letsgta.server.api.post.repository.PostRepository;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PostCustomRepositoryImplTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private PostCustomRepository postCustomRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        queryFactory = new JPAQueryFactory(entityManager);
        postCustomRepository = new PostCustomRepositoryImpl(queryFactory);
        prepareTestData();
    }

    @Test
    void findActivePosts_ShouldReturnPagedPosts_WhenPostsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<PostGetResponse> result = postCustomRepository.findActivePosts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3); // isDeleted=false 인 게시글만
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().getFirst().title()).isEqualTo("Post 3");
    }

    @Test
    void findActivePosts_ShouldReturnEmptyPage_WhenNoPostsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        postRepository.deleteAll(); // 데이터 초기화

        // When
        Page<PostGetResponse> result = postCustomRepository.findActivePosts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    private void prepareTestData() {
        // 사용자 생성
        User user = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .password("password")
                .role(RoleName.ROLE_USER)
                .build();
        userRepository.save(user);

        // 게시글 생성
        postRepository.save(Post.builder()
                .user(user)
                .title("Post 1")
                .content("Content 1")
                .isDeleted(false)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now())
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .title("Post 2")
                .content("Content 2")
                .isDeleted(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now())
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .title("Post 3")
                .content("Content 3")
                .isDeleted(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build());

        postRepository.save(Post.builder()
                .user(user)
                .title("Deleted Post")
                .content("Deleted Content")
                .isDeleted(true) // 삭제된 게시글
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }
}
