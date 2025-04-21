package book.bookspring.domain.university.util;

import book.bookspring.domain.university.dto.UniversityRawDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * OpenAPI JSON 응답을 DTO 목록으로 파싱하는 유틸리티 클래스입니다.
 */
public class UniversityParseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * JSON 응답 문자열에서 'data' 배열을 추출하여 UniversityRawDto 리스트로 변환합니다.
     *
     * @param json 전체 JSON 응답 문자열
     * @return 변환된 UniversityRawDto 리스트
     * @throws JsonProcessingException 파싱 실패 시 예외 발생
     */
    public static List<UniversityRawDto> parseRawDtos(String json) throws JsonProcessingException {
        JsonNode root = MAPPER.readTree(json);
        JsonNode dataNode = root.path("data");
        return MAPPER.convertValue(dataNode, new TypeReference<List<UniversityRawDto>>() {});
    }

    /**
     * JSON 응답 문자열에서 'totalCount' 필드 값을 추출합니다.
     *
     * @param json 전체 JSON 응답 문자열
     * @return 전체 레코드 수
     * @throws JsonProcessingException 파싱 실패 시 예외 발생
     */
    public static int getTotalCount(String json) throws JsonProcessingException {
        JsonNode root = MAPPER.readTree(json);
        return root.path("totalCount").asInt();
    }
}