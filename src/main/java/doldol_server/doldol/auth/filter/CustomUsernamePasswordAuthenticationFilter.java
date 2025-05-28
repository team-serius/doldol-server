package doldol_server.doldol.auth.filter;

import static doldol_server.doldol.common.constants.CookieConstant.REFRESH_TOKEN_COOKIE_NAME;
import static doldol_server.doldol.common.constants.TokenConstant.BEARER_FIX;
import static doldol_server.doldol.common.constants.TokenConstant.DAYS_IN_MILLISECONDS;
import static doldol_server.doldol.common.constants.TokenConstant.REFRESH_TOKEN_EXPIRATION_DAYS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.request.LoginRequest;
import doldol_server.doldol.auth.dto.response.LoginResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.CookieUtil;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

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

		UserTokenResponse loginToken = tokenProvider.createLoginToken(userid);

		LoginResponse loginResponse = LoginResponse.builder()
			.userId(userDetails.getUserId())
			.role(role)
			.build();

		ResponseCookie refreshTokenCookie = CookieUtil.createCookie(
			REFRESH_TOKEN_COOKIE_NAME,
			loginToken.refreshToken(),
			REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS
		);

		Map<String, String> headers = Map.of(
			AUTHORIZATION, BEARER_FIX + loginToken.accessToken(),
			HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()
		);

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			loginResponse,
			HttpStatus.OK,
			headers
		);
	}

	private void handleFailureAuthentication(HttpServletResponse response) throws IOException {
		ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.WRONG_ID_PW);
	}

}