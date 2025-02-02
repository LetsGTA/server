package org.example.server.api.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(

        @Size(max = 20, message = "카테고리 이름은 최대 20자 입니다.")
        @NotBlank(message = "게시글의 제목은 공백이 불가합니다.")
        String name,

        @NotNull(message = "부모카데고리식별번호는 필수입니다.")
        int parentId
) {
}
