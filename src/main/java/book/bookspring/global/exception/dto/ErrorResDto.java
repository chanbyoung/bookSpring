package book.bookspring.global.exception.dto;

import book.bookspring.global.exception.enums.ErrorCode;
import lombok.Builder;

@Builder
public record ErrorResDto(
        Integer status,
        String message
) {

    public static ErrorResDto of(ErrorCode errorCode) {
        return ErrorResDto.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }
}

