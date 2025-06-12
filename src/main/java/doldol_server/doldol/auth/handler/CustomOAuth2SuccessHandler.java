package doldol_server.doldol.auth.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.response.LoginResponse;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;

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
		if (userDetails.getUserId() == null) {
			handleNewSocialUser(response, userDetails.getSocialId());
		} else {
			handleExistingUser(response, userDetails);
		}
	}

	private void handleNewSocialUser(HttpServletResponse response, String socialId) throws IOException {

		String urlEncodedSocialId = URLEncoder.encode(socialId, StandardCharsets.UTF_8);

		String redirectUrl =
			signUpRedirectUrl + "?socialId=" + urlEncodedSocialId;

		response.addHeader(HttpHeaders.LOCATION, redirectUrl);

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			null,
			HttpStatus.FOUND
		);
	}

	private void handleExistingUser(HttpServletResponse response, CustomUserDetails userDetails) throws IOException {
		String userid = String.valueOf(userDetails.getUserId());

		UserTokenResponse loginToken = tokenProvider.createLoginToken(userid, userDetails.getRole());

		LoginResponse loginResponse = LoginResponse.builder()
			.userId(userDetails.getUserId())
			.role(userDetails.getRole())
			.accessToken(loginToken.accessToken())
			.refreshToken(loginToken.refreshToken())
			.build();

		response.addHeader(HttpHeaders.LOCATION, loginSuccessRedirectUrl);

		ResponseUtil.writeSuccessResponseWithHeaders(
			response,
			objectMapper,
			loginResponse,
			HttpStatus.FOUND
		);
	}
}