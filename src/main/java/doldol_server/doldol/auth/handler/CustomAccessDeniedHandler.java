package doldol_server.doldol.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
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

        ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.ACCESS_DENIED);
    }
}