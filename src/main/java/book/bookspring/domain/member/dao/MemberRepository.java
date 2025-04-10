package book.bookspring.domain.member.dao;

import book.bookspring.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(String username);

}
