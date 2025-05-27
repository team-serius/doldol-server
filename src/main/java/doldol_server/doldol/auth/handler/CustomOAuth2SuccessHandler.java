package doldol_server.doldol.auth.handler;

import static doldol_server.doldol.common.constants.CookieConstant.*;
import static doldol_server.doldol.common.constants.TokenConstant.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.CookieUtil;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

	@Value("${oauth2.temp-user.prefix}")
	private String tempUserPrefix;

	@Value("${oauth2.redirect-url.sign-up}")
	private String signUpRedirectUrl;

	@Value("${oauth2.redirect-url.login-success}")
	private String loginSuccessRedirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken)authentication;
		OAuth2User oAuth2User = oAuth2Token.getPrincipal();

		CustomUserDetails userDetails = (CustomUserDetails)oAuth2User;
		User user = userDetails.getUser();

		if (user.getLoginId().startsWith(tempUserPrefix)) {
			handleNewSocialUser(response, user.getLoginId());
		} else {
			handleExistingUser(response, userDetails);
		}
	}

	private void handleNewSocialUser(HttpServletResponse response, String userId) throws IOException {

		String tempToken = tokenProvider.createSocialTempToken(userId);

		String redirectUrl = signUpRedirectUrl;

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpHeaders.LOCATION, redirectUrl);
		headers.put(HttpHeaders.AUTHORIZATION, BEARER_FIX + tempToken);

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			null,
			HttpStatus.FOUND,
			headers
		);
	}

	private void handleExistingUser(HttpServletResponse response, CustomUserDetails userDetails) throws IOException {
		String userid = String.valueOf(userDetails.getUserId());

		UserTokenResponse loginToken = tokenProvider.createLoginToken(userid);

		ResponseCookie refreshTokenCookie = CookieUtil.createCookie(
			REFRESH_TOKEN_COOKIE_NAME,
			loginToken.refreshToken(),
			REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS
		);

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpHeaders.LOCATION, loginSuccessRedirectUrl);
		headers.put(HttpHeaders.AUTHORIZATION, BEARER_FIX + loginToken.accessToken());
		headers.put(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			null,
			HttpStatus.FOUND,
			headers
		);
	}
}