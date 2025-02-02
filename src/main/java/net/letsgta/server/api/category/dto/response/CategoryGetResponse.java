package net.letsgta.server.api.category.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import net.letsgta.server.api.category.entity.Category;

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
                        .toList())
                .build();
    }
}
