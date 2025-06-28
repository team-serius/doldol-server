package doldol_server.doldol.rollingPaper.dto.response;

import java.time.LocalDateTime;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(name = "MessageResponse: 메세지 응답 Dto")
public record MessageResponse(
	@NotBlank(message = "메세지 고유 id입니다.")
	@Schema(description = "메세지 고유 id", example = "1")
	Long messageId,

	@NotBlank(message = "수신/발신 여부가 입력되어야 합니다.")
	@Schema(description = "수신/발신 여부", example = "RECEIVE/SENT")
	MessageType messageType,

	@NotBlank(message = "메세지 내용이 입력되어야 합니다.")
	@Schema(description = "메세지 내용", example = "가나다라마바사")
	String content,

	@NotBlank(message = "폰트 스타일이 있어야합니다.")
	@Schema(description = "폰트 스타일", example = "귀여운 글씨체")
	String fontStyle,

	@NotBlank(message = "배경색이 있어야합니다.")
	@Schema(description = "배경색", example = "검은색")
	String backgroundColor,

	@NotBlank(message = "삭제여부가 있어야합니다..")
	@Schema(description = "삭제여부", example = "false")
	boolean isDeleted,

	@NotBlank(message = "보낸 사람 이름이 입력되어야 합니다.")
	@Schema(description = "보낸 사람", example = "돌돌")
	String fromName,

	@NotBlank(message = "받은 사람 이름이 입력되어야 합니다.")
	@Schema(description = "받은 사람", example = "돌돌")
	String toName,

	@NotBlank(message = "생성 날짜가 있어야 합니다.")
	@Schema(description = "생성 날짜", example = "2025-05-26T11:44:30.327959")
	LocalDateTime createdAt,

	@Schema(description = "수정 날짜", example = "2025-05-27T11:44:30.327959")
	LocalDateTime updatedAt
) {
	public static MessageResponse of(Message message, String fromName, String toName, MessageType messageType) {
		return MessageResponse.builder()
			.messageId(message.getId())
			.messageType(messageType)
			.content(message.getContent())
			.fontStyle(message.getFontStyle())
			.backgroundColor(message.getBackgroundColor())
			.isDeleted(message.isDeleted())
			.fromName(fromName)
			.toName(toName)
			.createdAt(message.getCreatedAt())
			.updatedAt(message.getUpdatedAt())
			.build();
	}

	public MessageResponse withNullContent() {
		return MessageResponse.builder()
			.messageId(this.messageId)
			.messageType(this.messageType)
			.content(null)
			.fontStyle(this.fontStyle)
			.backgroundColor(this.backgroundColor)
			.isDeleted(this.isDeleted)
			.fromName(this.fromName)
			.toName(this.toName)
			.createdAt(this.createdAt)
			.updatedAt(this.updatedAt)
			.build();
	}

	public MessageResponse withDecryptedContent(String decryptedContent) {
		return MessageResponse.builder()
			.messageId(this.messageId)
			.messageType(this.messageType)
			.content(decryptedContent)
			.fontStyle(this.fontStyle)
			.backgroundColor(this.backgroundColor)
			.isDeleted(this.isDeleted)
			.fromName(this.fromName)
			.toName(this.toName)
			.createdAt(this.createdAt)
			.updatedAt(this.updatedAt)
			.build();
	}
}
