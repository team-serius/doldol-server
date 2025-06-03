package doldol_server.doldol.rollingPaper.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.request.SortDirection;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.common.response.ApiCursorPageResponse;
import doldol_server.doldol.rollingPaper.dto.request.JoinPaperRequest;
import doldol_server.doldol.rollingPaper.dto.request.PaperRequest;
import doldol_server.doldol.rollingPaper.dto.response.CreatePaperResponse;
import doldol_server.doldol.rollingPaper.dto.response.MessageListResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "롤링페이퍼")
@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class PaperController {

	private final PaperService paperService;

	@GetMapping("/messages")
	@Operation(
		summary = "롤링페이퍼 상세 - 메세지 리스트 조회 API",
		description = "메세지 리스트 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<MessageListResponse>> getReceivedMessage(
		@ParameterObject @Parameter(description = "조회할 메시지 타입: SEND 또는 RECEIVE")
		@RequestParam MessageType type,
		@ParameterObject @PageableDefault(size = 18, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		MessageListResponse response = null;
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@PostMapping
	@Operation(
		summary = "롤링페이퍼 생성 API",
		description = "롤링페이퍼 생성",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<CreatePaperResponse>> createRollingPaper(
		@RequestBody @Valid PaperRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		CreatePaperResponse response = paperService.createPaper(request, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.created(response));
	}

	@GetMapping("/invite")
	@Operation(
		summary = "롤링페이퍼 초대장 조회 API",
		description = "링크를 통해 생성된 롤링페이퍼 초대장 조회")
	public ResponseEntity<ApiResponse<PaperResponse>> getPaperLink(
		@RequestParam("code") String invitationCode) {
		PaperResponse response = paperService.getInvitation(invitationCode);
		return ResponseEntity.ok(ApiResponse.ok(response));
	}

	@PostMapping("/join")
	@Operation(
		summary = "롤링페이퍼 참여 API",
		description = "초대받은 롤링페이퍼에 참여",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<Void>> joinRollingPaper(
		@RequestBody @Valid JoinPaperRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		paperService.joinPaper(request, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@GetMapping
	@Operation(
		summary = "롤링페이퍼 리스트 조회 API",
		description = "롤링페이퍼 리스트 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiCursorPageResponse<PaperResponse, Long>> getMyRollingPapers(
		@ParameterObject @Valid CursorPageRequest<Long> request,
		@Parameter(description = "정렬 기준: LATEST(최신순) 또는 OLDEST(오래된 순)")
		@RequestParam(defaultValue = "LATEST") SortDirection sortDirection,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		CursorPage<PaperResponse, Long> response = paperService.getMyRollingPapers(request,
			sortDirection, userDetails.getUserId());
		return ResponseEntity.ok(ApiCursorPageResponse.ok(response));
	}
}
