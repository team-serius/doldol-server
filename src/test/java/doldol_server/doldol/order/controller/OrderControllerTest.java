package doldol_server.doldol.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.common.ControllerTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.PaperErrorCode;
import doldol_server.doldol.order.dto.request.OrderRequest;
import doldol_server.doldol.order.service.OrderService;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest extends ControllerTest {

	@MockitoBean
	private OrderService orderService;

	@Test
	@DisplayName("주문 생성 - 성공")
	void order_Success() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, List.of(1L, 2L, 3L), 5L);
		doNothing().when(orderService).order(anyLong(), anyList(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(orderService).order(eq(1L), eq(List.of(1L, 2L, 3L)), eq(5L), eq(1L));
	}


	@Test
	@DisplayName("주문 생성 - messageIds가 null이면 오류 발생")
	void order_ValidationFail_MessageIdsNull() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, null, 5L);

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isBadRequest());

		verify(orderService, never()).order(anyLong(), anyList(), anyLong(), anyLong());
	}

	@Test
	@DisplayName("주문 생성 - messageIds가 빈 리스트면 오류 발생")
	void order_ValidationFail_MessageIdsEmpty() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, List.of(), 5L);

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isBadRequest());

		verify(orderService, never()).order(anyLong(), anyList(), anyLong(), anyLong());
	}

	@Test
	@DisplayName("주문 생성 - messageIds가 10개 초과하면 오류 발생")
	void order_ValidationFail_MessageIdsTooMany() throws Exception {
		// given
		List<Long> tooManyMessageIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
		OrderRequest request = new OrderRequest(1L, tooManyMessageIds, 5L);

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isBadRequest());

		verify(orderService, never()).order(anyLong(), anyList(), anyLong(), anyLong());
	}

	@Test
	@DisplayName("주문 생성 - count가 null이면 오류 발생")
	void order_ValidationFail_CountNull() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, List.of(1L, 2L, 3L), null);

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isBadRequest());

		verify(orderService, never()).order(anyLong(), anyList(), anyLong(), anyLong());
	}

	@Test
	@DisplayName("주문 생성 - count가 1보다 작으면 오류 발생")
	void order_ValidationFail_CountTooSmall() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, List.of(1L, 2L, 3L), 0L);

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isBadRequest());

		verify(orderService, never()).order(anyLong(), anyList(), anyLong(), anyLong());
	}

	@Test
	@DisplayName("주문 생성 - 존재하지 않는 paper로 주문하면 오류 발생")
	void order_PaperNotFound() throws Exception {
		// given
		OrderRequest request = new OrderRequest(999L, List.of(1L, 2L, 3L), 5L);
		doThrow(new CustomException(PaperErrorCode.PAPER_NOT_FOUND))
			.when(orderService).order(anyLong(), anyList(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isNotFound());

		verify(orderService).order(eq(999L), eq(List.of(1L, 2L, 3L)), eq(5L), eq(1L));
	}

	@Test
	@DisplayName("주문 생성 - messageIds 최소/최대 경계값 테스트 (1개)")
	void order_MessageIds_MinBoundary() throws Exception {
		// given
		OrderRequest request = new OrderRequest(1L, List.of(1L), 1L);
		doNothing().when(orderService).order(anyLong(), anyList(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(orderService).order(eq(1L), eq(List.of(1L)), eq(1L), eq(1L));
	}

	@Test
	@DisplayName("주문 생성 - messageIds 최소/최대 경계값 테스트 (10개)")
	void order_MessageIds_MaxBoundary() throws Exception {
		// given
		List<Long> maxMessageIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		OrderRequest request = new OrderRequest(1L, maxMessageIds, 1L);
		doNothing().when(orderService).order(anyLong(), anyList(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(orderService).order(eq(1L), eq(maxMessageIds), eq(1L), eq(1L));
	}
}