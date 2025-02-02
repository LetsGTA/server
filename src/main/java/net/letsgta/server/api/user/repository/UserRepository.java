package net.letsgta.server.api.user.repository;

import java.util.Optional;
import net.letsgta.server.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(long id);
}
