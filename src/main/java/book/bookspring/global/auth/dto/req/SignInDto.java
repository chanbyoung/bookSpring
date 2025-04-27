package book.bookspring.global.auth.dto.req;

import jakarta.validation.constraints.NotBlank;

public record SignInDto(
        @NotBlank
        String email,
        @NotBlank
        String password
) {

}
