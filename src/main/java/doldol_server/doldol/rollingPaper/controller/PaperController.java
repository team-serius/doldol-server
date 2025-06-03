package doldol_server.doldol.rollingPaper.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.request.SortDirection;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.rollingPaper.dto.request.JoinPaperRequest;
import doldol_server.doldol.rollingPaper.dto.request.PaperRequest;
import doldol_server.doldol.rollingPaper.dto.response.CreatePaperResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperListResponse;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;
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
		description = "롤링페이퍼 리스트 조회 - 커서 페이징 적용",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<PaperListResponse>> getMyRollingPapers(
		@ParameterObject @Valid CursorPageRequest request,
		@Parameter(description = "정렬 기준: LATEST(최신순) 또는 OLDEST(오래된 순)")
		@RequestParam(defaultValue = "LATEST") SortDirection sortDirection,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		PaperListResponse response = paperService.getMyRollingPapers(request,
			sortDirection, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.ok(response));
	}
}
