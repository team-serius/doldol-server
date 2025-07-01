package doldol_server.doldol.rollingPaper.service;

import java.time.LocalDate;
import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.PaperType;
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
	private final StringEncryptor encryptor;

	public MessageResponse getMessage(Long messageId, Long userId) {
		MessageResponse message = messageRepository.getMessage(messageId, userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		return decryptMessageContent(message);
	}

	public MessageListResponse getMessages(Long paperId, MessageType messageType, CursorPageRequest request,
		Long userId) {

		Paper paper = getPaperById(paperId);

		if (paper.getPaperType() == PaperType.INDIVIDUAL) {
			return getIndividualMessages(paperId, request, userId, paper);
		} else {
			return getGroupMessages(paperId, messageType, request, userId, paper);
		}
	}

	@Transactional
	public void createMessage(CreateMessageRequest request, PaperType paperType, Long userId) {
		Paper paper = getPaperById(request.paperId());

		if (paperType == PaperType.INDIVIDUAL) {
			createIndividualMessage(request, paper);
		} else {
			createGroupMessage(request, paper, userId);
		}
	}

	@Transactional
	public void updateMessage(UpdateMessageRequest request, Long userId) {
		Message message = messageRepository.getSendMessageEntity(request.messageId(), userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		conditionalUpdate(request, message);
	}

	@Transactional
	public void deleteMessage(DeleteMessageRequest request, Long userId) {
		Message message = messageRepository.getMessageEntity(request.messageId(), userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		message.getPaper().deleteMessage();
		message.updateDeleteStatus();
	}

	private Paper getPaperById(Long paperId) {
		return paperRepository.findById(paperId)
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));
	}

	private MessageListResponse getIndividualMessages(Long paperId, CursorPageRequest request, Long userId, Paper paper) {
		List<MessageResponse> messages = messageRepository.getIndividualMessages(paperId, userId, request);

		List<MessageResponse> processedMessages = messages.stream()
			.map(this::decryptMessageContent)
			.toList();

		int totalCount = getTotalIndividualMessageCount(paperId);
		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(processedMessages, request.size(),
			MessageResponse::messageId);
		return MessageListResponse.of(totalCount, cursorPage);
	}

	private MessageListResponse getGroupMessages(Long paperId, MessageType messageType,
		CursorPageRequest request, Long userId, Paper paper) {
		boolean isOpened = !paper.getOpenDate().isAfter(LocalDate.now());
		boolean isReceiveType = messageType == MessageType.RECEIVE;

		List<MessageResponse> messages = isReceiveType
			? messageRepository.getReceivedMessages(paperId, userId, request)
			: messageRepository.getSentMessages(paperId, userId, request);

		List<MessageResponse> processedMessages = processMessages(messages, isOpened, isReceiveType);

		int totalCount = getTotalMessageCount(paperId, userId, isReceiveType);
		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(processedMessages, request.size(),
			MessageResponse::messageId);
		return MessageListResponse.of(totalCount, cursorPage);
	}

	private List<MessageResponse> processMessages(List<MessageResponse> messages, boolean isOpened, boolean isReceiveType) {
		if (!isOpened && isReceiveType) {
			return messages.stream()
				.map(MessageResponse::withNullContent)
				.toList();
		} else {
			return messages.stream()
				.map(this::decryptMessageContent)
				.toList();
		}
	}

	private int getTotalMessageCount(Long paperId, Long userId, boolean isReceiveType) {
		return isReceiveType
			? getReceivedMessageCounts(paperId, userId).intValue()
			: getSentMessageCounts(paperId, userId).intValue();
	}

	private int getTotalIndividualMessageCount(Long paperId) {
		return messageRepository.getIndividualMessagesCount(paperId).intValue();
	}

	private void createIndividualMessage(CreateMessageRequest request, Paper paper) {
		paper.addMessage();
		Message message = buildIndividualMessage(request, paper);
		messageRepository.save(message);
	}

	private void createGroupMessage(CreateMessageRequest request, Paper paper, Long userId) {
		User toUser = userService.getById(request.receiverId());
		User fromUser = userService.getById(userId);

		validateMessageNotDuplicated(paper, fromUser, toUser);

		paper.addMessage();
		Message message = buildGroupMessage(request, paper, fromUser, toUser);
		messageRepository.save(message);
	}

	private Message buildIndividualMessage(CreateMessageRequest request, Paper paper) {
		return Message.builder()
			.to(null)
			.from(null)
			.paper(paper)
			.name(request.from())
			.backgroundColor(request.backgroundColor())
			.content(encryptor.encrypt(request.content()))
			.fontStyle(request.fontStyle())
			.build();
	}

	private Message buildGroupMessage(CreateMessageRequest request, Paper paper, User fromUser, User toUser) {
		return Message.builder()
			.to(toUser)
			.from(fromUser)
			.paper(paper)
			.name(request.from())
			.backgroundColor(request.backgroundColor())
			.content(encryptor.encrypt(request.content()))
			.fontStyle(request.fontStyle())
			.build();
	}

	private void validateMessageNotDuplicated(Paper paper, User fromUser, User toUser) {
		long messageCount = messageRepository.countByPaperAndFromAndToAndIsDeletedFalse(
			paper, fromUser, toUser);

		if (messageCount > 5) {
			throw new CustomException(MessageErrorCode.MESSAGE_LIMIT_EXCEEDED);
		}
	}

	private Long getReceivedMessageCounts(Long paperId, Long userId) {
		return messageRepository.getReceivedMessagesCount(paperId, userId);
	}

	private Long getSentMessageCounts(Long paperId, Long userId) {
		return messageRepository.getSentdMessagesCount(paperId, userId);
	}

	private void conditionalUpdate(UpdateMessageRequest request, Message message) {
		if (request.content() != null) {
			message.updateContent(encryptor.encrypt(request.content()));
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

	private MessageResponse decryptMessageContent(MessageResponse messageResponse) {
		String decryptedContent = encryptor.decrypt(messageResponse.content());
		return messageResponse.withDecryptedContent(decryptedContent);
	}
}