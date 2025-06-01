package doldol_server.doldol.rollingPaper.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public void createParticipant(Long userId, Paper paper) {
		Participant master = Participant.builder()
			.user(userService.getById(userId))
			.paper(paper)
			.isMaster(true)
			.build();

		participantRepository.save(master);
	}
}
