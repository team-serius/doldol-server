package doldol_server.doldol.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.jwt.TokenProvider;

import org.springframework.security.authentication.AuthenticationManager;

public class CustomUserLoginFilter extends CustomUsernamePasswordAuthenticationFilter {

	private static final String LONGIN_URI = "/auth/login";

	public CustomUserLoginFilter(AuthenticationManager authenticationManager,
		TokenProvider tokenProvider,
		ObjectMapper objectMapper) {
		super(authenticationManager, tokenProvider, objectMapper);
		this.setFilterProcessesUrl(LONGIN_URI);
	}

}
