package book.bookspring.domain.member.api;

import book.bookspring.domain.member.application.MemberService;
import book.bookspring.domain.member.dto.OnboardingReqDto;
import book.bookspring.global.config.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/onboard")
    public ResponseEntity<Void> onboard(
            @LoginUser Long memberId,
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage,
            @Validated @RequestPart(name = "data", required = false) OnboardingReqDto onboardingReqDto
    ) {
        memberService.completeOnboarding(memberId, profileImage, onboardingReqDto);
        return ResponseEntity.ok().build();
    }

}
