package doldol_server.doldol.user.dto.response;

import doldol_server.doldol.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "MyInfo Response: 사용자 본인 정보 응답 Dto")
public record UserResponse(
	@Schema(description = "이름", example = "김돌돌")
	String name,

	@Schema(description = "휴대전화 번호", example = "01012341234")
	String phone,

	@Schema(description = "이메일", example = "doldol@test.com")
	String email,

	@Schema(description = "소셜 아이디", example = "1233244124")
	String socialId,

	@Schema(description = "소셜 타입", example = "kakao")
	String socialType

) {
	public static UserResponse of(User user) {
		if (user.getSocialId() == null || user.getSocialType() == null) {
			return new UserResponse(user.getName(), user.getPhone(), user.getEmail(), null, null);
		}
		return new UserResponse(user.getName(), user.getPhone(), user.getEmail(),
			user.getSocialId(), user.getSocialType().name().toLowerCase());
	}
}
