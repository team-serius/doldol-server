package doldol_server.doldol.report.repository.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.report.dto.response.ReportResponse;
import doldol_server.doldol.report.entity.QReport;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private final QReport report = QReport.report;

	@Override
	public List<ReportResponse> findReportsByUserId(Long userId) {
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
			.join(report.message)
			.where(report.user.id.eq(userId))
			.orderBy(report.id.desc())
			.fetch();
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
			.join(report.message)
			.where(
				report.id.eq(reportId),
				report.user.id.eq(userId)
			)
			.fetchOne();
	}
}
