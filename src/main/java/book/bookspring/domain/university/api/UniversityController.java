package book.bookspring.domain.university.api;

import book.bookspring.domain.university.applicaton.UniversityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/university")
@RequiredArgsConstructor
@Slf4j
public class UniversityController {

    private final UniversityService universityService;


    /**
     * 전체 대학 정보를 OpenAPI에서 가져와 저장하는 배치 실행 엔드포인트
     * POST /api/universities/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<String> runBatch() {
        try {
            universityService.runSaveUniversityBatch();
            return ResponseEntity.ok("University batch save completed");
        } catch (Exception ex) {
            log.error("Batch save error", ex);
            return ResponseEntity
                    .status(500)
                    .body("Batch save failed: " + ex.getMessage());
        }
    }

}
