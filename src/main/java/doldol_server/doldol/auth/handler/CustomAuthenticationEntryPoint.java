package doldol_server.doldol.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorResponse<Void> errorResponse = ErrorResponse.error(
                AuthErrorCode.INVALID_TOKEN.getCode(),
                AuthErrorCode.INVALID_TOKEN.getMessage()
        );

        response.setStatus(AuthErrorCode.INVALID_TOKEN.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}