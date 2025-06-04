package doldol_server.doldol.rollingPaper.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
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
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("Message 서비스 통합 테스트")
class MessageServiceIntegrationTest extends ServiceTest {

	@Autowired
	private MessageService messageService;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaperRepository paperRepository;

	private User fromUser;
	private User toUser;
	private Paper paper;
	private Message savedMessage;

	private User createAndSaveUser(String loginId, String name, String email, String phone, String password) {
		User user = User.builder()
			.loginId(loginId)
			.name(name)
			.email(email)
			.phone(phone)
			.password(password)
			.build();
		return userRepository.save(user);
	}

	@BeforeEach
	void setUp() {
		fromUser = createAndSaveUser("sender", "김철수", "sender@test.com", "01012345678", "password123");
		toUser = createAndSaveUser("receiver", "이영희", "receiver@test.com", "01087654321", "password456");

		paper = Paper.builder()
			.name("테스트 페이퍼")
			.description("테스트 설명")
			.openDate(LocalDateTime.now().plusDays(1))
			.invitationCode("ABC123")
			.build();
		paper = paperRepository.save(paper);

		savedMessage = Message.builder()
			.name("김철수")
			.content("테스트 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		savedMessage = messageRepository.save(savedMessage);
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 성공")
	void getMessages_Receive_Success() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).messageType()).isEqualTo(MessageType.RECEIVE);
		assertThat(result.get(0).content()).isEqualTo("테스트 메시지");
		assertThat(result.get(0).name()).isEqualTo("김철수");
		assertThat(result.get(0).fontStyle()).isEqualTo("Arial");
		assertThat(result.get(0).backgroundColor()).isEqualTo("#FFFFFF");
		assertThat(result.get(0).isDeleted()).isFalse();
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 성공")
	void getMessages_Send_Success() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageService.getMessages(
			paper.getId(), MessageType.SEND, request, fromUser.getId()
		);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).messageType()).isEqualTo(MessageType.SEND);
		assertThat(result.get(0).content()).isEqualTo("테스트 메시지");
		assertThat(result.get(0).name()).isEqualTo("김철수");
	}

	@Test
	@DisplayName("커서 페이징을 사용한 메시지 목록 조회 - 성공")
	void getMessages_WithCursor_Success() {
		// given
		Message newMessage = Message.builder()
			.name("이영희")
			.content("새로운 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(toUser)
			.to(fromUser)
			.paper(paper)
			.build();
		messageRepository.save(newMessage);

		CursorPageRequest request = new CursorPageRequest(newMessage.getId(), 10);

		// when
		List<MessageResponse> result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).content()).isEqualTo("테스트 메시지");
	}

	@Test
	@DisplayName("메시지 목록 조회 - 빈 결과")
	void getMessages_EmptyResult() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);
		Long nonExistentPaperId = 999L;

		// when
		List<MessageResponse> result = messageService.getMessages(
			nonExistentPaperId, MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("메시지 목록 조회 - 삭제된 메시지 제외")
	void getMessages_ExcludeDeletedMessages() {
		// given
		savedMessage.updateDeleteStatus();
		messageRepository.save(savedMessage);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("메시지 작성 - 성공")
	void createMessage_Success() {
		// given
		CreateMessageRequest request = new CreateMessageRequest(
			paper.getId(), toUser.getId(), "새로운 메시지", "김철수", "Helvetica", "#00FF00"
		);

		// when
		assertDoesNotThrow(() -> messageService.createMessage(request, fromUser.getId()));

		// then
		List<Message> messages = messageRepository.findAll();
		Message createdMessage = messages.stream()
			.filter(m -> m.getContent().equals("새로운 메시지"))
			.findFirst()
			.orElseThrow();

		assertThat(createdMessage.getContent()).isEqualTo("새로운 메시지");
		assertThat(createdMessage.getFontStyle()).isEqualTo("Helvetica");
		assertThat(createdMessage.getBackgroundColor()).isEqualTo("#00FF00");
		assertThat(createdMessage.getName()).isEqualTo("김철수");
		assertThat(createdMessage.getFrom().getId()).isEqualTo(fromUser.getId());
		assertThat(createdMessage.getTo().getId()).isEqualTo(toUser.getId());
		assertThat(createdMessage.getPaper().getId()).isEqualTo(paper.getId());
		assertThat(createdMessage.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("메시지 작성 - 발신자를 찾을 수 없음")
	void createMessage_ThrowsException_FromUserNotFound() {
		// given
		Long nonExistentUserId = 999L;
		CreateMessageRequest request = new CreateMessageRequest(
			paper.getId(), toUser.getId(), "새로운 메시지", "Arial", "#FFFFFF", "김철수"
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.createMessage(request, nonExistentUserId));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 작성 - 수신자를 찾을 수 없음")
	void createMessage_ThrowsException_ToUserNotFound() {
		// given
		Long nonExistentUserId = 999L;
		CreateMessageRequest request = new CreateMessageRequest(
			paper.getId(), nonExistentUserId, "새로운 메시지", "Arial", "#FFFFFF", "김철수"
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.createMessage(request, fromUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 작성 - 페이퍼를 찾을 수 없음")
	void createMessage_ThrowsException_PaperNotFound() {
		// given
		Long nonExistentPaperId = 999L;
		CreateMessageRequest request = new CreateMessageRequest(
			nonExistentPaperId, toUser.getId(), "새로운 메시지", "Arial", "#FFFFFF", "김철수"
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.createMessage(request, fromUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(PaperErrorCode.PAPER_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 수정 - 성공")
	void updateMessage_Success() {
		// given
		UpdateMessageRequest request = new UpdateMessageRequest(
			savedMessage.getId(), "Georgia", "#FF0000", "수정된 내용", "수정된 이름"
		);

		// when
		assertDoesNotThrow(() -> messageService.updateMessage(request, fromUser.getId()));

		// then
		Message updatedMessage = messageRepository.findById(savedMessage.getId()).orElseThrow();
		assertThat(updatedMessage.getContent()).isEqualTo("수정된 내용");
		assertThat(updatedMessage.getFontStyle()).isEqualTo("Georgia");
		assertThat(updatedMessage.getBackgroundColor()).isEqualTo("#FF0000");
		assertThat(updatedMessage.getName()).isEqualTo("수정된 이름");
	}

	@Test
	@DisplayName("메시지 수정 - 메시지를 찾을 수 없음")
	void updateMessage_ThrowsException_MessageNotFound() {
		// given
		Long nonExistentMessageId = 999L;
		UpdateMessageRequest request = new UpdateMessageRequest(
			nonExistentMessageId, "수정된 내용", "Georgia", "#FF0000", "수정된 이름"
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.updateMessage(request, fromUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 수정 - 다른 사용자의 메시지 접근 시도")
	void updateMessage_ThrowsException_UnauthorizedAccess() {
		// given
		User otherUser = createAndSaveUser("other", "다른사용자", "other@test.com", "01011111111", "password789");

		UpdateMessageRequest request = new UpdateMessageRequest(
			savedMessage.getId(), "수정된 내용", "Georgia", "#FF0000", "수정된 이름"
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.updateMessage(request, otherUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 삭제 - 성공")
	void deleteMessage_Success() {
		// given
		DeleteMessageRequest request = new DeleteMessageRequest(savedMessage.getId());

		// when
		assertDoesNotThrow(() -> messageService.deleteMessage(request, fromUser.getId()));

		// then
		Message deletedMessage = messageRepository.findById(savedMessage.getId()).orElseThrow();
		assertThat(deletedMessage.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("메시지 삭제 - 메시지를 찾을 수 없음")
	void deleteMessage_ThrowsException_MessageNotFound() {
		// given
		Long nonExistentMessageId = 999L;
		DeleteMessageRequest request = new DeleteMessageRequest(nonExistentMessageId);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.deleteMessage(request, fromUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}

	@Test
	@DisplayName("메시지 삭제 - 다른 사용자의 메시지 접근 시도")
	void deleteMessage_ThrowsException_UnauthorizedAccess() {
		// given
		User otherUser = createAndSaveUser("other", "다른사용자", "other@test.com", "01011111111", "password789");

		DeleteMessageRequest request = new DeleteMessageRequest(savedMessage.getId());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> messageService.deleteMessage(request, otherUser.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}
}