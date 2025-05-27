package doldol_server.doldol.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "LoginResponse: 로그인 응답 Dto")
public class LoginResponse {

	@Schema(description = "로그인 성공한 유저의 권한입니다.", example = "ADMIN")
	private String role;

	@Builder
	public LoginResponse(String role) {
		this.role = role;
	}
}
