package doldol_server.doldol.report.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.ReportErrorCode;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;

	public List<ReportResponse> getUserReports(Long userId) {
		List<Report> reports = reportRepository.findByUserId(userId);

		return reports.stream()
			.map(report -> new ReportResponse(
				report.getMessage().getId(),
				report.getTitle(),
				report.getContent(),
				report.getCreatedAt(),
				report.getAnswer() != null
			)).collect(Collectors.toList());
	}

	public ReportResponse getReportDetail(Long reportId, Long userId) {
		Report report = reportRepository.findById(reportId)
			.orElseThrow(() -> new CustomException(ReportErrorCode.REPORT_NOT_FOUND));

		if (!report.getUser().getId().equals(userId)) {
			throw new CustomException(ReportErrorCode.REPORT_FORBIDDIN);
		}
		return new ReportResponse(
			report.getMessage().getId(),
			report.getTitle(),
			report.getContent(),
			report.getCreatedAt(),
			report.getAnswer() != null
		);
	}
}
