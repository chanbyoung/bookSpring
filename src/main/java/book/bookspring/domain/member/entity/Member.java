package book.bookspring.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;

import book.bookspring.global.auth.dto.req.SignUpDto;
import book.bookspring.global.config.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

    private static final int ACCOUNT_LENGTH = 50;
    private static final int MAX_VALUE = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = ACCOUNT_LENGTH)
    private String email;

    @Column(name = "password", nullable = false, length = ACCOUNT_LENGTH)
    private String password;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "profile_image", length = MAX_VALUE)
    private String profile_image;



    @Builder
    public Member(Long id, String email, String password, Role role, String profile_image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profile_image = profile_image;
    }

    public static Member of(SignUpDto signUpDto, String encodedPassword) {
        return Member.builder()
                .email(signUpDto.email())
                .password(encodedPassword)
                .role(Role.USER)
                .profile_image(signUpDto.profile_image())
                .build();
    }
}
