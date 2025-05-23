package book.bookspring.domain.university.dao;

import book.bookspring.domain.university.entity.University;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface UniversityRepository extends JpaRepository<University, Long> {

    @Query("""
           SELECT DISTINCT u.universityName
           FROM University u
           WHERE u.universityName LIKE %:q%
           """)
    Page<String> searchUniversities(@Param("q") String univName, Pageable pageable);

    @Query("""
           SELECT DISTINCT u.campusType
           FROM University u
           WHERE u.universityName = :univName
           """)
    List<String> findCampusesByUniversity(@Param("univName") String univName);

    @Query("""
           SELECT DISTINCT u.major
           FROM University u
           WHERE u.universityName = :univName
             AND u.campusType = :campus
             AND u.major LIKE %:q%
           """)
    Page<String> searchMajorsByUniversityAndCampus(@Param("univName") String univName, @Param("campus") String campus, @Param("q") String majorName, Pageable pageable);

    @Query("""
           SELECT u
           FROM University u
           WHERE u.universityName = :univName
             AND u.campusType = :campus
             AND u.major = :major
           """)
    Optional<University> findUniversityByOnboardingInfo(@Param("univName") String univName, @Param("campus") String campus, @Param("major") String major);
}
