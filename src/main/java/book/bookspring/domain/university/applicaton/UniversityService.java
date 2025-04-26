package book.bookspring.domain.university.applicaton;

import book.bookspring.domain.university.dao.UniversityRepository;
import book.bookspring.domain.university.dto.UnivAutocompleteRepDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    public UnivAutocompleteRepDto autocompleteUniversities(String univName, Pageable pageable) {
        return UnivAutocompleteRepDto.of(
                universityRepository.
                        searchUniversities(univName, pageable)
                        .stream()
                        .toList());
    }

    public UnivAutocompleteRepDto autocompleteCampus(String univName, String campusName,
            Pageable pageable) {
        return UnivAutocompleteRepDto.of(
                universityRepository
                        .searchCampusesByUniversity(univName, campusName, pageable)
                        .stream()
                        .toList()
        );
    }

    public UnivAutocompleteRepDto autocompleteMajors(String univName, String campusName,
            String majorName,
            Pageable pageable) {
        return UnivAutocompleteRepDto.of(
                universityRepository
                        .searchMajorsByUniversityAndCampus(univName, campusName, majorName,
                                pageable)
                        .stream()
                        .toList()
        );
    }
}