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
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.order.entity.Order;
import doldol_server.doldol.order.repository.OrderRepository;
import doldol_server.doldol.rollingPaper.entity.Paper;
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

	private User user;
	private Paper paper;
	private List<Long> messageIds;

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

	@BeforeEach
	void setUp() {
		user = createAndSaveUser("testuser", "김철수", "test@example.com", "01012345678", "password123");
		paper = createAndSavePaper("테스트 페이퍼", "테스트용 롤링페이퍼", "TEST123");
		messageIds = List.of(1L, 2L, 3L);
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
		assertThat(savedOrder.getMessageIds()).isEqualTo("1,2,3");
		assertThat(savedOrder.getCount()).isEqualTo(count);
		assertThat(savedOrder.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("주문 생성 - 단일 messageId로 성공")
	void order_SingleMessageId_Success() {
		// given
		List<Long> singleMessageId = List.of(42L);
		Long count = 1L;

		// when
		orderService.order(paper.getId(), singleMessageId, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getMessageIds()).isEqualTo("42");
		assertThat(savedOrder.getCount()).isEqualTo(count);
	}

	@Test
	@DisplayName("주문 생성 - 최대 개수 messageIds로 성공")
	void order_MaxMessageIds_Success() {
		// given
		List<Long> maxMessageIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		Long count = 10L;

		// when
		orderService.order(paper.getId(), maxMessageIds, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getMessageIds()).isEqualTo("1,2,3,4,5,6,7,8,9,10");
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
		Long count1 = 3L;
		Long count2 = 7L;

		// when
		orderService.order(paper.getId(), messageIds, count1, user.getId());
		orderService.order(paper.getId(), List.of(4L, 5L), count2, anotherUser.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(2);

		Order firstOrder = orders.stream()
			.filter(order -> order.getUser().getId().equals(user.getId()))
			.findFirst()
			.orElseThrow();
		assertThat(firstOrder.getMessageIds()).isEqualTo("1,2,3");
		assertThat(firstOrder.getCount()).isEqualTo(count1);

		Order secondOrder = orders.stream()
			.filter(order -> order.getUser().getId().equals(anotherUser.getId()))
			.findFirst()
			.orElseThrow();
		assertThat(secondOrder.getMessageIds()).isEqualTo("4,5");
		assertThat(secondOrder.getCount()).isEqualTo(count2);
	}

	@Test
	@DisplayName("주문 생성 - 동일한 사용자가 동일한 Paper에 여러 번 주문 성공")
	void order_SameUserMultipleOrders_Success() {
		// given
		Long count1 = 2L;
		Long count2 = 8L;

		// when
		orderService.order(paper.getId(), List.of(1L, 2L), count1, user.getId());
		orderService.order(paper.getId(), List.of(3L, 4L, 5L), count2, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(2);

		orders.forEach(order -> {
			assertThat(order.getUser().getId()).isEqualTo(user.getId());
			assertThat(order.getPaper().getId()).isEqualTo(paper.getId());
		});
	}

	@Test
	@DisplayName("주문 생성 - messageIds가 큰 숫자들로 구성되어도 성공")
	void order_LargeMessageIds_Success() {
		// given
		List<Long> largeMessageIds = List.of(999999L, 888888L, 777777L);
		Long count = 3L;

		// when
		orderService.order(paper.getId(), largeMessageIds, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		assertThat(savedOrder.getMessageIds()).isEqualTo("999999,888888,777777");
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

	@Test
	@DisplayName("주문 생성 - messageIds 순서 유지 확인")
	void order_MessageIdsOrder_Preserved() {
		// given
		List<Long> orderedMessageIds = List.of(5L, 1L, 9L, 3L, 7L);
		Long count = 5L;

		// when
		orderService.order(paper.getId(), orderedMessageIds, count, user.getId());

		// then
		List<Order> orders = orderRepository.findAll();
		assertThat(orders).hasSize(1);

		Order savedOrder = orders.get(0);
		// 입력한 순서대로 저장되는지 확인
		assertThat(savedOrder.getMessageIds()).isEqualTo("5,1,9,3,7");
	}
}