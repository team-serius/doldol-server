package doldol_server.doldol.rollingPaper.service;

import static doldol_server.doldol.common.exception.errorCode.PaperErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.request.SortDirection;
import doldol_server.doldol.rollingPaper.dto.request.JoinPaperRequest;
import doldol_server.doldol.rollingPaper.dto.request.PaperRequest;
import doldol_server.doldol.rollingPaper.dto.response.CreatePaperResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperDetailResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperListResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaperService {

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
			.paperType(request.paperType())
			.build();

		paperRepository.save(paper);

		participantService.addUser(userId, paper, true);

		return CreatePaperResponse.of(paper);
	}

	public PaperResponse getInvitation(String invitationCode) {
		PaperResponse paperResponse = paperRepository.findPaperWithUserByInvitationCode(invitationCode);

		if (paperResponse == null) {
			throw new CustomException(PAPER_NOT_FOUND);
		}
		return paperResponse;
	}

	@Transactional
	public void joinPaper(JoinPaperRequest request, Long userId) {
		Paper paper = getByInvitationCode(request.invitationCode());

		participantService.validateJoinable(userId, paper.getId());

		participantService.addUser(userId, paper, false);
	}

	public PaperListResponse getMyRollingPapers(CursorPageRequest request,
		SortDirection sortDirection, Long userId) {
		int totalSize = paperRepository.countByUserId(userId);

		List<PaperResponse> papers = paperRepository.getPapers(userId, request, sortDirection);
		CursorPage<PaperResponse, Long> paperResponseCursorPage = CursorPage.of(papers, request.size(),
			PaperResponse::paperId);
		return PaperListResponse.of(totalSize, paperResponseCursorPage);
	}

	public PaperDetailResponse getPaper(Long paperId, Long userId) {
		Participant participant = participantService.getOneByPaperAndUser(paperId, userId);
		boolean isMaster = participant.isMaster();
		return PaperDetailResponse.of(participant.getPaper(), isMaster);
	}

	private String createInvitationCode() {
		return UUID.randomUUID().toString();
	}

	private Paper getByInvitationCode(String invitationCode) {
		return paperRepository.findByInvitationCode(invitationCode)
			.orElseThrow(() -> new CustomException(PAPER_NOT_FOUND));
	}
}
