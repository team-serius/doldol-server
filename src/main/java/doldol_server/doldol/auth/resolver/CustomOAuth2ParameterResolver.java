package doldol_server.doldol.auth.resolver;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomOAuth2ParameterResolver implements OAuth2AuthorizationRequestResolver {

	private static final String USER_ID = "userId";
	private static final String USER_ID_OPT = "_userId:";
	private static final String OAUTH2_LOGIN_API = "/oauth2/authorization";

	private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

	public CustomOAuth2ParameterResolver(ClientRegistrationRepository clientRegistrationRepository) {
		this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
			clientRegistrationRepository, OAUTH2_LOGIN_API);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);

		if (authorizationRequest != null) {
			return customizeAuthorizationRequest(authorizationRequest, request);
		}

		return authorizationRequest;
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);

		if (authorizationRequest != null) {
			return customizeAuthorizationRequest(authorizationRequest, request);
		}

		return authorizationRequest;
	}

	private OAuth2AuthorizationRequest customizeAuthorizationRequest(
		OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {

		String userId = request.getParameter(USER_ID);

		if (userId != null && !userId.isEmpty()) {
			String customState = authorizationRequest.getState() + USER_ID_OPT + userId;

			return OAuth2AuthorizationRequest.from(authorizationRequest)
				.state(customState)
				.build();
		}

		return authorizationRequest;
	}
}