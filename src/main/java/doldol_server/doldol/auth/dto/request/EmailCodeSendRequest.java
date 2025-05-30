package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EmailCodeSendRequest: 이메일 인증 코드 전송 Dto")
public record EmailCodeSendRequest(@NotBlank(message = "이메일은 필수 입력값입니다.")
								   @Email(message = "올바른 이메일 양식을 입력해주세요.")
								   @Schema(description = "이메일", example = "doldol@test.com")
								   String email) {
}
