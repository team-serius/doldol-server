package doldol_server.doldol.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.order.entity.Order;
import doldol_server.doldol.order.repository.OrderRepository;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.PaperRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final UserService userService;
	private final PaperRepository paperRepository;
	private final OrderRepository orderRepository;

	@Transactional
	public void order(Long paperId,
		List<Long> messageIds,
		Long count, Long userId) {
		User user = userService.getById(userId);
		Paper paper = paperRepository.findById(paperId)
			.orElseThrow(() -> new CustomException(PaperErrorCode.PAPER_NOT_FOUND));

		String messageIdsString = messageIds.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(","));


		Order order = Order.builder()
			.paper(paper)
			.user(user)
			.messageIds(messageIdsString)
			.count(count)
			.build();

		orderRepository.save(order);
	}
}
