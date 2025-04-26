package book.bookspring.domain.member.application;

import book.bookspring.domain.member.dao.MemberRepository;
import book.bookspring.domain.member.dto.OnboardingReqDto;
import book.bookspring.domain.member.entity.Member;
import book.bookspring.domain.university.applicaton.UniversityService;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import book.bookspring.global.utils.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final UniversityService universityService;

    public void completeOnboarding(Long memberId, MultipartFile profileImage,
            OnboardingReqDto onboardingReqDto) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(
                () -> new BusinessException(memberId, "memberId", ErrorCode.MEMBER_NOT_FOUND)
        );

        if (profileImage != null && !profileImage.isEmpty()) {
            handleProfileImageUpdate(member, profileImage);
        }

    }

    /**
     * 기존 이미지를 삭제 후, 새 이미지를 저장하고
     * 멤버 엔티티에 경로만 업데이트.
     */
    private void handleProfileImageUpdate(Member member, MultipartFile newImage) {
        // 기존 파일 삭제 (full-path을 받아 내부에서 파일명 추출)
        fileService.deleteProfileImageByPath(member.getProfileImage());

        // 새 파일 저장 & 엔티티 업데이트
        String newPath = fileService.saveProfileImage(newImage);
        member.updateProfileImage(newPath);
    }




}
