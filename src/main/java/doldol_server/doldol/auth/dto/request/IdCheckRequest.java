package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "IdCheckRequest: 아이디 중복 검증 요청 Dto")
public record IdCheckRequest(
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영어 소문자와 숫자만 사용하여 4~20자리여야 합니다.")
	@Schema(description = "아이디는 입력되어야 합니다. 아이디는 영어 소문자와 숫자만 사용하여 4~20자리입니다", example = "doldol1234")
	String id) {
}