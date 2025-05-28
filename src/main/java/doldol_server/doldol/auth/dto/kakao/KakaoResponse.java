package doldol_server.doldol.auth.dto.kakao;

import java.util.HashMap;
import java.util.Map;

import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.user.entity.SocialType;

public class KakaoResponse implements OAuth2Response {

	private final Map<String, Object> attribute;
	private final String providerId;
	private final String email;

	public KakaoResponse(Map<String, Object> attribute) {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attribute.get("kakao_account");

		this.attribute = new HashMap<>();

		this.providerId = ((Long)attribute.get("id")).toString();
		this.email = (String)kakaoAccount.get("email");

		this.attribute.put("providerId", providerId);
		this.attribute.put("email", this.email);
	}

	@Override
	public String getSocialId() {
		return providerId;
	}

	@Override
	public String getEmail() {
		return email;
	}
}