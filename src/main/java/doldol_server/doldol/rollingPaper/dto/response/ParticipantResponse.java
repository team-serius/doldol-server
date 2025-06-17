package doldol_server.doldol.rollingPaper.dto.response;

import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ParticipantResponse: 롤링페이퍼 참여자 응답 Dto")
public record ParticipantResponse(
	@Schema(description = "참여 아이디", example = "1")
	Long participantId,

	@Schema(description = "참여 유저 아이디", example = "1")
	Long userId,

	@Schema(description = "참여자 이름", example = "김돌돌(1234)")
	String name
) {
	public static ParticipantResponse of(Participant participant) {
		User user = participant.getUser();
		String lastFourDigits = user.getPhone().substring(user.getPhone().length() - 4);
		String displayName = user.getName() + " (" + lastFourDigits + ")";

		return new ParticipantResponse(
			participant.getId(),
			user.getId(),
			displayName
		);
	}
}
