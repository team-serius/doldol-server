package doldol_server.doldol.order.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "OrderRequest: 주문 요청 Dto")
public record OrderRequest(
	@NotNull(message = "롤링페이퍼 ID는 필수입니다.")
	@Schema(description = "롤링페이퍼 ID", example = "1")
	Long paperId,

	@NotNull(message = "메시지 ID 목록은 필수입니다.")
	@Size(min = 1, max = 10, message = "메시지는 1개 이상 10개 이하여야 합니다.")
	@Schema(description = "메시지 ID 목록", example = "[1, 2, 3]")
	List<Long> messageIds,

	@NotNull(message = "주문 갯수는 필수입니다.")
	@Min(value = 1, message = "갯수는 1 이상이어야 합니다.")
	@Schema(description = "주문 갯수", example = "1")
	Long count
) {
}
