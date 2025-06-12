package doldol_server.doldol.auth.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.request.LoginRequest;
import doldol_server.doldol.auth.dto.response.LoginResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

	public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
		TokenProvider tokenProvider, ObjectMapper objectMapper) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		LoginRequest loginRequest;
		try {
			String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
			loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(loginRequest.id(), loginRequest.password(), null);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {
		handleSuccessAuthentication(response, authentication);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		handleFailureAuthentication(response);
	}

	private void handleSuccessAuthentication(HttpServletResponse response, Authentication authentication)
		throws IOException {

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		String userid = String.valueOf(userDetails.getUserId());

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		String role = authorities.stream()
			.findFirst()
			.map(GrantedAuthority::getAuthority)
			.orElseThrow(() -> new RuntimeException("권한이 식별되지 않은 사용자 입니다. : " + userid));

		UserTokenResponse loginToken = tokenProvider.createLoginToken(userid, userDetails.getRole());

		LoginResponse loginResponse = LoginResponse.builder()
			.userId(userDetails.getUserId())
			.role(role)
			.accessToken(loginToken.accessToken())
			.refreshToken(loginToken.refreshToken())
			.build();

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			loginResponse,
			HttpStatus.OK
		);
	}

	private void handleFailureAuthentication(HttpServletResponse response) throws IOException {
		ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.WRONG_ID_PW);
	}

}
