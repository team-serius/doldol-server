package doldol_server.doldol.auth.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.jwt.dto.UserTokenResponse;
import doldol_server.doldol.common.constants.CookieConstant;
import doldol_server.doldol.user.entity.Role;
import doldol_server.doldol.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@DisplayName("JWT 토큰 검증 테스트")
@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

	private static final String SECRET_KEY = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
	private static final String USER_ID = "1";

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;


	@Mock
	private HttpServletRequest request;

	private TokenProvider tokenProvider;
	private User testUser;

	@BeforeEach
	void setUp() {
		tokenProvider = new TokenProvider(SECRET_KEY, redisTemplate);

		testUser = User.builder()
			.loginId("testuser")
			.email("test@example.com")
			.password("password")
			.build();
	}

	@Test
	@DisplayName("Access Token을 성공적으로 생성한다")
	void createAccessToken_Success() {
		// when
		String accessToken = tokenProvider.createAccessToken(USER_ID, Role.USER.getRole());

		// then
		assertThat(accessToken).isNotBlank();
	}

	@Test
	@DisplayName("Refresh Token을 성공적으로 생성한다")
	void createRefreshToken_Success() {
		// when
		String refreshToken = tokenProvider.createRefreshToken(USER_ID, Role.USER.getRole());

		// then
		assertThat(refreshToken).isNotBlank();
	}

	@Test
	@DisplayName("Login Token을 성공적으로 생성한다")
	void createLoginToken_Success() {
		// given
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		// when
		UserTokenResponse tokenResponse = tokenProvider.createLoginToken(USER_ID, Role.USER.getRole());

		// then
		assertThat(tokenResponse).isNotNull();
		assertThat(tokenResponse.accessToken()).isNotBlank();
		assertThat(tokenResponse.refreshToken()).isNotBlank();
		verify(redisTemplate).opsForValue();
		verify(valueOperations).set(eq(USER_ID), anyString(), anyLong(), any());
	}

	@Test
	@DisplayName("존재하지 않는 사용자로 Login Token 생성 시 예외 발생")
	void createLoginToken_UserNotFound_ThrowsException() {
		// 이 테스트는 더 이상 적용되지 않음 (UserRepository 조회 로직이 제거됨)
		// 다른 예외 케이스가 필요하다면 별도로 작성
	}

	@Test
	@DisplayName("유효한 토큰일 시 검증값은 true를 반환한다")
	void validateToken_ValidToken_ChecksBehavior() {
		// given
		String validToken = tokenProvider.createAccessToken(USER_ID, Role.USER.getRole());

		// when
		boolean isValid = tokenProvider.validateToken(validToken);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	@DisplayName("null 토큰일 시 검증값은 false를 반환한다")
	void validateToken_NullToken_ReturnsFalse() {
		// when
		boolean isValid = tokenProvider.validateToken(null);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	@DisplayName("잘못된 형식의 토큰일 시 검증값은 false를 반환한다")
	void validateToken_MalformedToken_ReturnsFalse() {
		// given
		String wrongToken = "wrongToken";

		// when
		boolean isValid = tokenProvider.validateToken(wrongToken);

		// then
		assertThat(isValid).isFalse();
	}

	@Test
	@DisplayName("만료된 토큰일 시 ExpiredJwtException을 던진다")
	void validateToken_ExpiredToken_ThrowsExpiredJwtException() {
		// given
		SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		String expiredToken = Jwts.builder()
			.subject(USER_ID)
			.claim("role", Role.USER.getRole())
			.issuedAt(new Date(System.currentTimeMillis() - 10000))
			.expiration(new Date(System.currentTimeMillis() - 1000))
			.signWith(secretKey)
			.compact();

		// when & then
		assertThatThrownBy(() -> tokenProvider.validateToken(expiredToken))
			.isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
	}

	@Test
	@DisplayName("쿠키에서 Access Token을 추출한다")
	void resolveAccessToken_ValidCookie_ReturnsToken() {
		// given
		String token = "accessToken";
		Cookie accessTokenCookie = new Cookie(CookieConstant.ACCESS_TOKEN_COOKIE_NAME, token);
		Cookie[] cookies = {accessTokenCookie};
		given(request.getCookies()).willReturn(cookies);

		// when
		String accessToken = tokenProvider.resolveAccessToken(request);

		// then
		assertThat(accessToken).isEqualTo(token);
	}

	@Test
	@DisplayName("쿠키가 없으면 null을 반환한다")
	void resolveAccessToken_NoCookies_ReturnsNull() {
		// given
		given(request.getCookies()).willReturn(null);

		// when
		String accessToken = tokenProvider.resolveAccessToken(request);

		// then
		assertThat(accessToken).isNull();
	}

	@Test
	@DisplayName("Access Token 쿠키가 없으면 null을 반환한다")
	void resolveAccessToken_NoAccessTokenCookie_ReturnsNull() {
		// given
		Cookie otherCookie = new Cookie("Non-Access-Token", "value");
		Cookie[] cookies = {otherCookie};
		given(request.getCookies()).willReturn(cookies);

		// when
		String accessToken = tokenProvider.resolveAccessToken(request);

		// then
		assertThat(accessToken).isNull();
	}

	@Test
	@DisplayName("Access Token으로 Authentication 객체를 생성한다")
	void getAuthenticationByAccessToken_ValidToken_ReturnsAuthentication() {
		// given
		String userId = "1";
		String accessToken = tokenProvider.createAccessToken(userId, Role.ADMIN.getRole());

		// when
		Authentication authentication = tokenProvider.getAuthenticationByAccessToken(accessToken);

		// then
		assertThat(authentication).isNotNull();

		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		assertThat(principal.getUserId()).isEqualTo(1L);
		assertThat(principal.getUsername()).isEqualTo("1");
		assertThat(principal.getRole()).isEqualTo(Role.ADMIN.getRole());
		assertThat(authentication.getAuthorities()).isNotEmpty();

		// 권한 확인
		assertThat(authentication.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals(Role.ADMIN.getRole()))).isTrue();
	}

	@Test
	@DisplayName("유효한 토큰에서 Claims를 추출한다")
	void getClaimsFromToken_ValidToken_ReturnsClaims() {
		// given
		String token = tokenProvider.createAccessToken(USER_ID, Role.USER.getRole());

		// when
		Claims claims = tokenProvider.getClaimsFromToken(token);

		// then
		assertThat(claims).isNotNull();
		assertThat(claims.getSubject()).isEqualTo(USER_ID);
		assertThat(claims.get("role", String.class)).isEqualTo(Role.USER.getRole());
		assertThat(claims.getIssuedAt()).isNotNull();
		assertThat(claims.getExpiration()).isNotNull();
	}

	@Test
	@DisplayName("만료된 토큰에서도 Claims를 추출한다")
	void getClaimsFromToken_ExpiredToken_ReturnsClaims() {
		// given
		SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		String expiredToken = Jwts.builder()
			.subject(USER_ID)
			.claim("role", Role.USER.getRole())
			.issuedAt(new Date(System.currentTimeMillis() - 10000))
			.expiration(new Date(System.currentTimeMillis() - 1000))
			.signWith(secretKey)
			.compact();

		// when
		Claims claims = tokenProvider.getClaimsFromToken(expiredToken);

		// then
		assertThat(claims).isNotNull();
		assertThat(claims.getSubject()).isEqualTo(USER_ID);
		assertThat(claims.get("role", String.class)).isEqualTo(Role.USER.getRole());
	}

	@Test
	@DisplayName("잘못된 토큰에서 Claims 추출 시 예외가 발생한다")
	void getClaimsFromToken_InvalidToken_ThrowsException() {
		// given
		String wrongToken = "wrongToken";

		// when & then
		assertThatThrownBy(() -> tokenProvider.getClaimsFromToken(wrongToken))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Refresh Token을 삭제한다")
	void deleteRefreshToken_Success() {
		// when
		tokenProvider.deleteRefreshToken(USER_ID);

		// then
		verify(redisTemplate).delete(USER_ID);
	}

	@Test
	@DisplayName("ADMIN 권한을 가진 토큰을 올바르게 생성하고 파싱한다")
	void createAndParseAdminToken_Success() {
		// given
		String adminUserId = "2";

		// when
		String accessToken = tokenProvider.createAccessToken(adminUserId, Role.ADMIN.getRole());
		Authentication authentication = tokenProvider.getAuthenticationByAccessToken(accessToken);

		// then
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		assertThat(principal.getRole()).isEqualTo(Role.ADMIN.getRole());
		assertThat(authentication.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))).isTrue();
	}
}