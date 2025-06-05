package doldol_server.doldol.report.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import doldol_server.doldol.common.ServiceTest;
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

	private User reporter;
	private User admin;
	private Message message;

	private Report savedReport;

	@BeforeEach
	void setUp() {
		// 사용자 생성
		reporter = userRepository.save(User.builder()
			.loginId("reporter")
			.name("홍길동")
			.email("reporter@test.com")
			.phone("01011112222")
			.password("1234")
			.build());

		admin = userRepository.save(User.builder()
			.loginId("admin")
			.name("관리자")
			.email("admin@test.com")
			.phone("01099998888")
			.password("admin123")
			.build());

		// 메시지 생성
		message = messageRepository.save(Message.builder()
			.name("김말자")
			.content("욕설 메시지")
			.fontStyle("Arial")
			.backgroundColor("#FFFFFF")
			.from(reporter)
			.to(admin)
			.paper(null)
			.build());

		// 신고 생성
		savedReport = reportRepository.save(Report.builder()
			.user(reporter)
			.admin(admin)
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
		// when
		List<ReportResponse> result = reportService.getUserReports(reporter.getId());

		// then
		assertThat(result).hasSize(1);
		ReportResponse response = result.get(0);
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

		// when
		List<ReportResponse> result = reportService.getUserReports(newUser.getId());

		// then
		assertThat(result).isEmpty();
	}
}