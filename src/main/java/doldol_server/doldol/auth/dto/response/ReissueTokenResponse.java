package doldol_server.doldol.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "ReissueTokenResponse: 재발급 토큰 응답 Dto")
public record ReissueTokenResponse(
	@NotNull
	@Schema(description = "JWT Access 토큰입니다.", example = "access-token")
	String accessToken,
	@NotNull
	@Schema(description = "JWT Refresh 토큰입니다.", example = "refresh-token")
	String refreshToken
) {}
