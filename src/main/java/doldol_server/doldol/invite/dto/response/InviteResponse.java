package doldol_server.doldol.invite.dto.response;

import doldol_server.doldol.invite.entity.Invite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class InviteResponse {

    @Schema(description = "초대장 ID", example = "1")
    private final Long inviteId;

    @Schema(description = "제목")
    private final String title;

    @Schema(description = "행사 일시")
    private final LocalDateTime eventDateTime;

    @Schema(description = "장소")
    private final String location;

    @Schema(description = "본문 문구")
    private final String content;

    @Schema(description = "보내는 사람")
    private final String sender;

    @Schema(description = "초대장 UUID")
    private final String inviteCode;

    @Schema(description = "초대장 테마")
    private final String theme;

    @Schema(description = "폰트 스타일")
    private final String fontStyle;

    @Schema(description = "댓글 목록")
    private final List<InviteCommentResponse> comments;

    public static InviteResponse from(Invite invite) {
        List<InviteCommentResponse> commentResponses = invite.getComments()
            .stream()
            .map(InviteCommentResponse::from)
            .toList();

        return InviteResponse.builder()
            .inviteId(invite.getInviteId())
            .title(invite.getTitle())
            .eventDateTime(invite.getEventDateTime())
            .location(invite.getLocation())
            .content(invite.getContent())
            .sender(invite.getSender())
            .inviteCode(invite.getInviteCode())
            .theme(invite.getTheme())
            .fontStyle(invite.getFontStyle())
            .comments(commentResponses)
            .build();
    }
}

