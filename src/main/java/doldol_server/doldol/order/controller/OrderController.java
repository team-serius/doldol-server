package doldol_server.doldol.order.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.order.dto.request.OrderRequest;
import doldol_server.doldol.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "주문 ")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping()
	@Operation(
		summary = "주문 API",
		description = "주문",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> order(
		@RequestBody OrderRequest orderRequest,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		orderService.order(orderRequest.paperId(), orderRequest.messageIds(), orderRequest.count(),
			userDetails.getUserId());
		return ApiResponse.noContent();
	}
}
