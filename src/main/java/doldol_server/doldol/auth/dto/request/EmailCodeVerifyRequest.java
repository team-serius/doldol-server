package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EmailCodeVerifyRequest: 이메일 인증 코드 검증 요청 Dto")
public record EmailCodeVerifyRequest(@NotBlank(message = "이메일은 필수 입력값입니다.")
									 @Email(message = "올바른 이메일 양식을 입력해주세요.")
									 @Schema(description = "이메일", example = "doldol@test.com")
									 String email,
									 @NotBlank(message = "인증번호는 필수 입력값입니다.")
									 @Schema(description = "인증번호", example = "123456")
									 String code) {
}
