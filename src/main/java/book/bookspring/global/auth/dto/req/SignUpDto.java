package book.bookspring.global.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        @NotBlank
        @Size(max = 50)
        String email,

        @NotBlank
        @Size(max = 50)
        String password,

        String profile_image
) {

}
