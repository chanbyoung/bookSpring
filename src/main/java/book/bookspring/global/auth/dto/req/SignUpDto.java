package book.bookspring.global.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        @NotBlank
        @Size(max = 50)
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank
        @Size(max = 50)
        String password

) {

}
