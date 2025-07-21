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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			log.warn("존재하지 않는 메시지 조회 시도: messageId={}, userId={}", messageId, userId);
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		return decryptMessageContent(message);
	}

	public MessageListResponse getMessages(Long paperId, MessageType messageType, CursorPageRequest request,
		Long userId) {

		Paper paper = paperRepository.findById(paperId)
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));

		boolean isOpened = !paper.getOpenDate().isAfter(LocalDate.now());
		boolean isReceiveType = messageType == MessageType.RECEIVE;

		List<MessageResponse> messages = isReceiveType
			? messageRepository.getReceivedMessages(paperId, userId, request)
			: messageRepository.getSentMessages(paperId, userId, request);

		List<MessageResponse> processedMessages;

		if (!isOpened && isReceiveType) {
			processedMessages = messages.stream()
				.map(MessageResponse::withNullContent)
				.toList();
		} else {
			processedMessages = messages.stream()
				.map(this::decryptMessageContent)
				.toList();
		}

		int totalCount = isReceiveType ? getReceivedMessageCounts(paperId, userId).intValue() :
			getSentMessageCounts(paperId, userId).intValue();
		CursorPage<MessageResponse, Long> cursorPage = CursorPage.of(processedMessages, request.size(),
			MessageResponse::messageId);
		return MessageListResponse.of(totalCount, cursorPage);
	}

	@Transactional
	public void createMessage(CreateMessageRequest request, Long userId) {

		User fromUser = userService.getById(userId);
		User toUser = userService.getById(request.receiverId());

		Paper paper = paperRepository.findById(request.paperId())
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));

		validateMessageCount(paper, fromUser, toUser);

		paper.addMessage();

		Message message = Message.builder()
			.to(toUser)
			.from(fromUser)
			.paper(paper)
			.name(request.from())
			.backgroundColor(request.backgroundColor())
			.content(encryptor.encrypt(request.content()))
			.fontStyle(request.fontStyle())
			.build();

		messageRepository.save(message);

		log.info("메시지 작성 완료: messageId={}, paperId={}, 발신자={}, 수신자={}, 글자수={}",
			message.getId(), paper.getId(), userId, request.receiverId(),
			request.content().length());
	}

	@Transactional
	public void updateMessage(UpdateMessageRequest request, Long userId) {
		Message message = messageRepository.getSendMessageEntity(request.messageId(), userId);

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		conditionalUpdate(request, message);
		log.info("메시지 수정 완료: messageId={}, 수정자={}", request.messageId(), userId);
	}

	@Transactional
	public void deleteMessage(DeleteMessageRequest request, Long userId) {
		Message message = messageRepository.getMessageEntity(request.messageId(), userId);
		message.getPaper().deleteMessage();

		if (message == null) {
			throw new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND);
		}

		message.updateDeleteStatus();

		log.info("메시지 삭제 완료: messageId={}, 삭제자={}", request.messageId(), userId);
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
		try {
			String decryptedContent = encryptor.decrypt(messageResponse.content());
			return messageResponse.withDecryptedContent(decryptedContent);
		} catch (Exception e) {
			log.error("메시지 복호화 실패: messageId={}, 오류={}",
				messageResponse.messageId(), e.getMessage(), e);
			return messageResponse.withDecryptedContent("메시지 내용을 불러올 수 없습니다.");
		}
	}

	private void validateMessageCount(Paper paper, User fromUser, User toUser) {
		long messageCount = messageRepository.countByPaperAndFromAndToAndIsDeletedFalse(
			paper, fromUser, toUser);
		if (messageCount >= 5) {
			log.warn("메시지 작성 제한 도달: paperId={}, 발신자={}, 수신자={}, 현재개수={}",
				paper.getId(), fromUser.getId(), toUser.getId(), messageCount);
			throw new CustomException(MessageErrorCode.MESSAGE_LIMIT_EXCEEDED);
		}
	}
}
