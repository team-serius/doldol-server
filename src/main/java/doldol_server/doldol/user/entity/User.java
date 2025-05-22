package doldol_server.doldol.user.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "USERS")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    public User(String loginId, String name, String password, String phoneNumber, String socialId,
                SocialType socialType) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.socialId = socialId;
        this.socialType = socialType;
    }
}
