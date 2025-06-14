package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "ReissueTokenRequest: 재발급 토큰 요청 Dto")
public record ReissueTokenRequest(
	@NotNull
	@Schema(description = "JWT Refresh 토큰입니다.", example = "refresh-token")
	String refreshToken
) {
}
