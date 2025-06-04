package doldol_server.doldol.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "UpdateUserResponse: 개인정보 수정 응답 Dto")
public record UpdateUserInfoResponse(
	@Schema(description = "변경된 이름", example = "김돌돌")
	String name,
	@Schema(description = "변경된 비밀번호", example = "doldol1234!")
	String password
) {
}
