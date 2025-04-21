package book.bookspring.domain.university.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "universities")
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_id", nullable = false)
    private Long id;

    @Column(name = "university_name")
    private String universityName;

    // 단과대학
    @Column(name = "college_name")
    private String collegeName;

    // 전공
    @Column(name = "major")
    private String major;

    //지역
    @Column(name = "region")
    private String region;


}
