package net.letsgta.server.api.user.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.user.application.UserDelService;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import net.letsgta.server.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDelServiceImpl implements UserDelService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void deleteUserByUserId(long id) {
        // 사용자 정보 조회
        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> new UserException(UserExceptionResult.NOT_EXISTS));

        // delete
        userRepository.delete(user);
    }
}
