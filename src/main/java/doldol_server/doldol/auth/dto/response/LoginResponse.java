package doldol_server.doldol.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "LoginResponse: 로그인 응답 Dto")
public record LoginResponse(
	@Schema(description = "유저 식별 값입니다,", example = "1")
	Long userId,
	@Schema(description = "로그인 성공한 유저의 권한입니다.", example = "ADMIN")
	String role,
	@NotNull
	@Schema(description = "JWT Access 토큰입니다.", example = "access-token")
	String accessToken,
	@NotNull
	@Schema(description = "JWT Refresh 토큰입니다.", example = "refresh-token")
	String refreshToken
) {}