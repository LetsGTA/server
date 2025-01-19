package org.example.server.api.category.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CategoryException extends RuntimeException {

    private final CategoryExceptionResult categoryExceptionResult;
}
