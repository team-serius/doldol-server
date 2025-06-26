package doldol_server.doldol.report.repository.custom;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.QReport;
import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.rollingPaper.entity.QMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QReport report = QReport.report;
	private final QMessage message = QMessage.message;

	@Override
	public List<ReportResponse> findAllReports(CursorPageRequest request) {
		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = report.id.lt(request.cursorId());
		}

		List<Report> reports = queryFactory
			.selectFrom(report)
			.join(report.message, message).fetchJoin()
			.where(cursorCondition)
			.orderBy(report.id.desc())
			.limit(request.size() + 1L)
			.fetch();

		return reports.stream()
			.map(ReportResponse::of)
			.toList();
	}

	@Override
	public ReportResponse findByReportId(Long reportId) {
		return queryFactory
			.select(Projections.constructor(
				ReportResponse.class,
				report.id,              // Long reportId
				message.id,             // Long messageId
				message.content,        // String messageContent
				report.createdAt,       // LocalDateTime createdAt
				report.answer.isNotNull() // boolean isAnswered
			))
			.from(report)
			.join(report.message, message)
			.where(report.id.eq(reportId))
			.fetchOne();
	}
}

