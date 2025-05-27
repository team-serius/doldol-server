package doldol_server.doldol.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "LoginResponse: 로그인 응답 Dto")
public record LoginResponse(
	@Schema(description = "로그인 성공한 유저의 권한입니다.", example = "ADMIN")
	String role
) {}