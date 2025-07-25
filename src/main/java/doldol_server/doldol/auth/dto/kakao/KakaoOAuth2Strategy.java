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
		} catch (NumberFormatException e) {
			throw new OAuth2UnlinkException(OAuth2ErrorCode.INVALID_SOCIAL_ID);
		} catch (FeignException.Unauthorized e) {
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_UNAUTHORIZED);
		} catch (FeignException.BadRequest e) {
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_BAD_REQUEST);
		} catch (FeignException e) {
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_API_ERROR);
		} catch (Exception e) {
			throw new OAuth2UnlinkException(OAuth2ErrorCode.OAUTH2_UNLINK_FAILED);
		}
	}
}