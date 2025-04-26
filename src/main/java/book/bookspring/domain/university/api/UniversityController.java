package book.bookspring.domain.university.api;

import book.bookspring.domain.university.applicaton.UniversityApiClient;
import book.bookspring.domain.university.applicaton.UniversityService;
import book.bookspring.domain.university.dto.UnivAutocompleteRepDto;
import book.bookspring.domain.university.entity.University;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/university")
@RequiredArgsConstructor
@Slf4j
public class UniversityController {

    private final UniversityService universityService;
    private final UniversityApiClient apiClient;

    /**
     * 전체 대학 정보를 OpenAPI에서 가져와 저장하는 배치 실행 엔드포인트 POST /api/universities/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<String> runBatch() {
        try {
            apiClient.runSaveUniversityBatch();
            return ResponseEntity.ok("University batch save completed");
        } catch (Exception ex) {
            log.error("Batch save error", ex);
            return ResponseEntity
                    .status(500)
                    .body("Batch save failed: " + ex.getMessage());
        }
    }

    @GetMapping("/autocomplete/univName")
    public UnivAutocompleteRepDto searchUniversities(
            @RequestParam String univName,
            Pageable pageable
    ) {
        return universityService.autocompleteUniversities(univName, pageable);
    }

    @GetMapping("/autocomplete/campus")
    public UnivAutocompleteRepDto searchCampuses(
            @RequestParam String univName,
            @RequestParam String campusName,
            Pageable pageable
    ) {
        return universityService.autocompleteCampus(univName, campusName, pageable);
    }

    @GetMapping("/autocomplete/major")
    public UnivAutocompleteRepDto searchMajors(
            @RequestParam String univName,
            @RequestParam String campusName,
            @RequestParam String majorName,
            Pageable pageable
    ) {
        return universityService.autocompleteMajors(univName, campusName, majorName, pageable);
    }

}
