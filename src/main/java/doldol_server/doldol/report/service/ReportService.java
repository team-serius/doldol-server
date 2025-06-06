package doldol_server.doldol.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.ReportErrorCode;
import doldol_server.doldol.report.dto.request.ReportRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.ReportRepository;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

	private final ReportRepository reportRepository;
	private final MessageRepository messageRepository;
	private final UserService userService;

	public List<ReportResponse> getUserReports(Long userId) {
		return reportRepository.findReportsByUserId(userId);
	}

	public ReportResponse getReportDetail(Long reportId, Long userId) {
		ReportResponse response = reportRepository.findByReportIdAndUserId(reportId, userId);

		if (response == null) {
			throw new CustomException(ReportErrorCode.REPORT_NOT_FOUND);
		}

		return response;
	}

	@Transactional
	public ReportResponse createReport(ReportRequest request, Long userId) {
		Message message = messageRepository.findById(request.messageId())
			.orElseThrow(() -> new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND));

		Report report = Report.builder()
			.message(message)
			.title(request.title())
			.content(request.content())
			.isSolved(false)
			.build();

		Report saved = reportRepository.save(report);

		return ReportResponse.of(saved);
	}
}
