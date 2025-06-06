package doldol_server.doldol.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.exception.errorCode.ReportErrorCode;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.report.dto.request.ReportRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.ReportRepository;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;

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
		User reporter = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
		Message message = messageRepository.findById(request.messageId())
			.orElseThrow(() -> new CustomException(MessageErrorCode.MESSAGE_NOT_FOUND));

		Report report = Report.builder()
			.user(reporter)
			.message(message)
			.title(request.title())
			.content(request.content())
			.isSolved(false)
			.build();

		Report saved = reportRepository.save(report);

		return new ReportResponse(
			saved.getMessage().getId(),
			saved.getMessage().getContent(),
			saved.getTitle(),
			saved.getContent(),
			saved.getCreatedAt(),
			false
		);
	}
}
