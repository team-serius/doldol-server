package doldol_server.doldol.auth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import lombok.Getter;

public class CustomUserDetails implements UserDetails, OAuth2User {

	@Getter
	private final User user;
	private final Map<String, Object> attributes;
	@Getter
	private final String socialId;
	@Getter
	private final SocialType socialType;

	public CustomUserDetails(User user) {
		this.user = user;
		this.attributes = null;
		this.socialId = null;
		this.socialType = null;
	}

	public CustomUserDetails(User user, Map<String, Object> attributes, String socialId, SocialType socialType) {
		this.user = user;
		this.attributes = attributes;
		this.socialId = socialId;
		this.socialType = socialType;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(() -> user.getRole().name());
		return authorities;
	}

	@Override
	public String getUsername() {
		return user.getLoginId();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes != null ? attributes : Map.of();
	}

	@Override
	public String getName() {
		return user.getLoginId();
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getEmail() {
		return user.getEmail();
	}
}