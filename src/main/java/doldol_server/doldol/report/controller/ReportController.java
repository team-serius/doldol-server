package doldol_server.doldol.report.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.report.dto.request.ReportRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "신고")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@GetMapping
	@Operation(
		summary = "신고 내역 조회 API",
		description = "신고 내역 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<List<ReportResponse>> getComplaints(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		List<ReportResponse> response = reportService.getUserReports(userId);
		return ApiResponse.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "신고 상세 조회 API",
		description = "신고 상세 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<ReportResponse> getComplaint(
		@PathVariable("id") Long reportId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		ReportResponse response = reportService.getReportDetail(reportId, userDetails.getUserId());
		return ApiResponse.ok(response);
	}

	@PostMapping
	@Operation(
		summary = "신고 작성 API",
		description = "신고",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<ReportResponse> createMessage(
		@RequestBody @Valid ReportRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		ReportResponse response = null;
		return ApiResponse.created(response);
	}
}
