package book.bookspring.global.config.security;

import book.bookspring.global.auth.custom.CustomUserDetails;
import book.bookspring.global.auth.dto.JwtToken;
import book.bookspring.global.auth.dto.RefreshTokenInfoDto;
import book.bookspring.global.config.redis.dao.RedisRepository;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenProvider {

    private final Key key;
    private final RedisRepository redisRepository;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    @Autowired
    public TokenProvider(@Value("${jwt.secret}") String secretKey,
            RedisRepository redisRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisRepository = redisRepository;
    }

    public JwtToken generateToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String userId = authentication.getName();

        // Access, Refresh Token 생성
        String accessToken = createToken(userId, roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(userId, null, REFRESH_TOKEN_EXPIRATION);

        RefreshTokenInfoDto refreshTokenInfoDto = RefreshTokenInfoDto.of(userId, refreshToken,
                roles);

        //redis에 refreshToken 저장
        redisRepository.storeRefreshToken(refreshTokenInfoDto);

        return JwtToken.of(accessToken, refreshToken);
    }

    // 공통 토큰 생성 로직
    private String createToken(String userId, String roles, long expiration) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null) {
            claims.put("roles", roles);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     */
    public JwtToken refreshAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사
        validateToken(refreshToken);

        // 2. 토큰에서 사용자 ID 추출
        String userId = getMemberIdFromToken(refreshToken);

        // 3. Redis에서 Refresh 토큰 유효성 검사
        if (!redisRepository.isValidRefreshToken(userId, refreshToken)) {
            throw new BusinessException(refreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. Redis에서 사용자 권한 정보 추출
        String authorities = redisRepository.getAuthorities(userId);

        // 5. 새로운 Access 토큰 생성
        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION);

        // 7. 새로운 AuthResponseDto 반환 (기존 Refresh 토큰 유지)
        return JwtToken.of(newAccessToken, refreshToken);
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    public String getMemberIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 사용자 정보 추출 후 Authentication 객체 생성
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 사용자 정보 추출
        Claims claims = parseClaims(token);
        String id = claims.getSubject();
        String roles = claims.get("roles", String.class);

        // 2. 권한 정보 설정
        Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        String authority = authorities.isEmpty() ? "ROLE_USER" : authorities.iterator().next().getAuthority();


        // 3. CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .username(id)
                .authority(authority)
                .build();

        // 4. Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException e) {
            log.info("invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 만료 시간 가져오기
     *
     * @param token JWT 토큰
     * @return 토큰의 남은 만료 시간 (밀리초)
     */
    public long getExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }


}
