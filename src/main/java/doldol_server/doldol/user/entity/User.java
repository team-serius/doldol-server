package doldol_server.doldol.user.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "USERS",
	indexes = {
		@Index(name = "idx_user_name_id", columnList = "name, user_id")
	})
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

	@Column(name = "phone", unique = true)
	private String phone;

	@Column(name = "email", unique = true)
	private String email;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "role")
	private Role role = Role.USER;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@Column(name = "social_id", unique = true)
	private String socialId;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "social_type")
	private SocialType socialType;

	@Builder
	public User(String loginId, String name, String password, String phone,
		String email, String socialId, SocialType socialType) {
		this.loginId = loginId;
		this.name = name;
		this.password = password;
		this.phone = phone;
		this.email = email;
		this.socialId = socialId;
		this.socialType = socialType;
	}

	@Builder
	public User(Long id) {
		this.id = id;
	}

	public void updateSocialInfo(String socialId, SocialType socialType) {
		this.socialId = socialId;
		this.socialType = socialType;
	}

	public void updateUserName(String name) {
		this.name = name;
	}

	public void updateUserPassword(String password) {
		this.password = password;
	}

	public void updateDeleteStatus() {
		this.isDeleted = true;
	}

	public void deleteOAuthInfo() {
		this.socialId = null;
		this.socialType = null;
	}
}
