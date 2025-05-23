package doldol_server.doldol.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private final String role;
}
