package doldol_server.doldol.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "LoginResDto: 로그인 응답 Dto")
public class LoginResDto {

	@Schema(description = "로그인 성공한 유저의 권한입니다.", example = "ADMIN")
	private String role;

	@Builder
	public LoginResDto(String role) {
		this.role = role;
	}
}
