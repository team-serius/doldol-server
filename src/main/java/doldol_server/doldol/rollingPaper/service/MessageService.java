package doldol_server.doldol.rollingPaper.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.UpdateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
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

	public MessageListResponse getMessages(Long paperId, MessageType messageType, LocalDate openDate,
		CursorPageRequest request, Long userId) {

		boolean isOpened = openDate.isBefore(LocalDate.now());
		boolean isReceiveType = messageType == MessageType.RECEIVE;

		List<MessageResponse> messages = isReceiveType
			? messageRepository.getReceivedMessages(paperId, userId, request)
			: messageRepository.getSentMessages(paperId, userId, request);

		if (!isOpened) {
			messages = messages.stream()
				.map(MessageResponse::withNullContent)
				.toList();
		}

		int totalCount = isReceiveType ? getReceivedMessageCounts(paperId, userId).intValue() :
			getSentMessageCounts(paperId, userId).intValue();
		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(messages, request.size(), MessageResponse::messageId);
		return MessageListResponse.of(totalCount, cursorPage);
	}

	@Transactional
	public void createMessage(CreateMessageRequest request, Long userId) {

		User fromUser = userService.getById(userId);
		User toUser = userService.getById(request.receiverId());

		Paper paper = paperRepository.findById(request.paperId())
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));

		paper.addMessage();

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

		conditionalUpdate(request, message);
	}

	@Transactional
	public void deleteMessage(DeleteMessageRequest request, Long userId) {
		Message message = messageRepository.getMessage(request.messageId(), userId);
		message.getPaper().deleteMessage();

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		message.updateDeleteStatus();
	}

	private Long getReceivedMessageCounts(Long paperId, Long userId) {
		return messageRepository.getReceivedMessagesCount(paperId, userId);
	}

	private Long getSentMessageCounts(Long paperId, Long userId) {
		return messageRepository.getSentdMessagesCount(paperId, userId);
	}

	private static void conditionalUpdate(UpdateMessageRequest request, Message message) {
		if (request.content() != null) {
			message.updateContent(request.content());
		}
		if (request.fontStyle() != null) {
			message.updateFontStyle(request.fontStyle());
		}
		if (request.backgroundColor() != null) {
			message.updateBackgroundColor(request.backgroundColor());
		}
		if (request.fromName() != null) {
			message.updateName(request.fromName());
		}
	}
}
