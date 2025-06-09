package doldol_server.doldol.user.entity;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
	KAKAO("카카오"),
	GOOGLE("구글"),
	NAVER("네이버");

	private final String displayName;

	public static SocialType getSocialType(String socialTypeStr) {
		if (socialTypeStr == null) {
			throw new RuntimeException("소셜 타입이 null입니다.");
		}

		return Arrays.stream(SocialType.values())
			.filter(type -> type.name().equalsIgnoreCase(socialTypeStr))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("존재하지 않는 소셜 타입입니다."));
	}
}
