package doldol_server.doldol.auth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import doldol_server.doldol.user.entity.Role;
import doldol_server.doldol.user.entity.User;
import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

	private final Long userId;
	private final String email;
	private final String loginId;
	private final String password;
	private final String role;

	private final Map<String, Object> attributes;
	private final String socialId;

	public CustomUserDetails(User user) {
		this.userId = user.getId();
		this.email = user.getEmail();
		this.loginId = user.getLoginId();
		this.password = user.getPassword();
		this.role = user.getRole().getRole();
		this.attributes = null;
		this.socialId = null;
	}

	public CustomUserDetails(Long userId, Map<String, Object> attributes, String socialId) {
		this.userId = userId;
		this.email = null;
		this.loginId = null;
		this.password = null;
		this.role = Role.USER.getRole(); 
		this.attributes = attributes;
		this.socialId = socialId;
	}

	public CustomUserDetails(Map<String, Object> attributes, String socialId) {
		this.userId = null;
		this.email = null;
		this.loginId = null;
		this.password = null;
		this.role = Role.USER.getRole();
		this.attributes = attributes;
		this.socialId = socialId;
	}

	public static CustomUserDetails fromClaims(String userId, String role) {
		return new CustomUserDetails(
			Long.parseLong(userId),
			null,
			null,
			null,
			role,
			null,
			null
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

	@Override
	public String getUsername() {
		if (userId == null) {
			return socialId;
		}
		return userId.toString();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getName() {
		if (userId == null) {
			return socialId;
		}
		return userId.toString();
	}

	private CustomUserDetails(Long userId, String email, String loginId, String password,
		String role, Map<String, Object> attributes, String socialId) {
		this.userId = userId;
		this.email = email;
		this.loginId = loginId;
		this.password = password;
		this.role = role;
		this.attributes = attributes;
		this.socialId = socialId;
	}
}