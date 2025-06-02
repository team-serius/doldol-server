package doldol_server.doldol.rollingPaper.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;
import doldol_server.doldol.rollingPaper.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "참여자")
@RestController
@RequestMapping("/participants")
@RequiredArgsConstructor
public class ParticipantController {

	private final ParticipantService participantService;

	@GetMapping("/{id}")
	@Operation(
		summary = "롤링페이퍼 참여자 조회 API",
		description = "롤링페이퍼 참여자 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getParticipants(
		@PathVariable("id") Long paperId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<ParticipantResponse> response = participantService.getParticipants(paperId, userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.ok(response));
	}
}
