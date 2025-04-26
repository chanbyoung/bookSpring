package book.bookspring.domain.member.dao;

import book.bookspring.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(String username);

    boolean existsByEmail(String email);

    @Query("""
           SELECT m
           FROM Member m
           WHERE m.delete = false
           """)
    Optional<Member> findMemberById(Long id);


}
