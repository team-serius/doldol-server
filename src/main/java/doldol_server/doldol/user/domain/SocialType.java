package doldol_server.doldol.user.domain;

import java.util.Arrays;

public enum SocialType {
    KAKAO;

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
