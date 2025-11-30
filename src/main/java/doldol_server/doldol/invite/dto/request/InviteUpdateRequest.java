package doldol_server.doldol.invite.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InviteUpdateRequest {

    private static final String TITLE_MESSAGE = "제목은 1자 이상 40자 이하여야 합니다.";

    @Schema(description = "초대장 제목", example = "돌돌이의 홈파티")
    @NotBlank(message = TITLE_MESSAGE)
    @Size(max = 40, message = TITLE_MESSAGE)
    private String title;

    @Schema(description = "행사 일시", example = "2025-12-31T18:30:00")
    @NotNull(message = "일시는 필수입니다.")
    private LocalDateTime eventDateTime;

    @Schema(description = "행사 장소", example = "서울시 강남구 역삼동 123-45")
    @NotBlank(message = "장소는 필수입니다.")
    @Size(max = 120, message = "장소는 120자를 초과할 수 없습니다.")
    private String location;

    @Schema(description = "장소 링크", example = "https://maps.google.com/?q=서울시+강남구")
    private String locationLink;

    @Schema(description = "초대장 본문 문구", example = "함께 모여 즐거운 시간을 보내요!")
    @NotBlank(message = "문구는 필수입니다.")
    private String content;

    @Schema(description = "보내는 사람 또는 단체 명", example = "돌돌팀")
    @NotBlank(message = "보내는 사람은 필수입니다.")
    @Size(max = 60, message = "보내는 사람은 60자를 초과할 수 없습니다.")
    private String sender;

    @Schema(description = "초대장 테마", example = "retro")
    @Size(max = 30, message = "테마는 30자를 초과할 수 없습니다.")
    private String theme;

    @Schema(description = "폰트 스타일", example = "Arial")
    @NotBlank(message = "폰트 스타일은 필수입니다.")
    private String fontStyle;

}

