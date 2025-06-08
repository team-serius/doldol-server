package doldol_server.doldol.auth.dto.kakao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.user.entity.SocialType;
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
		kakaoApiClient.withdraw(KAKAO_ADMIN_KEY + adminKey,
			KAKAO_TARGET_TYPE,
			Long.parseLong(socialId));
	}
}