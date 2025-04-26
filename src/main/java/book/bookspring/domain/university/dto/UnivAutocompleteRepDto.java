package book.bookspring.domain.university.dto;

import java.util.List;


public record UnivAutocompleteRepDto(
        List<String> result
) {
    public static UnivAutocompleteRepDto of(List<String> result) {
        return new UnivAutocompleteRepDto(result);
    }

}
