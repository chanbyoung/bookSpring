package book.bookspring.global.auth.dto.req;

public record SignInDto(
        String email,
        String password
) {

}
