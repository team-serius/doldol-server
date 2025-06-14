package doldol_server.doldol.auth.util;

import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

public class BearerUtil {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	public static Optional<String> extractBearerToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		return extractBearerToken(authorizationHeader);
	}

	private static Optional<String> extractBearerToken(String authorizationHeader) {
		if (!validate(authorizationHeader)) {
			return Optional.empty();
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

		if (token.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(token);
	}

	private static boolean validate(String authorizationHeader) {
		return authorizationHeader != null &&
			!authorizationHeader.trim().isEmpty() &&
			authorizationHeader.startsWith(BEARER_PREFIX) &&
			authorizationHeader.length() > BEARER_PREFIX.length();
	}
}