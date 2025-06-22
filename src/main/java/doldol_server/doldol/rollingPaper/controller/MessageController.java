package doldol_server.doldol.rollingPaper.controller;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.rollingPaper.dto.request.CreateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.DeleteMessageRequest;
import doldol_server.doldol.rollingPaper.dto.request.UpdateMessageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@GetMapping("/{messageId}")
	@Operation(
		summary = "메세지 조회 API",
		description = "메세지 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<MessageResponse> getMessage(
		@PathVariable("messageId") Long messageId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		MessageResponse message = messageService.getMessage(messageId, userDetails.getUserId());
		return ApiResponse.ok(message);
	}

	@GetMapping()
	@Operation(
		summary = "메세지 리스트 조회 API",
		description = "메세지 리스트 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<MessageListResponse> getMessages(
		@RequestParam("paperId") Long paperId,
		@Parameter(description = "메시지 타입: RECEIVE(송신) 또는 SEND(발신)")
		@RequestParam(defaultValue = "SEND") MessageType messageType,
		@Parameter(description = "공개 날짜: 2025-06-26")
		@RequestParam LocalDate openDate,
		@ParameterObject @Valid CursorPageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		MessageListResponse messages = messageService.getMessages(paperId, messageType, openDate, request,
			userDetails.getUserId());
		return ApiResponse.ok(messages);
	}

	@PostMapping
	@Operation(
		summary = "메세지 작성 API",
		description = "메세지 작성",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> createMessage(
		@RequestBody @Valid CreateMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		messageService.createMessage(request, userDetails.getUserId());
		return ApiResponse.noContent();
	}

	@PatchMapping
	@Operation(
		summary = "메세지 수정 API",
		description = "메세지 수정",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> updateMessage(
		@RequestBody @Valid UpdateMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		messageService.updateMessage(request, userDetails.getUserId());
		return ApiResponse.noContent();
	}

	@DeleteMapping
	@Operation(
		summary = "메세지 삭제 API",
		description = "메세지 삭제",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> deleteMessage(
		@ParameterObject @RequestBody @Valid DeleteMessageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		messageService.deleteMessage(request, userDetails.getUserId());
		return ApiResponse.noContent();
	}
}
