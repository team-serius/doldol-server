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
	public List<ReportResponse> findReportsByUserId(Long userId, CursorPageRequest request) {
		QReport report = QReport.report;
		QMessage message = QMessage.message;

		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = report.id.lt(request.cursorId());
		}

		List<Report> reports = queryFactory
			.selectFrom(report)
			.join(report.message, message).fetchJoin()
			.where(
				message.to.id.eq(userId),
				cursorCondition
			)
			.orderBy(report.id.desc())
			.limit(request.size() + 1L)
			.fetch();

		return reports.stream().map(ReportResponse::of).toList();
	}

	@Override
	public ReportResponse findByReportIdAndUserId(Long reportId, Long userId) {
		return queryFactory
			.select(Projections.constructor(
				ReportResponse.class,
				report.message.id,
				report.message.content,
				report.title,
				report.content,
				report.createdAt,
				report.answer.isNotNull()
			))
			.from(report)
			.join(report.message, message)
			.where(
				report.id.eq(reportId),
				message.to.id.eq(userId)
			)
			.fetchOne();
	}
}

