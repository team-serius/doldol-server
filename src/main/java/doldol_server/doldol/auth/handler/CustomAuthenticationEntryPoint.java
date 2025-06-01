package doldol_server.doldol.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
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

        ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.INVALID_TOKEN);
    }
}
