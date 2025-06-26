package doldol_server.doldol.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.ReportErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.report.dto.request.ReportRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.ReportRepository;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

	private final ReportRepository reportRepository;
	private final MessageRepository messageRepository;

	public CursorPage<ReportResponse, Long> getAllReports(CursorPageRequest request) {
		List<ReportResponse> reports = reportRepository.findAllReports(request);
		return CursorPage.of(reports, request.size(), ReportResponse::messageId);
	}

	public ReportResponse getReportDetail(Long reportId) {
		ReportResponse response = reportRepository.findByReportId(reportId);

		if (response == null) {
			throw new CustomException(ReportErrorCode.REPORT_NOT_FOUND);
		}

		return response;
	}

	@Transactional
	public ReportResponse createReport(ReportRequest request, Long userId) {
		Message message = messageRepository.findById(request.messageId())
			.orElseThrow(() -> new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND));

		if (!message.getTo().getId().equals(userId)) {
			throw new CustomException(ReportErrorCode.REPORT_FORBIDDEN);
		}

		// message.updateDeleteStatus();

		Report report = Report.builder()
			.message(message)
			.isSolved(false)
			.build();

		Report saved = reportRepository.save(report);

		return ReportResponse.of(saved);
	}
}
