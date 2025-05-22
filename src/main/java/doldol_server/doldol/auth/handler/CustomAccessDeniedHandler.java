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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ErrorResponse<Void> errorResponse = ErrorResponse.error(
                AuthErrorCode.ACCESS_DENIED.getCode(),
                AuthErrorCode.ACCESS_DENIED.getMessage()
        );

        response.setStatus(AuthErrorCode.ACCESS_DENIED.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}