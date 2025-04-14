package book.bookspring.global.auth.custom;

import book.bookspring.domain.member.dao.MemberRepository;
import book.bookspring.domain.member.entity.Member;
import book.bookspring.global.exception.custom.BusinessException;
import book.bookspring.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findMemberByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BusinessException(username, "username", ErrorCode.AUTHENTICATION_FAILED));
    }

    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.builder()
                .username(String.valueOf(member.getId()))
                .password(member.getPassword())
                .authority("ROLE_"+member.getRole())
                .build();

    }
}
