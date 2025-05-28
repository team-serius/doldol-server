package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OAuthTempJoinRequest(
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

	@AssertTrue(message = "이용약관에 동의해야 합니다.")
	@Schema(description = "이용약관 동의", example = "true")
	boolean termsAgreed,

	@AssertTrue(message = "개인정보 수집에 동의해야 합니다.")
	@Schema(description = "개인정보 수집 동의", example = "true")
	boolean privacyAgreed,

	@AssertTrue(message = "만 14세 이상이어야 합니다.")
	@Schema(description = "만 14세 이상 여부", example = "true")
	boolean ageOverFourteen,

	@NotBlank(message = "소셜 아이디는 필수 입력값입니다.")
	@Schema(description = "소셜 아이디", example = "1233244124")
	String socialId,

	@NotBlank(message = "소셜 타입은 필수 입력값입니다.")
	@Schema(description = "소셜 타입", example = "kakao")
	String socialType
) {
}