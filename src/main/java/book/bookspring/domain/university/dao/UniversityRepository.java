package book.bookspring.domain.university.dao;

import book.bookspring.domain.university.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {

}
