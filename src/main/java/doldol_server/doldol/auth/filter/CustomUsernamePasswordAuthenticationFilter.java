package doldol_server.doldol.auth.filter;

import static doldol_server.doldol.common.constants.CookieConstant.REFRESH_TOKEN_COOKIE_NAME;
import static doldol_server.doldol.common.constants.TokenConstant.BEARER_FIX;
import static doldol_server.doldol.common.constants.TokenConstant.DAYS_IN_MILLISECONDS;
import static doldol_server.doldol.common.constants.TokenConstant.REFRESH_TOKEN_EXPIRATION_DAYS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.LoginReqDto;
import doldol_server.doldol.auth.dto.LoginResDto;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.CookieUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.common.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
public abstract class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                      TokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginReqDto loginReqDto;
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            loginReqDto = objectMapper.readValue(messageBody, LoginReqDto.class);
            validateLoginRequestDto(loginReqDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReqDto.getId(), loginReqDto.getPassword(), null);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException {
        handleSuccessAuthentication(response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        handleFailureAuthentication(response);
    }

    private void handleSuccessAuthentication(HttpServletResponse response, Authentication authentication)
            throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userid = String.valueOf(userDetails.getUserId());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("권한이 식별되지 않은 사용자 입니다. : " + userid));

        UserTokenResponse loginToken = tokenProvider.createLoginToken(userid);

        LoginResDto loginResDto = LoginResDto.builder()
                .role(role)
                .build();

        ApiResponse<LoginResDto> apiResponse = ApiResponse.ok(loginResDto);

        ResponseCookie refreshTokenCookie = CookieUtil.createCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                loginToken.refreshToken(),
                REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS
        );

        response.setHeader(AUTHORIZATION, BEARER_FIX + loginToken.accessToken());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private void handleFailureAuthentication(HttpServletResponse response) throws IOException {

        ErrorResponse<Void> errorResponse = ErrorResponse.error(
                AuthErrorCode.WRONG_ID_PW.getCode(),
                AuthErrorCode.WRONG_ID_PW.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    protected abstract void validateLoginRequestDto(LoginReqDto loginReqDto);
}