package org.example.server.api.category.repository;

import org.example.server.api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);
}
