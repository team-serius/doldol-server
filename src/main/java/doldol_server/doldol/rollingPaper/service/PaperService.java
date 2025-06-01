package doldol_server.doldol.rollingPaper.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.rollingPaper.dto.request.PaperRequest;
import doldol_server.doldol.rollingPaper.dto.response.CreatePaperResponse;
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

		Paper paper = Paper.builder()
			.name(request.name())
			.description(request.description())
			.openDate(request.openDate())
			.link(createLink())
			.build();
		paperRepository.save(paper);

		participantService.createParticipant(userId, paper);

		return CreatePaperResponse.of(paper);
	}

	private String createLink() {
		return defaultPaperLink + UUID.randomUUID();
	}
}
