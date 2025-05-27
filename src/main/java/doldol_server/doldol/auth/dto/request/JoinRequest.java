package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Schema(name = "JoinRequest: 회원가입 요청 Dto")
public record JoinRequest(
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영어 소문자와 숫자만 사용하여 4~20자리여야 합니다.")
	@Schema(description = "아이디는 입력되어야 합니다. 아이디는 영어 소문자와 숫자만 사용하여 4~20자리입니다", example = "doldol1234")
	String loginId,

	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$",
		message = "비밀번호는 8~16자리로 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "비밀번호는 입력되어야 합니다. 비밀번호는 8~16자리 수 입니다. 영문 대소문자, 숫자, 특수문자를 포함합니다.", example = "doldol1234!")
	String password,

	@NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
	@Schema(description = "위의 비밀번호 항목과 동일해야 합니다.", example = "doldol1234!")
	String passwordConfirm,

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
	boolean ageOverFourteen
) {
}
