package doldol_server.doldol.rollingPaper.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.UpdateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

	private final PaperRepository paperRepository;
	private final MessageRepository messageRepository;
	private final UserService userService;

	public List<MessageResponse> getMessages(Long paperId, MessageType messageType, CursorPageRequest request,
		Long userId) {
		if (messageType == MessageType.RECEIVE) {
			return messageRepository.getReceivedMessages(paperId, userId, request);
		}
		return messageRepository.getSentMessages(paperId, userId, request);
	}

	@Transactional
	public void createMessage(CreateMessageRequest request, Long userId) {

		User fromUser = userService.getById(userId);
		User toUser = userService.getById(request.receiverId());

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

	@Transactional
	public void updateMessage(UpdateMessageRequest request, Long userId) {
		Message message = messageRepository.getMessage(request.messageId(), userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		message.update(request.fontStyle(), request.backgroundColor(), request.content(), request.fromName());
	}

	@Transactional
	public void deleteMessage(DeleteMessageRequest request, Long userId) {
		Message message = messageRepository.getMessage(request.messageId(), userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		message.updateDeleteStatus();
	}
}