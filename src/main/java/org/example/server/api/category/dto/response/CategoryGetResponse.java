package org.example.server.api.category.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import org.example.server.api.category.entity.Category;

@Builder
public record CategoryGetResponse(

        int categoryId,

        String name,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDateTime createdAt,

        List<CategoryGetResponse> subCategories
) {
    public static CategoryGetResponse from(Category category) {
        return CategoryGetResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .subCategories(category.getSubCategories()
                        .stream()
                        .map(CategoryGetResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
