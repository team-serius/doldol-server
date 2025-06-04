package doldol_server.doldol.rollingPaper.service;

import static doldol_server.doldol.common.exception.errorCode.PaperErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.rollingPaper.repository.ParticipantRepository;
import doldol_server.doldol.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantRepository participantRepository;
	private final UserService userService;
	private final PaperRepository paperRepository;

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

	public void validParticipant(Long userId, Long paperId) {
		List<Participant> participants = participantRepository.findByIdWithUser(paperId);

		existUser(userId, participants);
	}

	public List<ParticipantResponse> getParticipants(Long paperId, Long userId) {
		List<Participant> participants = participantRepository.findByIdWithUser(paperId);

		existParticipant(participants);
		existUser(userId, participants);

		return participants.stream()
			.filter(participant -> participant.getUser().getId() != userId)
			.map(ParticipantResponse::of)
			.toList();
	}

	private void existParticipant(List<Participant> participants) {
		if (participants.isEmpty()) {
			throw new CustomException(PAPER_NOT_FOUND);
		}
	}

	private void existUser(Long userId, List<Participant> participants) {
		if (participants.stream().noneMatch(participant -> participant.getUser().getId() == userId)) {
			throw new CustomException(PARTICIPANT_NOT_FOUND);
		}
	}
}
