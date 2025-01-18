package org.example.server.api.user.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.dto.response.UserGetResponse;
import org.example.server.api.user.exception.UserException;
import org.example.server.api.user.exception.UserExceptionResult;
import org.example.server.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGetServiceImpl implements UserGetService {

    private final UserRepository userRepository;

    @Override
    public UserGetResponse getUserByUserId(long id) {
        return userRepository.findByUserId(id)
                .map(UserGetResponse::from)
                .orElseThrow(() -> new UserException(UserExceptionResult.NOT_EXISTS));
    }

    @Override
    public UserGetResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserGetResponse::from)
                .orElseThrow(() -> new UserException(UserExceptionResult.NOT_EXISTS));
    }

    @Override
    public boolean isUserExist(long id) {
        return userRepository.findByUserId(id).isPresent();
    }

    @Override
    public boolean isUserExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
