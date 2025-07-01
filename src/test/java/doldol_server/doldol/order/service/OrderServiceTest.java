package doldol_server.doldol.order.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.order.entity.Order;
import doldol_server.doldol.order.repository.OrderRepository;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("Order 서비스 통합 테스트")
class OrderServiceTest extends ServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaperRepository paperRepository;

	@Autowired
	private MessageRepository messageRepository;

	private User user;
	private Paper paper;
	private List<Long> messageIds;
	private List<Message> messages;

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

	private Paper createAndSavePaper(String name, String description, String invitationCode) {
		Paper paper = Paper.builder()
			.name(name)
			.description(description)
			.openDate(LocalDate.now().plusDays(7))
			.invitationCode(invitationCode)
			.build();
		return paperRepository.save(paper);
	}

	private Message createAndSaveMessage(String content, User from, User to, Paper paper) {
		Message message = Message.builder()
			.content(content)
			.name(from != null ? from.getName() : "익명")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(from)
			.to(to)
			.paper(paper)
			.build();
		return messageRepository.save(message);
	}

	@BeforeEach
	void setUp() {
		user = createAndSaveUser("testuser", "김철수", "test@example.com", "01012345678", "password123");
		paper = createAndSavePaper("테스트 페이퍼", "테스트용 롤링페이퍼", "TEST123");

		messages = List.of(
			createAndSaveMessage("메시지 1", user, user, paper),
			createAndSaveMessage("메시지 2", user, user, paper),
			createAndSaveMessage("메시지 3", user, user, paper)
		);

		messageIds = messages.stream().map(Message::getId).toList();
	}

	@Test
	@DisplayName("주문 생성 - 성공")
	void order_Success() {
		// given
		Long count = 5L;

		// when
		orderService.order(paper.getId(), messageIds, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getPaper().getId()).isEqualTo(paper.getId());
		assertThat(savedOrder.getUser().getId()).isEqualTo(user.getId());
		assertThat(savedOrder.getCount()).isEqualTo(count);
		assertThat(savedOrder.getOrderMessages()).hasSize(3);
		assertThat(savedOrder.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("주문 생성 - 단일 messageId로 성공")
	void order_SingleMessageId_Success() {
		// given
		List<Long> singleMessageId = List.of(messages.get(0).getId());
		Long count = 1L;

		// when
		orderService.order(paper.getId(), singleMessageId, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getOrderMessages()).hasSize(1);
		assertThat(savedOrder.getOrderMessages().get(0).getMessage().getId()).isEqualTo(messages.get(0).getId());
		assertThat(savedOrder.getCount()).isEqualTo(count);
	}


	@Test
	@DisplayName("주문 생성 - 존재하지 않는 Paper ID로 실패")
	void order_PaperNotFound() {
		// given
		Long nonExistentPaperId = 999L;
		Long count = 5L;

		// when & then
		assertThatThrownBy(() -> orderService.order(nonExistentPaperId, messageIds, count, user.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessage(PaperErrorCode.PAPER_NOT_FOUND.getMessage());

		List<Order> orders = orderRepository.findAll();
		assertThat(orders).isEmpty();
	}

	@Test
	@DisplayName("주문 생성 - 존재하지 않는 Message ID로 실패")
	void order_MessageNotFound() {
		// given
		List<Long> invalidMessageIds = List.of(999L, 888L);
		Long count = 5L;

		// when & then
		assertThatThrownBy(() -> orderService.order(paper.getId(), invalidMessageIds, count, user.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessage(MessageErrorCode.MESSAGE_NOT_FOUND.getMessage());

		List<Order> orders = orderRepository.findAll();
		assertThat(orders).isEmpty();
	}

	@Test
	@DisplayName("주문 생성 - 존재하지 않는 User ID로 실패")
	void order_UserNotFound() {
		// given
		Long nonExistentUserId = 999L;
		Long count = 5L;

		// when & then
		assertThatThrownBy(() -> orderService.order(paper.getId(), messageIds, count, nonExistentUserId))
			.isInstanceOf(CustomException.class);

		List<Order> orders = orderRepository.findAll();
		assertThat(orders).isEmpty();
	}

	@Test
	@DisplayName("주문 생성 - 여러 사용자가 동일한 Paper에 주문 성공")
	void order_MultipleUsers_Success() {
		// given
		User anotherUser = createAndSaveUser("another", "이영희", "another@example.com", "01087654321", "password456");
		List<Message> anotherMessages = List.of(
			createAndSaveMessage("메시지 4", anotherUser, anotherUser, paper),
			createAndSaveMessage("메시지 5", anotherUser, anotherUser, paper)
		);
		List<Long> anotherMessageIds = anotherMessages.stream().map(Message::getId).toList();
		Long count1 = 3L;
		Long count2 = 7L;

		// when
		orderService.order(paper.getId(), messageIds, count1, user.getId());
		orderService.order(paper.getId(), anotherMessageIds, count2, anotherUser.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(2);

		Order firstOrder = orders.stream()
			.filter(order -> order.getUser().getId().equals(user.getId()))
			.findFirst()
			.orElseThrow();
		assertThat(firstOrder.getOrderMessages()).hasSize(3);
		assertThat(firstOrder.getCount()).isEqualTo(count1);

		Order secondOrder = orders.stream()
			.filter(order -> order.getUser().getId().equals(anotherUser.getId()))
			.findFirst()
			.orElseThrow();
		assertThat(secondOrder.getOrderMessages()).hasSize(2);
		assertThat(secondOrder.getCount()).isEqualTo(count2);
	}

	@Test
	@DisplayName("주문 생성 - 동일한 사용자가 동일한 Paper에 여러 번 주문 성공")
	void order_SameUserMultipleOrders_Success() {
		// given
		List<Long> firstOrderMessageIds = List.of(messages.get(0).getId(), messages.get(1).getId());
		List<Long> secondOrderMessageIds = List.of(messages.get(2).getId());
		Long count1 = 2L;
		Long count2 = 8L;

		// when
		orderService.order(paper.getId(), firstOrderMessageIds, count1, user.getId());
		orderService.order(paper.getId(), secondOrderMessageIds, count2, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(2);

		orders.forEach(order -> {
			assertThat(order.getUser().getId()).isEqualTo(user.getId());
			assertThat(order.getPaper().getId()).isEqualTo(paper.getId());
		});
	}

	@Test
	@DisplayName("주문 생성 - OrderMessage 연관관계 확인")
	void order_OrderMessageRelationship() {
		// given
		Long count = 3L;

		// when
		orderService.order(paper.getId(), messageIds, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getOrderMessages()).hasSize(3);

		savedOrder.getOrderMessages().forEach(orderMessage -> {
			assertThat(orderMessage.getOrder()).isEqualTo(savedOrder);
			assertThat(orderMessage.getMessage()).isNotNull();
			assertThat(messageIds).contains(orderMessage.getMessage().getId());
		});
	}

	@Test
	@DisplayName("주문 생성 - 트랜잭션 롤백 테스트")
	void order_TransactionRollback() {
		// given
		Long nonExistentUserId = 999L;

		// when & then
		assertThatThrownBy(() -> orderService.order(paper.getId(), messageIds, 5L, nonExistentUserId))
			.isInstanceOf(CustomException.class);

		List<Order> orders = orderRepository.findAll();
		assertThat(orders).isEmpty();
	}
}