package doldol_server.doldol.auth.dto.kakao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.common.exception.OAuth2UnlinkException;
import doldol_server.doldol.common.exception.errorCode.OAuth2ErrorCode;
import doldol_server.doldol.user.entity.SocialType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuth2Strategy implements OAuth2ResponseStrategy {

	private static final String KAKAO_ADMIN_KEY = "KakaoAK ";
	private static final String KAKAO_TARGET_TYPE = "user_id";

	private final KakaoApiClient kakaoApiClient;

	@Value("${admin-key.kakao}")
	private String adminKey;

	@Override
	public String getProviderType() {
		return SocialType.KAKAO.name();
	}

	@Override
	public OAuth2Response createResponse(Map<String, Object> attributes) {
		return new KakaoResponse(attributes);
	}


	@Override
	public void unlink(String socialId) {
		try {
			kakaoApiClient.withdraw(KAKAO_ADMIN_KEY + adminKey,
				KAKAO_TARGET_TYPE,
				Long.parseLong(socialId));
			log.info("카카오 사용자 연결 해제 성공. socialId: {}", socialId);
		} catch (NumberFormatException e) {
			log.error("카카오 연결 해제 중 잘못된 socialId 형식. socialId: {}", socialId, e);
			throw new OAuth2UnlinkException(OAuth2ErrorCode.INVALID_SOCIAL_ID);
		} catch (FeignException.Unauthorized e) {
			log.error("카카오 API 인증 실패. 관리자 키를 확인하세요. socialId: {}", socialId, e);
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_UNAUTHORIZED);
		} catch (FeignException.BadRequest e) {
			log.error("카카오 API 잘못된 요청. 유효하지 않은 socialId 또는 파라미터. socialId: {}", socialId, e);
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_BAD_REQUEST);
		} catch (FeignException e) {
			log.error("카카오 API 호출 실패. 상태코드: {}, socialId: {}", e.status(), socialId, e);
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_API_ERROR);
		} catch (Exception e) {
			log.error("카카오 연결 해제 중 예상치 못한 오류 발생. socialId: {}", socialId, e);
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_UNLINK_FAILED);
		}
	}
}