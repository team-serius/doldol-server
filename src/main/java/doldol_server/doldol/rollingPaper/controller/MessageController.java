package doldol_server.doldol.rollingPaper.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.UpdateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "메세지")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	@GetMapping("/{id}")
	@Operation(
		summary = "메세지 상세 조회 API",
		description = "메세지 상세 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<MessageResponse>> getMessage(
		@PathVariable("id") Long messageId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		MessageResponse response = null;
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@PostMapping
	@Operation(
		summary = "메세지 작성 API",
		description = "메세지 작성",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<Void>> createMessage(
		@RequestBody @Valid CreateMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		messageService.createMessage(request, userDetails.getUserId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PatchMapping
	@Operation(
		summary = "메세지 수정 API",
		description = "메세지 수정",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<Void>> updateMessage(
		@RequestBody @Valid UpdateMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@DeleteMapping
	@Operation(
		summary = "메세지 삭제 API",
		description = "메세지 삭제",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<Void>> deleteMessage(
		@ParameterObject @RequestBody @Valid DeleteMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
