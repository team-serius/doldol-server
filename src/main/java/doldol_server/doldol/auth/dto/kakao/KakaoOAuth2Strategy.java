package doldol_server.doldol.auth.dto.kakao;

import java.util.Map;

import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.user.entity.SocialType;

@Component
public class KakaoOAuth2Strategy implements OAuth2ResponseStrategy {

	@Override
	public String getProviderType() {
		return SocialType.KAKAO.name();
	}

	@Override
	public OAuth2Response createResponse(Map<String, Object> attributes) {
		return new KakaoResponse(attributes);
	}
}