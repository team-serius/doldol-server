package doldol_server.doldol.auth.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CustomOAuth2Exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		AuthErrorCode errorCode = getAuthErrorCode(exception);
		ResponseUtil.writeErrorResponse(response, objectMapper, errorCode);
	}

	private AuthErrorCode getAuthErrorCode(AuthenticationException exception) {
		if (exception instanceof CustomOAuth2Exception customException) {
			return (AuthErrorCode)customException.getErrorCode();
		}

		return AuthErrorCode.INVALID_TOKEN;
	}
}