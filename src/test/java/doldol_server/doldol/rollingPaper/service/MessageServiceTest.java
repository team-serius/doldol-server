package doldol_server.doldol.rollingPaper.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
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
			.openDate(LocalDate.now().plusDays(1))
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
	@DisplayName("받은 메시지 목록 조회 - 오픈 후 성공")
	void getMessages_Receive_AfterOpen_Success() {
		// given
		LocalDate pastOpenDate = LocalDate.now().minusDays(1); // 과거 날짜 (이미 오픈됨)
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, pastOpenDate, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1); // 실제 카운트 반환
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
		LocalDate futureOpenDate = LocalDate.now().plusDays(1); // 미래 날짜 (아직 오픈 안됨)
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, futureOpenDate, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1); // 현재 페이지 메시지 수
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).messageType()).isEqualTo(MessageType.RECEIVE);
		assertThat(result.message().getData().get(0).content()).isNull(); // content 숨김
		assertThat(result.message().getData().get(0).name()).isEqualTo("김철수"); // 다른 정보는 유지
		assertThat(result.message().getData().get(0).fontStyle()).isEqualTo("Arial");
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 오픈 후 성공")
	void getMessages_Send_AfterOpen_Success() {
		// given
		LocalDate pastOpenDate = LocalDate.now().minusDays(1);
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paper.getId(), MessageType.SEND, pastOpenDate, request, fromUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1);
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).messageType()).isEqualTo(MessageType.SEND);
		assertThat(result.message().getData().get(0).content()).isEqualTo("테스트 메시지");
		assertThat(result.message().getData().get(0).name()).isEqualTo("김철수");
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 오픈 전 content 숨김")
	void getMessages_Send_BeforeOpen_ContentHidden() {
		// given
		LocalDate futureOpenDate = LocalDate.now().plusDays(1);
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paper.getId(), MessageType.SEND, futureOpenDate, request, fromUser.getId()
		);

		// then
		assertThat(result.messageCount()).isEqualTo(1);
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).messageType()).isEqualTo(MessageType.SEND);
		assertThat(result.message().getData().get(0).content()).isNull(); // content 숨김
		assertThat(result.message().getData().get(0).name()).isEqualTo("김철수");
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
			.paper(paper)
			.build();
		messageRepository.save(newMessage);

		LocalDate pastOpenDate = LocalDate.now().minusDays(1);
		CursorPageRequest request = new CursorPageRequest(newMessage.getId(), 10);

		// when
		MessageListResponse result = messageService.getMessages(
			paper.getId(), MessageType.RECEIVE, pastOpenDate, request, toUser.getId()
		);

		// then
		assertThat(result.message().getData()).hasSize(1);
		assertThat(result.message().getData().get(0).content()).isEqualTo("테스트 메시지");
	}

	@Test
	@DisplayName("메시지 목록 조회 - 빈 결과")
	void getMessages_EmptyResult() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);
		Long nonExistentPaperId = 999L;
		LocalDate pastOpenDate = LocalDate.now().minusDays(1);

		// when
		MessageListResponse result = messageService.getMessages(
			nonExistentPaperId, MessageType.RECEIVE, pastOpenDate, request, toUser.getId()
		);

		// then
		assertThat(result.messageCount()).isZero();
		assertThat(result.message().getData()).isEmpty();
		assertThat(result.message().isHasNext()).isFalse();
		assertThat(result.message().getNextCursor()).isNull();
	}
}
