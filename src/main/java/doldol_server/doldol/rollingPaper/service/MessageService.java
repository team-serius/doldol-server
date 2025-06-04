package doldol_server.doldol.rollingPaper.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

	private final PaperRepository paperRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createMessage(CreateMessageRequest request, Long userId) {

		User fromUser = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

		User toUser = userRepository.findById(request.receiverId())
			.orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

		Paper paper = paperRepository.findById(request.paperId())
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));

		Message message = Message.builder()
			.to(toUser)
			.from(fromUser)
			.paper(paper)
			.name(request.from())
			.backgroundColor(request.backgroundColor())
			.content(request.content())
			.fontStyle(request.fontStyle())
			.build();

		messageRepository.save(message);
	}
}
