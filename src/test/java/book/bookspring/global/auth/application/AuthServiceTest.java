package book.bookspring.global.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import book.bookspring.domain.member.dao.MemberRepository;
import book.bookspring.domain.member.entity.Member;
import book.bookspring.domain.member.entity.Role;
import book.bookspring.global.auth.dto.JwtToken;
import book.bookspring.global.auth.dto.req.SignInDto;
import book.bookspring.global.auth.dto.req.SignUpDto;
import book.bookspring.global.config.redis.dao.RedisRepository;
import book.bookspring.global.config.security.TokenProvider;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final static String TEST_EMAIL = "test@example.com";
    private final static String TEST_PASSWORD = "password123";

    @InjectMocks
    private AuthService authService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RedisRepository redisRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    @DisplayName("정상적으로 회원가입에 성공합니다.")
    @Test
    void signUp() {
        // given
        SignUpDto signUpDto = new SignUpDto(TEST_EMAIL, TEST_PASSWORD);
        when(memberRepository.existsByEmail(signUpDto.email())).thenReturn(false);
        when(passwordEncoder.encode(signUpDto.password())).thenReturn("encodedPassword");

        //when
        authService.signUp(signUpDto);

        //then
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(passwordEncoder).encode(signUpDto.password());
    }

    @DisplayName("이메일이 이미 가입 이력이 있어 회원가입에 실패합니다.")
    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // given
        SignUpDto signUpDto = new SignUpDto(TEST_EMAIL, TEST_PASSWORD);
        when(memberRepository.existsByEmail(signUpDto.email())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(signUpDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_ALREADY_EXIST.getMessage());
    }

    @DisplayName("정상적으로 로그인에 성공하면 토큰을 반환한다")
    @Test
    void signIn() {
        // given
        SignInDto signInDto = new SignInDto(TEST_EMAIL, TEST_PASSWORD);
        Authentication authentication = mock(Authentication.class);
        JwtToken expectedToken = new JwtToken("accessToken", "refreshToken");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(tokenProvider.generateToken(authentication))
                .thenReturn(expectedToken);

        // when
        JwtToken result = authService.signIn(signInDto);

        // then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).generateToken(authentication);
        assertThat(result).isEqualTo(expectedToken);
    }

    @DisplayName("정상적으로 로그아웃에 성공한다.")
    @Test
    void logout() {
        // given
        String jwtToken = "validJwtToken";
        String userId = "12345";
        long expiration = 3600000L;

        when(tokenProvider.getExpiration(jwtToken)).thenReturn(expiration);
        when(tokenProvider.getMemberIdFromToken(jwtToken)).thenReturn(userId);

        // when
        authService.logout(jwtToken);

        // then
        verify(redisRepository, times(1)).logoutTokens(jwtToken, expiration, userId);
    }

    @DisplayName("정상적으로 회원 탈퇴에 성공한다.")
    @Test
    void delete() {
        //given
        Member mockMember = Member.builder()
                .id(1L)
                .role(Role.USER)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));

        //when
        authService.delete(mockMember.getId());

        //then
        assertThat(mockMember.isDelete()).isTrue();
    }

    @DisplayName("Refresh 토큰을 이용하여 새로운 Access 토큰을 발급한다.")
    @Test
    void refreshAccessToken() {
        // given
        String newToken = "newValidJwtToken";
        JwtToken jwtToken = new JwtToken("oldAccessToken", "refreshToken");
        when(tokenProvider.refreshAccessToken(jwtToken.refreshToken())).thenReturn(new JwtToken(newToken, "refreshToken"));

        // when
        JwtToken newJwtToken = authService.refreshAccessToken(jwtToken);

        // then
        assertThat(newJwtToken.accessToken()).isEqualTo(newToken);
        verify(tokenProvider, times(1)).refreshAccessToken(jwtToken.refreshToken());
    }

    @DisplayName("이미 삭제 처리된 회원을 삭제하면 오류가 발생한다.")
    @Test
    void shouldThrowException_whenDeletingAlreadyDeletedMember(){
        // given
        Long memberId = 1L;
        Member mockMember = mock(Member.class);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(mockMember.isDelete()).thenReturn(true);

        // when && then
        assertThatThrownBy(() -> authService.delete(memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_ALREADY_DELETE.getMessage());
    }
}