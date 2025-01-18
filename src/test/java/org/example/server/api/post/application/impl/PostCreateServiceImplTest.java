package org.example.server.api.post.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.server.api.post.dto.request.PostCreateRequest;
import org.example.server.api.post.repository.PostRepository;
import org.example.server.api.user.application.impl.UserGetServiceImpl;
import org.example.server.api.user.exception.UserException;
import org.example.server.api.user.exception.UserExceptionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class PostCreateServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserGetServiceImpl userGetService;

    @InjectMocks
    private PostCreateServiceImpl postCreateService;

    private PostCreateRequest testPostCreateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPostCreateRequest = new PostCreateRequest(1, "test", "test");
    }

    @Test
    void createPost_WhenUserExists_ShouldSavePost() {
        // Given
        when(userGetService.isUserExist(anyLong())).thenReturn(true);

        // When
        postCreateService.createPost(testPostCreateRequest);

        // Then
        verify(userGetService, times(1)).isUserExist(anyLong());
        verify(postRepository, times(1)).save(any());
    }

    @Test
    void createPost_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        when(userGetService.isUserExist(anyLong())).thenReturn(false);

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> postCreateService.createPost(testPostCreateRequest));

        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());
        verify(userGetService, times(1)).isUserExist(anyLong());
        verify(postRepository, never()).save(any());
    }
}