package doldol_server.doldol.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "UserLoginIdResponse: 사용자 아이디 응답 Dto")
public record UserLoginIdResponse(
	@Schema(description = "유저 로그인 아이디입니다,", example = "doldol")
	String id
) {
}
