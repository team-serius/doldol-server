package doldol_server.doldol.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.request.LoginRequest;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.Set;

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