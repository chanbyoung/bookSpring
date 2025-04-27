package book.bookspring.domain.member.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import book.bookspring.domain.member.dao.MemberRepository;
import book.bookspring.domain.member.dto.OnboardingReqDto;
import book.bookspring.domain.member.entity.Member;
import book.bookspring.domain.university.applicaton.UniversityService;
import book.bookspring.domain.university.entity.University;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import book.bookspring.global.utils.file.FileService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileService fileService;

    @Mock
    private UniversityService universityService;

    @Mock
    private Member mockMember;

    @BeforeEach
    void setUp() {
        // 기본적으로 멤버가 조회되도록 세팅
        when(memberRepository.findMemberById(anyLong()))
                .thenReturn(Optional.of(mockMember));
    }


    @Test
    @DisplayName("이미지와 DTO가 없으면 아무 동작도 수행하지 않는다")
    void completeOnboarding_whenNoImageAndNoDto_thenNothingHappens() {
        // when
        assertDoesNotThrow(() ->
                memberService.completeOnboarding(123L, null, null)
        );

        // then
        verify(memberRepository).findMemberById(123L);
        verifyNoInteractions(fileService, universityService, mockMember);
    }

    @Test
    @DisplayName("프로필 이미지만 있을 때 이미지가 업데이트된다")
    void completeOnboarding_withProfileImage_updatesImage() throws Exception {
        // given
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(mockMember.getProfileImage()).thenReturn("/old/path.jpg");
        when(fileService.saveProfileImage(multipartFile))
                .thenReturn("/new/path.jpg");

        // when
        memberService.completeOnboarding(123L, multipartFile, null);

        // then
        verify(fileService).deleteProfileImageByPath("/old/path.jpg");
        verify(fileService).saveProfileImage(multipartFile);
        verify(mockMember).updateProfileImage("/new/path.jpg");
        verifyNoInteractions(universityService);
    }

    @Test
    @DisplayName("DTO만 있을 때 대학 정보가 업데이트된다")
    void completeOnboarding_withOnboardingDto_updatesUniversity() {
        // given
        OnboardingReqDto dto = new OnboardingReqDto("서울대", "관악캠퍼스", "컴퓨터공학");
        University univ = new University();
        when(universityService.loadUniversity(dto)).thenReturn(univ);

        // when
        memberService.completeOnboarding(123L, null, dto);

        // then        verify(universityService).loadUniversity(dto);
        verify(mockMember).updateUniversity(univ);
        verifyNoInteractions(fileService);
    }

    @Test
    @DisplayName("멤버가 없으면 BusinessException이 발생한다")
    void completeOnboarding_memberNotFound_throwsBusinessException() {
        // given
        when(memberRepository.findMemberById(anyLong()))
                .thenReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.completeOnboarding(999L, null, null)
        );
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, ex.getErrorCode());
    }
}