package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "UserInfoIdCheckRequest: 아이디 찾기위한 유저 데이터 요청 Dto")
public record UserInfoIdCheckRequest(
	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Pattern(regexp = "^[가-힣]{1,5}$", message = "이름은 한글 1~5글자만 입력 가능합니다.")
	@Schema(description = "이름", example = "김돌돌")
	String name,

	@NotBlank(message = "휴대전화 번호는 필수 입력값입니다.")
	@Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다.")
	@Schema(description = "휴대전화 번호", example = "01012341234")
	String phone,

	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "올바른 이메일 양식을 입력해주세요.")
	@Schema(description = "이메일", example = "doldol@test.com")
	String email
) {
}
