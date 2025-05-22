package doldol_server.doldol.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import doldol_server.doldol.auth.dto.LoginReqDto;
import doldol_server.doldol.auth.jwt.TokenProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;

public class CustomUserLoginFilter extends CustomUsernamePasswordAuthenticationFilter {

    private static final String LONGIN_URI = "/auth/login";
    private final Validator validator;

    public CustomUserLoginFilter(AuthenticationManager authenticationManager,
                                 TokenProvider tokenProvider,
                                 ObjectMapper objectMapper,
                                 Validator validator) {
        super(authenticationManager, tokenProvider, objectMapper);
        this.validator = validator;
        this.setFilterProcessesUrl(LONGIN_URI);
    }

    @Override
    protected void validateLoginRequestDto(LoginReqDto loginReqDto) {
        Set<ConstraintViolation<LoginReqDto>> violations = validator.validate(loginReqDto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.iterator().next().getMessage();
            throw new IllegalArgumentException(errorMessage);
        }
    }
}