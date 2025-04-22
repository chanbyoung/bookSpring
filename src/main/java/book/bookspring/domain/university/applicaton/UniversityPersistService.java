package book.bookspring.domain.university.applicaton;

import book.bookspring.domain.university.dao.UniversityRepository;
import book.bookspring.domain.university.entity.University;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniversityPersistService {

    private final UniversityRepository universityRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBatch(List<University> batch) {
        if (batch.isEmpty()) {
            log.info("저장할 데이터가 없습니다.");
            return;
        }
        log.info("batch size={} 저장 시작", batch.size());
        universityRepository.saveAll(batch);
        universityRepository.flush();
        log.info("batch 저장 완료");
    }


}
