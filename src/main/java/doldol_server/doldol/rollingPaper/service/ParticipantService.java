package doldol_server.doldol.rollingPaper.service;

import static doldol_server.doldol.common.exception.errorCode.PaperErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.rollingPaper.dto.request.GetParticipantsRequest;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantsCursorResponse;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.rollingPaper.repository.ParticipantRepository;
import doldol_server.doldol.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantRepository participantRepository;
	private final UserService userService;

	@Transactional
	public void addUser(Long userId, Paper paper, boolean isMaster) {
		Participant participant = Participant.builder()
			.user(userService.getById(userId))
			.paper(paper)
			.isMaster(isMaster)
			.build();
		paper.addParticipant();

		participantRepository.save(participant);
	}

	public CursorPage<ParticipantResponse, ParticipantsCursorResponse> getParticipants(Long paperId,
		GetParticipantsRequest request, Long userId) {
		existUser(userId, paperId);

		List<ParticipantResponse> participants = participantRepository.getParticipants(paperId, request, userId);
		if (participants.isEmpty()) {
			throw new CustomException(PAPER_NOT_FOUND);
		}

		List<ParticipantResponse> response = participants.stream()
			.filter(resp -> !resp.userId().equals(userId))
			.toList();


		return CursorPage.of(response, request.size(),
			last -> new ParticipantsCursorResponse(formatName(last.name()), last.userId()));
	}

	public Participant getOneByPaperAndUser(Long paperId, Long userId) {
		List<Participant> participants = participantRepository.findByPaperIdWithUser(paperId);
		existPaper(participants);

		return participants.stream()
			.filter(participant -> participant.getUser().getId().equals(userId))
			.findFirst().orElseThrow(() -> new CustomException(PARTICIPANT_NOT_FOUND));
	}

	public void validateJoinable(Long userId, Long paperId) {
		List<Participant> participants = participantRepository.findByPaperIdWithUser(paperId);
		existPaper(participants);
		alreadyExistUser(userId, participants);
	}

	private void alreadyExistUser(Long userId, List<Participant> participants) {
		if (participants.stream().anyMatch(participant -> participant.getUser().getId().equals(userId))) {
			throw new CustomException(PARTICIPANT_ALREADY_EXIST);
		}
	}

	private void existPaper(List<Participant> participants) {
		if (participants.isEmpty()) {
			throw new CustomException(PAPER_NOT_FOUND);
		}
	}

	private void existUser(Long userId, Long paperId) {
		if(!participantRepository.existsByPaperIdAndUserId(paperId, userId)) {
			throw new CustomException(PARTICIPANT_NOT_FOUND);
		}
	}

	private String formatName(String name) {
		return name.substring(0, name.indexOf("(")).trim();
	}
}
