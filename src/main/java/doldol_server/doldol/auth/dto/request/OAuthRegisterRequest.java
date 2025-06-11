package doldol_server.doldol.auth.dto.request;

import doldol_server.doldol.user.entity.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(name = "OAuthRegisterRequest: 소셜 회원가입 요청 Dto")
public record OAuthRegisterRequest(
	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Schema(description = "이름", example = "김돌돌")
	String name,

	@NotBlank(message = "휴대전화 번호는 필수 입력값입니다.")
	@Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다.")
	@Schema(description = "휴대전화 번호", example = "01012341234")
	String phone,

	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "올바른 이메일 양식을 입력해주세요.")
	@Schema(description = "이메일", example = "doldol@test.com")
	String email,

	@NotBlank(message = "소셜 아이디는 필수 입력값입니다.")
	@Schema(description = "소셜 아이디", example = "1233244124")
	String socialId,

	@NotNull(message = "소셜 타입은 필수 입력값입니다.")
	@Schema(description = "소셜 타입", example = "KAKAO")
	SocialType socialType
) {
}