package doldol_server.doldol.invite.dto.response;

import doldol_server.doldol.invite.entity.InviteComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InviteCommentResponse {

    @Schema(description = "댓글 ID", example = "11")
    private final Long commentId;

    @Schema(description = "댓글 작성자", example = "돌돌이 친구들")
    private final String author;

    @Schema(description = "댓글 작성자 ID", example = "1")
    private final Long userId;

    @Schema(description = "댓글 작성자 이름", example = "홍길동")
    private final String userName;

    @Schema(description = "댓글 내용", example = "꼭 참석할게요!")
    private final String content;

    @Schema(description = "작성 일시")
    private final LocalDateTime createdAt;

    public static InviteCommentResponse from(InviteComment comment) {
        return InviteCommentResponse.builder()
            .commentId(comment.getCommentId())
            .author(comment.getAuthor())
            .userId(comment.getUser().getId())
            .userName(comment.getUser().getName())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build();
    }
}

