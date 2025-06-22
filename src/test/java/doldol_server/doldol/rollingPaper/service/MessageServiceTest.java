package doldol_server.doldol.rollingPaper.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("Message 서비스 통합 테스트")
class MessageServiceTest extends ServiceTest {

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
	private Paper paperOpened;
	private Paper paperNotOpened;
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

		paperOpened = Paper.builder()
			.name("공개된 페이퍼")
			.description("테스트 설명")
			.openDate(LocalDate.now().minusDays(1))
			.invitationCode("ABC123")
			.build();
		paperOpened = paperRepository.save(paperOpened);

		paperNotOpened = Paper.builder()
			.name("미공개 페이퍼")
			.description("테스트 설명")
			.openDate(LocalDate.now().plusDays(1))
			.invitationCode("XYZ789")
			.build();
		paperNotOpened = paperRepository.save(paperNotOpened);

		savedMessage = Message.builder()
			.name("김철수")
			.content("테스트 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paperOpened)
			.build();
		savedMessage = messageRepository.save(savedMessage);
	}

	@Test
	@DisplayName("단일 메시지 조회 - 성공")
	void getMessage_Success() {
		// when
		MessageResponse result = messageService.getMessage(savedMessage.getId(), fromUser.getId());

		// then
		assertThat(result.messageId()).isEqualTo(savedMessage.getId());
		assertThat(result.messageType()).isEqualTo(MessageType.SEND);
		assertThat(result.content()).isEqualTo("테스트 메시지");
		assertThat(result.fontStyle()).isEqualTo("Arial");
		assertThat(result.backgroundColor()).isEqualTo("#FFFFFF");
		assertThat(result.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("단일 메시지 조회 - 존재하지 않는 메시지 ID로 조회시 예외 발생")
	void getMessage_NotFound() {
		// given
		Long nonExistentMessageId = 999L;

		// when & then
		assertThatThrownBy(() -> messageService.getMessage(nonExistentMessageId, fromUser.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessage(MessageErrorCode.MESSAGE_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("단일 메시지 조회 - 권한이 없는 사용자가 조회시 예외 발생")
	void getMessage_Unauthorized() {
		// given
		User otherUser = createAndSaveUser("other", "박민수", "other@test.com", "01011111111", "password789");

		// when & then
		assertThatThrownBy(() -> messageService.getMessage(savedMessage.getId(), otherUser.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessage(MessageErrorCode.MESSAGE_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 오픈 후 성공")
	void getMessages_Receive_AfterOpen_Success() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paperOpened.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1);
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).messageType()).isEqualTo(MessageType.RECEIVE);
		assertThat(result.message().getData().get(0).content()).isEqualTo("테스트 메시지"); // content 표시
		assertThat(result.message().getData().get(0).name()).isEqualTo("김철수");
		assertThat(result.message().getData().get(0).fontStyle()).isEqualTo("Arial");
		assertThat(result.message().getData().get(0).backgroundColor()).isEqualTo("#FFFFFF");
		assertThat(result.message().getData().get(0).isDeleted()).isFalse();
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 오픈 전 content 숨김")
	void getMessages_Receive_BeforeOpen_ContentHidden() {
		// given
		Message messageInNotOpenedPaper = Message.builder()
			.name("김철수")
			.content("숨겨져야 할 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paperNotOpened)
			.build();
		messageRepository.save(messageInNotOpenedPaper);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paperNotOpened.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1);
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).messageType()).isEqualTo(MessageType.RECEIVE);
		assertThat(result.message().getData().get(0).content()).isNull();
		assertThat(result.message().getData().get(0).name()).isEqualTo("김철수");
		assertThat(result.message().getData().get(0).fontStyle()).isEqualTo("Arial");
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
			.from(fromUser)
			.to(toUser)
			.paper(paperOpened)
			.build();
		messageRepository.save(newMessage);

		CursorPageRequest request = new CursorPageRequest(newMessage.getId(), 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paperOpened.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result.message().getData()).hasSize(2);
	}

	@Test
	@DisplayName("메시지 목록 조회 - 존재하지 않는 페이퍼 ID로 조회시 예외 발생")
	void getMessages_PaperNotFound() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);
		Long nonExistentPaperId = 999L;

		// when & then
		assertThatThrownBy(() -> messageService.getMessages(
			nonExistentPaperId, MessageType.RECEIVE, request, toUser.getId()
		))
			.isInstanceOf(CustomException.class)
			.hasMessage(PaperErrorCode.PAPER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("메시지 목록 조회 - 빈 결과")
	void getMessages_EmptyResult() {
		// given
		Paper emptyPaper = Paper.builder()
			.name("빈 페이퍼")
			.description("메시지가 없는 페이퍼")
			.openDate(LocalDate.now().minusDays(1))
			.invitationCode("EMPTY123")
			.build();
		emptyPaper = paperRepository.save(emptyPaper);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			emptyPaper.getId(), MessageType.RECEIVE, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isZero();
		assertThat(result.message().getData()).isEmpty();
		assertThat(result.message().isHasNext()).isFalse();
		assertThat(result.message().getNextCursor()).isNull();
	}
}