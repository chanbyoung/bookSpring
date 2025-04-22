package book.bookspring.domain.university.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UniversityRawDto(
        @JsonProperty("단과대학명")
        String collegeName,
        @JsonProperty("대학구분")
        String universityType,
        @JsonProperty("학과상태")
        String status,
        @JsonProperty("학교명")
        String universityName,
        @JsonProperty("학부_과(전공)명")
        String major,
        @JsonProperty("지역")
        String region
) {}
