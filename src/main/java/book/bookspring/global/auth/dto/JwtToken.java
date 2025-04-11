package book.bookspring.global.auth.dto;

import lombok.Builder;

@Builder
public record JwtToken(
        String accessToken,
        String refreshToken
) {

    public static JwtToken of(String accessToken, String refreshToken) {
        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
