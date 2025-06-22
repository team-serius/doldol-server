package doldol_server.doldol.rollingPaper.repository.custom;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.common.RepositoryTest;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("MessageRepositoryCustomImpl 테스트")
class MessageRepositoryCustomImplTest extends RepositoryTest {

	@Autowired
	private JPAQueryFactory queryFactory;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaperRepository paperRepository;

	private MessageRepositoryCustomImpl messageRepositoryCustom;

	private User fromUser;
	private User toUser;
	private User otherUser;
	private Paper paper;
	private Paper otherPaper;

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
		messageRepositoryCustom = new MessageRepositoryCustomImpl(queryFactory);

		fromUser = createAndSaveUser("sender", "김철수", "sender@test.com", "01012345678", "password123");
		toUser = createAndSaveUser("receiver", "이영희", "receiver@test.com", "01087654321", "password456");
		otherUser = createAndSaveUser("other", "박민수", "other@test.com", "01011111111", "password789");

		paper = Paper.builder()
			.name("테스트 페이퍼")
			.description("테스트 설명")
			.openDate(LocalDate.now().plusDays(1))
			.invitationCode("ABC123")
			.build();
		paper = paperRepository.save(paper);

		otherPaper = Paper.builder()
			.name("다른 페이퍼")
			.description("다른 설명")
			.openDate(LocalDate.now().plusDays(2))
			.invitationCode("XYZ789")
			.build();
		otherPaper = paperRepository.save(otherPaper);
	}

	@Test
	@DisplayName("메시지 조회 - 발신자로 조회 성공")
	void getMessage_FromUser_Success() {
		// given
		Message savedMessage = Message.builder()
			.name("김철수")
			.content("테스트 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		savedMessage = messageRepository.save(savedMessage);

		// when
		MessageResponse result = messageRepositoryCustom.getMessage(savedMessage.getId(), fromUser.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.messageId()).isEqualTo(savedMessage.getId());
		assertThat(result.content()).isEqualTo("테스트 메시지");
	}

	@Test
	@DisplayName("메시지 조회 - 수신자로 조회 성공")
	void getMessage_ToUser_Success() {
		// given
		Message savedMessage = Message.builder()
			.name("김철수")
			.content("테스트 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		savedMessage = messageRepository.save(savedMessage);

		// when
		MessageResponse result = messageRepositoryCustom.getMessage(savedMessage.getId(), fromUser.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.messageId()).isEqualTo(savedMessage.getId());
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 성공")
	void getReceivedMessages_Success() {
		// given
		Message message1 = Message.builder()
			.name("김철수")
			.content("첫 번째 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(message1);

		Message message2 = Message.builder()
			.name("박민수")
			.content("두 번째 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(otherUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(message2);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).messageType()).isEqualTo(MessageType.RECEIVE);
		assertThat(result.get(1).messageType()).isEqualTo(MessageType.RECEIVE);

		assertThat(result.get(0).messageId()).isGreaterThan(result.get(1).messageId());
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 커서 페이징")
	void getReceivedMessages_WithCursor_Success() {
		// given
		Message message1 = Message.builder()
			.name("김철수")
			.content("첫 번째 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		message1 = messageRepository.save(message1);

		Message message2 = Message.builder()
			.name("박민수")
			.content("두 번째 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(otherUser)
			.to(toUser)
			.paper(paper)
			.build();
		message2 = messageRepository.save(message2);

		CursorPageRequest request = new CursorPageRequest(message2.getId(), 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).messageId()).isEqualTo(message1.getId());
		assertThat(result.get(0).content()).isEqualTo("첫 번째 메시지");
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 삭제된 메시지 제외")
	void getReceivedMessages_ExcludeDeletedMessages() {
		// given
		Message normalMessage = Message.builder()
			.name("김철수")
			.content("정상 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(normalMessage);

		Message deletedMessage = Message.builder()
			.name("박민수")
			.content("삭제된 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(otherUser)
			.to(toUser)
			.paper(paper)
			.build();
		deletedMessage.updateDeleteStatus();
		messageRepository.save(deletedMessage);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).content()).isEqualTo("정상 메시지");
		assertThat(result.get(0).isDeleted()).isFalse();
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 다른 페이퍼의 메시지 제외")
	void getReceivedMessages_ExcludeOtherPaperMessages() {
		// given
		Message messageInTargetPaper = Message.builder()
			.name("김철수")
			.content("대상 페이퍼 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(messageInTargetPaper);

		Message messageInOtherPaper = Message.builder()
			.name("박민수")
			.content("다른 페이퍼 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(otherUser)
			.to(toUser)
			.paper(otherPaper)
			.build();
		messageRepository.save(messageInOtherPaper);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).content()).isEqualTo("대상 페이퍼 메시지");
	}

	@Test
	@DisplayName("받은 메시지 목록 조회 - 빈 결과")
	void getReceivedMessages_EmptyResult() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 성공")
	void getSentMessages_Success() {
		// given
		Message message1 = Message.builder()
			.name("김철수")
			.content("첫 번째 보낸 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(message1);

		Message message2 = Message.builder()
			.name("김철수")
			.content("두 번째 보낸 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(fromUser)
			.to(otherUser)
			.paper(paper)
			.build();
		messageRepository.save(message2);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getSentMessages(paper.getId(), fromUser.getId(), request);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).messageType()).isEqualTo(MessageType.SEND);
		assertThat(result.get(1).messageType()).isEqualTo(MessageType.SEND);

		assertThat(result.get(0).messageId()).isGreaterThan(result.get(1).messageId());
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 커서 페이징")
	void getSentMessages_WithCursor_Success() {
		// given
		Message message1 = Message.builder()
			.name("김철수")
			.content("첫 번째 보낸 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		message1 = messageRepository.save(message1);

		Message message2 = Message.builder()
			.name("김철수")
			.content("두 번째 보낸 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(fromUser)
			.to(otherUser)
			.paper(paper)
			.build();
		message2 = messageRepository.save(message2);

		CursorPageRequest request = new CursorPageRequest(message2.getId(), 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getSentMessages(paper.getId(), fromUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).messageId()).isEqualTo(message1.getId());
		assertThat(result.get(0).content()).isEqualTo("첫 번째 보낸 메시지");
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 삭제된 메시지 제외")
	void getSentMessages_ExcludeDeletedMessages() {
		// given
		Message normalMessage = Message.builder()
			.name("김철수")
			.content("정상 보낸 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(normalMessage);

		Message deletedMessage = Message.builder()
			.name("김철수")
			.content("삭제된 보낸 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(fromUser)
			.to(otherUser)
			.paper(paper)
			.build();
		deletedMessage.updateDeleteStatus();
		messageRepository.save(deletedMessage);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getSentMessages(paper.getId(), fromUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).content()).isEqualTo("정상 보낸 메시지");
		assertThat(result.get(0).isDeleted()).isFalse();
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 다른 사용자가 보낸 메시지 제외")
	void getSentMessages_ExcludeOtherUserMessages() {
		// given
		Message myMessage = Message.builder()
			.name("김철수")
			.content("내가 보낸 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(myMessage);

		Message otherUserMessage = Message.builder()
			.name("박민수")
			.content("다른 사용자가 보낸 메시지")
			.fontStyle("Georgia")
			.backgroundColor("#F0F0F0")
			.from(otherUser)
			.to(toUser)
			.paper(paper)
			.build();
		messageRepository.save(otherUserMessage);

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getSentMessages(paper.getId(), fromUser.getId(), request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).content()).isEqualTo("내가 보낸 메시지");
	}

	@Test
	@DisplayName("보낸 메시지 목록 조회 - 빈 결과")
	void getSentMessages_EmptyResult() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getSentMessages(paper.getId(), fromUser.getId(), request);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("메시지 목록 조회 - 페이지 크기 제한 확인")
	void getMessages_PageSizeLimit() {
		// given
		for (int i = 1; i <= 5; i++) {
			final int index = i;
			Message message = Message.builder()
				.name("발신자" + index)
				.content("메시지 내용 " + index)
				.fontStyle("Arial")
				.backgroundColor("#FFFFFF")
				.from(fromUser)
				.to(toUser)
				.paper(paper)
				.build();
			messageRepository.save(message);
		}

		CursorPageRequest request = new CursorPageRequest(null, 3);

		// when
		List<MessageResponse> result = messageRepositoryCustom.getReceivedMessages(paper.getId(), toUser.getId(), request);

		// then
		assertThat(result).hasSize(4);
	}

	@Test
	@DisplayName("fetchJoin 동작 확인 - N+1 문제 방지")
	void getMessage_FetchJoin_Success() {
		// given
		Message savedMessage = Message.builder()
			.name("김철수")
			.content("테스트 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(fromUser)
			.to(toUser)
			.paper(paper)
			.build();
		savedMessage = messageRepository.save(savedMessage);

		// when
		MessageResponse result = messageRepositoryCustom.getMessage(savedMessage.getId(), fromUser.getId());

		// then
		assertThat(result).isNotNull();
	}
}
