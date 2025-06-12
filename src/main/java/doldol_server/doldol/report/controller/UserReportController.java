package doldol_server.doldol.report.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@Tag(name = "사용자 신고")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class UserReportController {

	private final ReportService reportService;

	@PostMapping
	@Operation(
		summary = "신고 작성 API",
		description = "신고",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<ReportResponse> createMessage(
		@RequestBody @Valid ReportRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		ReportResponse response = reportService.createReport(request, userDetails.getUserId());
		return ApiResponse.created(response);
	}
}