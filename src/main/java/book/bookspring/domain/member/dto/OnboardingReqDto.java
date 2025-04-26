package book.bookspring.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record OnboardingReqDto(
        @NotBlank
        String universityName,
        @NotBlank
        String campus,
        @NotBlank
        String major
) {

}
