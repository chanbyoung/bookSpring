package book.bookspring.global.auth.dto;

public record RefreshTokenInfoDto(
        String email,
        String refreshToken,
        String authorities
) {

}
