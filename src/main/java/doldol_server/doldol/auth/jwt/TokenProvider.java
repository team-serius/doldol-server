package doldol_server.doldol.auth.jwt;

import static doldol_server.doldol.common.constants.TokenConstant.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.auth.util.BearerUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenProvider {

	private final SecretKey secretKey;
	private final RedisTemplate<String, String> redisTemplate;

	public TokenProvider(@Value("${security.jwt.token.secret-key}") String secretKeyString,
		RedisTemplate<String, String> redisTemplate) {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
		this.redisTemplate = redisTemplate;
	}

	public UserTokenResponse createLoginToken(final String userId, String role) {

		String accessToken = createAccessToken(userId, role);
		String refreshToken = createRefreshToken(userId, role);

		saveRefreshToken(userId, refreshToken);

		return new UserTokenResponse(
			accessToken,
			refreshToken
		);
	}

	public String createAccessToken(final String userId, final String role) {
		return createToken(userId, role, ACCESS_TOKEN_EXPIRATION_MINUTE * MINUTE_IN_MILLISECONDS);
	}

	public String createRefreshToken(final String userId, final String role) {
		return createToken(userId, role, REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		Optional<String> accessToken = BearerUtil.extractBearerToken(request);
		if (!accessToken.isEmpty()) {
			return accessToken.get();
		}
		return null;
	}

	public boolean validateToken(final String token) {
		if (token == null) {
			return false;
		}
		try {
			parseToken(token);
			return true;
		} catch (SignatureException e) {
			log.error("잘못된 jwt 서명입니다.");
		} catch (MalformedJwtException e) {
			log.error("잘못된 jwt 토큰입니다.");
		} catch (ExpiredJwtException e) {
			log.error("만료된 jwt 토큰입니다.");
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 jwt 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("jwt 클레임 문자열이 비어 있습니다.");
		}
		return false;
	}

	public Authentication getAuthenticationByAccessToken(String accessToken) {
		Claims claims = getClaimsFromToken(accessToken);
		String userId = claims.getSubject();
		String role = claims.get("role", String.class);
		CustomUserDetails customUserDetails = CustomUserDetails.fromClaims(userId, role);
		return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
	}

	public Claims getClaimsFromToken(String token) {
		return getClaims(token);
	}

	private Claims getClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			log.info("토큰 만료");
			return e.getClaims();
		} catch (JwtException e) {
			log.error("잘못된 jwt 토큰입니다. {}", e.getMessage());
			throw new IllegalArgumentException("잘못된 jwt 토큰 입니다.");
		}
	}

	public void deleteRefreshToken(String userId) {
		redisTemplate.delete(userId);
	}

	private void saveRefreshToken(String userId, String refreshToken) {
		redisTemplate.opsForValue().set(userId, refreshToken, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
	}

	private String createToken(final String userId, final String role, final long expireLength) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + expireLength);

		return Jwts.builder()
			.subject(userId)
			.claim("role", role)
			.issuedAt(now)
			.expiration(validity)
			.signWith(secretKey)
			.compact();
	}

	private Claims parseToken(final String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}