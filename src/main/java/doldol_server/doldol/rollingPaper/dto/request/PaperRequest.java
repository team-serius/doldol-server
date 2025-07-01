package doldol_server.doldol.rollingPaper.dto.request;

import java.time.LocalDate;

import doldol_server.doldol.rollingPaper.entity.PaperType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Schema(name = "PaperRequest: 롤링페이퍼 생성 요청 Dto")
public record PaperRequest(
	@NotNull(message = "단체 이름은 필수입니다.")
	@Schema(description = "단체 이름", example = "[KB] IT's Your Life 6기 16회차")
	String name,

	@Schema(description = "단체 설명", example = "KB 16회차 짱짱맨 영원하라.")
	String description,

	@Schema(description = "메세지 공개 날짜", example = "2025-06-26")
	@FutureOrPresent(message = "메세지 공개 날짜는 과거일 수 없습니다.")
	LocalDate openDate,

	@NotNull(message = "롤링페이퍼 타입은 필수입니다.")
	@Schema(description = "롤링페이퍼 타입", example = "GROUP")
	PaperType paperType
) {
}
