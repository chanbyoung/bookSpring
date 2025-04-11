package book.bookspring.global.auth.application;

import book.bookspring.domain.member.dao.MemberRepository;
import book.bookspring.domain.member.entity.Member;
import book.bookspring.global.auth.dto.JwtToken;
import book.bookspring.global.auth.dto.req.SignInDto;
import book.bookspring.global.auth.dto.req.SignUpDto;
import book.bookspring.global.config.redis.dao.RedisRepository;
import book.bookspring.global.config.security.TokenProvider;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void signUp(SignUpDto signUpDto) {
        if (memberRepository.existsByEmail(signUpDto.email())) {
            throw new BusinessException(signUpDto.email(), "email", ErrorCode.MEMBER_ALREADY_EXIST);
        }
        String encodePassword = passwordEncoder.encode(signUpDto.password());
        // TODO 프로필 이미지 처리
        memberRepository.save(Member.of(signUpDto, encodePassword));
    }

    public JwtToken signIn(SignInDto signInDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInDto.email(),
                        signInDto.password()
                )
        );
        // access, refresh 토큰 생성 후 반환
        return tokenProvider.generateToken(authentication);
    }

    public void logout(String accessToken) {
        // 남은 유효기간
        long expiration = tokenProvider.getExpiration(accessToken);
        // 회원 id
        String memberId = tokenProvider.getMemberIdFromToken(accessToken);
        // 남은 유효기간동안 토큰 블랙리스트 처리
        redisRepository.logoutTokens(accessToken, expiration, memberId);
    }
}
