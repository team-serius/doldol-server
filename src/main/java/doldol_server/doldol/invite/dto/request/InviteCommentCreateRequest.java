package doldol_server.doldol.invite.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class InviteCommentCreateRequest {

    @Schema(description = "댓글 작성자", example = "돌돌팬클럽")
    @Size(max = 40, message = "작성자 이름은 40자를 초과할 수 없습니다.")
    private String author;

    @Schema(description = "댓글 내용", example = "그날 꼭 갈게요!")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다.")
    private String content;
}

