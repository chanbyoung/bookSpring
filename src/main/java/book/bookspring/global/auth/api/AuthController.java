package book.bookspring.global.auth.api;

import book.bookspring.global.auth.application.AuthService;
import book.bookspring.global.auth.dto.JwtToken;
import book.bookspring.global.auth.dto.RefreshTokenInfoDto;
import book.bookspring.global.auth.dto.req.SignInDto;
import book.bookspring.global.auth.dto.req.SignUpDto;
import book.bookspring.global.config.security.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtToken> signIn(@Valid @RequestBody SignInDto signInDto) {
        JwtToken jwtToken = authService.signIn(signInDto);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody JwtToken jwtToken) {
        authService.logout(jwtToken.accessToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@LoginUser Long memberId) {
        authService.delete(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtToken> refreshAccessToken(
            @RequestBody JwtToken jwtToken
    ) {
        JwtToken newAccessToken = authService.refreshAccessToken(jwtToken);
        return ResponseEntity.ok(newAccessToken);
    }
}
