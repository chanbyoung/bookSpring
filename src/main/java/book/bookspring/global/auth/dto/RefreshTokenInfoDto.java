package book.bookspring.global.auth.dto;

import lombok.Builder;

@Builder
public record RefreshTokenInfoDto(
        String id,
        String refreshToken,
        String authorities
) {

    public static RefreshTokenInfoDto of(String id, String refreshToken, String authorities) {
        return RefreshTokenInfoDto.builder()
                .id(id)
                .refreshToken(refreshToken)
                .authorities(authorities)
                .build();
    }

}
