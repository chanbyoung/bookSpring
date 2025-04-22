package book.bookspring.domain.university.applicaton;

import book.bookspring.domain.university.dto.UniversityRawDto;
import book.bookspring.domain.university.entity.University;
import book.bookspring.domain.university.util.UniversityParseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    @Value("${api.university.url}")
    private String UNIVERSITY_API_URL;

    @Value("${api.service.key}")
    private String SERVICE_KEY;

    private static final int PER_PAGE = 1_000;

    private final RestTemplate restTemplate;
    private final UniversityPersistService persistService;

    public void runSaveUniversityBatch() throws Exception {
        // 1) 첫 페이지로 totalCount, totalPages 계산
        String firstBody = fetchRaw(1);
        int totalCount = UniversityParseUtil.getTotalCount(firstBody);
        int totalPages = (int) Math.ceil((double) totalCount / PER_PAGE);
        log.info("총 {}건, 페이지 수: {}", totalCount, totalPages);

        // 2) 각 페이지별로
        for (int page = 1; page <= totalPages; page++) {
            String body = fetchRaw(page);

            // 3) 파싱 유틸로 DTO 리스트 얻기
            List<UniversityRawDto> raws = UniversityParseUtil.parseRawDtos(body);

            // 4) DTO → 엔티티, 필터링
            List<University> batch = raws.stream()
                    .filter(r -> "기존".equals(r.status()) && "대학".equals(r.universityType()))
                    .map(University::from)
                    .toList();

            // 5) 저장
            persistService.saveBatch(batch);

            log.info("페이지 {}/{} 완료 (size={})", page, totalPages, batch.size());
            Thread.sleep(100);
        }
    }

    /**
     * JSON 전체 문자열을 리턴
     */
    private String fetchRaw(int page) {
        // 1) URI 구성
        String uri = UriComponentsBuilder.fromUriString(UNIVERSITY_API_URL)
                .queryParam("page", page)
                .queryParam("perPage", PER_PAGE)
                .build()
                .toUriString();

        // 2) Authorization 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Infuser " + SERVICE_KEY);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // 3) exchange() 호출
        ResponseEntity<String> resp = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 4) 응답 검증
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("API 호출 실패: page=" + page +
                    ", status=" + resp.getStatusCodeValue());
        }
        return resp.getBody();
    }
}