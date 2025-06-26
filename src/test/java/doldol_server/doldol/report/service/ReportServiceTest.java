package doldol_server.doldol.report.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.dto.CursorPage;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MessageErrorCode;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.report.dto.request.ReportRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.ReportRepository;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("Report 서비스 통합 테스트")
class ReportServiceTest extends ServiceTest {

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageRepository messageRepository;

	private User receiver;
	private User sender;
	private Message message;
	private Report report;


	@BeforeEach
	void setUp() {
		receiver = userRepository.save(User.builder()
			.loginId("receiver")
			.name("수신자")
			.email("receiver@test.com")
			.phone("01022223333")
			.password("5678")
			.build());

		sender = userRepository.save(User.builder()
			.loginId("sender")
			.name("홍길동")
			.email("sender@test.com")
			.phone("01011112222")
			.password("1234")
			.build());

		message = messageRepository.save(Message.builder()
			.name("김말자")
			.content("욕설 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(sender)
			.to(receiver) // ✅ 핵심: 이 유저가 받은 메시지
			.paper(null)
			.build());

		report = reportRepository.save(Report.builder()
			.message(message)
			.answer(null)
			.isSolved(false)
			.build());
	}

	@Test
	@DisplayName("신고 상세 조회 - 성공")
	void getReportDetail_Success() {
		// when
		ReportResponse response = reportService.getReportDetail(report.getId());

		// then
		assertThat(response.messageId()).isEqualTo(message.getId());
		assertThat(response.isAnswered()).isFalse();
	}

	@Test
	@DisplayName("신고 상세 조회 - 존재하지 않는 신고")
	void getReportDetail_ReportNotFound() {
		// given
		Long invalidId = 999L;

		// when & then
		assertThrows(CustomException.class,
			() -> reportService.getReportDetail(invalidId));
	}

	@Test
	@DisplayName("신고 생성 - 성공")
	void createReport_Success() {
		// given
		ReportRequest request = new ReportRequest(
			message.getId(),
			LocalDateTime.now()
		);

		// when
		ReportResponse response = reportService.createReport(request, receiver.getId());

		// then
		assertThat(response.messageId()).isEqualTo(message.getId());
		assertThat(response.isAnswered()).isFalse();

		List<Report> reports = reportRepository.findAll();
		Report createdReport = reports.stream()
			.findFirst()
			.orElseThrow(() -> new AssertionError("생성된 신고를 찾을 수 없습니다."));

		assertThat(createdReport.getMessage().getId()).isEqualTo(message.getId());
	}

	@Test
	@DisplayName("신고 생성 - 메시지를 찾을 수 없음")
	void createReport_MessageNotFound() {
		// given
		Long invalidMessageId = 999L;
		ReportRequest request = new ReportRequest(
			invalidMessageId,
			LocalDateTime.now()
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			reportService.createReport(request, receiver.getId())
		);

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}
}
