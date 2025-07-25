package doldol_server.doldol.auth.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;

	@Value("${oauth2.redirect-url.sign-up}")
	private String signUpRedirectUrl;

	@Value("${oauth2.redirect-url.login-success}")
	private String loginSuccessRedirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken)authentication;
		OAuth2User oAuth2User = oAuth2Token.getPrincipal();
		String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();
		CustomUserDetails userDetails = (CustomUserDetails)oAuth2User;

		if (userDetails.getUserId() == null) {
			handleNewSocialUser(response, userDetails.getSocialId(), registrationId);
		} else {
			handleExistingUser(response, userDetails, registrationId);
		}
	}

	private void handleNewSocialUser(HttpServletResponse response, String socialId, String registrationId) throws
		IOException {

		String urlEncodedSocialId = URLEncoder.encode(socialId, StandardCharsets.UTF_8);
		String urlEncodedRegistrationId = URLEncoder.encode(registrationId.toUpperCase(), StandardCharsets.UTF_8);

		String redirectUrl =
			signUpRedirectUrl + "?socialId=" + urlEncodedSocialId + "&socialType=" + urlEncodedRegistrationId;

		response.sendRedirect(redirectUrl);
	}

	private void handleExistingUser(HttpServletResponse response, CustomUserDetails userDetails,
		String registrationId) throws IOException {


		String userid = String.valueOf(userDetails.getUserId());

		UserTokenResponse loginToken = tokenProvider.createLoginToken(userid, userDetails.getRole());

		String redirectUrl = loginSuccessRedirectUrl +
			"?accessToken=" + URLEncoder.encode(loginToken.accessToken(), StandardCharsets.UTF_8) +
			"&refreshToken=" + URLEncoder.encode(loginToken.refreshToken(), StandardCharsets.UTF_8) +
			"&userId=" + userDetails.getUserId() +
			"&role=" + URLEncoder.encode(userDetails.getRole().toString(), StandardCharsets.UTF_8) +
			"&socialType=" + URLEncoder.encode(registrationId.toUpperCase(), StandardCharsets.UTF_8);

		response.sendRedirect(redirectUrl);
	}
}