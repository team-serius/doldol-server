package doldol_server.doldol.auth.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.util.ResponseUtil;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;
	private final String[] whiteList;
	private final String[] blackList;
	private final ObjectMapper objectMapper;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = tokenProvider.resolveAccessToken(request);
		try {
			if (accessToken != null && tokenProvider.validateToken(accessToken)) {
				Authentication authentication = tokenProvider.getAuthenticationByAccessToken(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			SecurityContextHolder.clearContext();
			ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.TOKEN_EXPIRED);
			return;
		} catch (IncorrectClaimException e) {
			SecurityContextHolder.clearContext();
			ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.INCORRECT_CLAIM_TOKEN);
			return;
		} catch (UsernameNotFoundException e) {
			SecurityContextHolder.clearContext();
			ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.USER_NOT_FOUND);
			return;
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			ResponseUtil.writeErrorResponse(response, objectMapper, AuthErrorCode.INVALID_TOKEN);
			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestURI = request.getRequestURI();

		boolean isInBlackList = Arrays.stream(blackList)
			.anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

		if (isInBlackList) {
			return false;
		}

		return Arrays.stream(whiteList)
			.anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
	}
}
