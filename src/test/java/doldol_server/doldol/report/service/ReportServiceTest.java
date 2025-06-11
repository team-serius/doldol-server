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
			.title("신고합니다")
			.content("부적절한 메시지를 신고합니다.")
			.answer(null)
			.isSolved(false)
			.build());
	}

	@Test
	@DisplayName("사용자 신고 내역 조회 - 성공")
	void getUserReports_Success() {
		// given
		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		CursorPage<ReportResponse, Long> result = reportService.getUserReports(request, receiver.getId());

		// then
		assertThat(result.getData()).hasSize(1);
		ReportResponse response = result.getData().get(0);
		assertThat(response.title()).isEqualTo("신고합니다");
		assertThat(response.content()).isEqualTo("부적절한 메시지를 신고합니다.");
		assertThat(response.isAnswered()).isFalse();
		assertThat(response.messageId()).isEqualTo(message.getId());
	}

	@Test
	@DisplayName("사용자 신고 내역 조회 - 신고가 없는 경우")
	void getUserReports_EmptyList() {
		// given
		User newUser = userRepository.save(User.builder()
			.loginId("newuser")
			.name("신규 사용자")
			.email("newuser@test.com")
			.phone("01012341234")
			.password("newpass")
			.build());

		CursorPageRequest request = new CursorPageRequest(null, 10);

		// when
		CursorPage<ReportResponse,Long> result = reportService.getUserReports(request, newUser.getId());

		// then
		assertThat(result.getData()).isEmpty();
	}

	@Test
	@DisplayName("신고 상세 조회 - 성공")
	void getReportDetail_Success() {
		// when
		ReportResponse response = reportService.getReportDetail(report.getId(), receiver.getId());

		// then
		assertThat(response.title()).isEqualTo("신고합니다");
		assertThat(response.content()).isEqualTo("부적절한 메시지를 신고합니다.");
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
			() -> reportService.getReportDetail(invalidId, receiver.getId()));
	}

	@Test
	@DisplayName("신고 상세 조회 - 다른 사용자의 신고 접근")
	void getReportDetail_Forbidden() {
		// given
		User otherUser = userRepository.save(User.builder()
			.loginId("other")
			.name("다른 사용자")
			.email("other@example.com")
			.phone("01099990000")
			.password("pass")
			.build());

		// when & then
		assertThrows(CustomException.class,
			() -> reportService.getReportDetail(report.getId(), otherUser.getId()));
	}

	@Test
	@DisplayName("신고 생성 - 성공")
	void createReport_Success() {
		// given
		ReportRequest request = new ReportRequest(
			message.getId(),
			"신고합니다",
			"욕설이 포함된 메시지입니다.",
			LocalDateTime.now()
		);

		// when
		ReportResponse response = reportService.createReport(request, receiver.getId());

		// then
		assertThat(response.title()).isEqualTo("신고합니다");
		assertThat(response.content()).isEqualTo("욕설이 포함된 메시지입니다.");
		assertThat(response.messageId()).isEqualTo(message.getId());
		assertThat(response.isAnswered()).isFalse();

		List<Report> reports = reportRepository.findAll();
		Report createdReport = reports.stream()
			.filter(r -> r.getTitle().equals("신고합니다") &&
				r.getContent().equals("욕설이 포함된 메시지입니다."))
			.findFirst()
			.orElseThrow(() -> new AssertionError("생성된 신고를 찾을 수 없습니다."));

		assertThat(createdReport.getContent()).isEqualTo("욕설이 포함된 메시지입니다.");
	}

	@Test
	@DisplayName("신고 생성 - 메시지를 찾을 수 없음")
	void createReport_MessageNotFound() {
		// given
		Long invalidMessageId = 999L;
		ReportRequest request = new ReportRequest(
			invalidMessageId,
			"신고합니다",
			"욕설이 포함된 메시지입니다.",
			LocalDateTime.now()
		);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			reportService.createReport(request, receiver.getId())
		);

		assertThat(exception.getErrorCode()).isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
	}
}
