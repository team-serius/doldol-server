package doldol_server.doldol.report.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 신고")
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

	private final ReportService reportService;

	@GetMapping
	@Operation(
		summary = "신고 내역 조회 API",
		description = "신고 내역 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<CursorPage<ReportResponse, Long>> getReports(
		@ParameterObject @Valid CursorPageRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CursorPage<ReportResponse, Long> reports = reportService.getAllReports(request);
		return ApiResponse.ok(reports);
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "신고 상세 조회 API",
		description = "신고 상세 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<ReportResponse> getComplaint(
		@PathVariable("id") Long reportId) {
		ReportResponse response = reportService.getReportDetail(reportId);
		return ApiResponse.ok(response);
	}
}