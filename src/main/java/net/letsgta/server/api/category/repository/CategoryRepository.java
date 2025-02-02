package net.letsgta.server.api.category.repository;

import java.util.Optional;
import net.letsgta.server.api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
