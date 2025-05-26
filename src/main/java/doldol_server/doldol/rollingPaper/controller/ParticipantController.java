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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "참여자")
@RestController
@RequestMapping("/participants")
public class ParticipantController {

	@GetMapping("/{id}")
	@Operation(
		summary = "롤링페이퍼 참여자 조회 API",
		description = "롤링페이퍼 참여자 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ResponseEntity<ApiResponse<List<ParticipantResponse>>> getComplaints(
		@PathVariable("id") Long paperId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<ParticipantResponse> response = null;
		return ResponseEntity.ok(ApiResponse.ok(response));
	}
}
