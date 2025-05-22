package doldol_server.doldol.auth.filter;

import static doldol_server.doldol.common.constants.CookieConstant.REFRESH_EXPIRATION_DELETE;
import static doldol_server.doldol.common.constants.CookieConstant.REFRESH_TOKEN_COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.util.CookieUtil;
import doldol_server.doldol.common.response.ApiResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

public class CustomLogoutFilter extends GenericFilterBean {

    private static final String LOGOUT_URL = "/auth/logout";

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public CustomLogoutFilter(TokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!request.getRequestURI().equals(LOGOUT_URL) || !HttpMethod.POST.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = tokenProvider.resolveAccessToken(request);

        Claims claimsByAccessToken = tokenProvider.getClaimsFromToken(accessToken);
        String id = claimsByAccessToken.getSubject();

        tokenProvider.deleteRefreshToken(id);

        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, null, REFRESH_EXPIRATION_DELETE).toString());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.noContent()));
    }
}
