package doldol_server.doldol.rollingPaper.service;

import static doldol_server.doldol.common.exception.errorCode.PaperErrorCode.*;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.rollingPaper.dto.request.JoinPaperRequest;
import doldol_server.doldol.rollingPaper.dto.request.PaperRequest;
import doldol_server.doldol.rollingPaper.dto.response.CreatePaperResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaperService {

	@Value("${paper.default.link}")
	private String defaultPaperLink;

	private final PaperRepository paperRepository;
	private final ParticipantService participantService;

	@Transactional
	public CreatePaperResponse createPaper(PaperRequest request, Long userId) {
		String invitationCode = createInvitationCode();
		Paper paper = Paper.builder()
			.name(request.name())
			.description(request.description())
			.openDate(request.openDate())
			.invitationCode(invitationCode)
			.link(defaultPaperLink + invitationCode)
			.build();
		paperRepository.save(paper);

		participantService.addUser(userId, paper, true);

		return CreatePaperResponse.of(paper);
	}

	public PaperResponse getInvitation(String invitationCode) {
		Paper paper = paperRepository.findByInvitationCode(invitationCode)
			.orElseThrow(() -> new CustomException(PAPER_NOT_FOUND));
		return PaperResponse.of(paper);
	}

	@Transactional
	public void joinPaper(JoinPaperRequest request, Long userId) {
		Paper paper = getByInvitationCode(request.invitationCode());

		if (participantService.existUserInPaper(userId, paper)) {
			throw new CustomException(PARTICIPANT_ALREADY_EXIST);
		}

		participantService.addUser(userId, paper, false);
	}

	public Paper getByInvitationCode(String invitationCode) {
		return paperRepository.findByInvitationCode(invitationCode)
			.orElseThrow(() -> new CustomException(PAPER_NOT_FOUND));
	}

	private String createInvitationCode() {
		return UUID.randomUUID().toString();
	}

}
