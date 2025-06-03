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

	public boolean existUserInPaper(Long userId, Long paperId) {
		return participantRepository.existsByUserAndPaper(userId, paperId);
	}

	public List<ParticipantResponse> getParticipants(Long paperId, Long userId) {
		Paper paper = paperRepository.findById(paperId).orElseThrow(() -> new CustomException(PAPER_NOT_FOUND));

		if (!existUserInPaper(userId, paper.getId())) {
			throw new CustomException(PARTICIPANT_NOT_FOUND);
		}

		return participantRepository.findAllByPaperAndUserExceptMe(paperId, userId);
	}
}
