package doldol_server.doldol.report.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.report.entity.QReport;
import doldol_server.doldol.report.entity.Report;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private final QReport report = QReport.report;

	@Override
	public List<Report> findReportsByUserId(Long userId) {
		return queryFactory
			.selectFrom(report)
			.leftJoin(report.message).fetchJoin()
			.where(report.user.id.eq(userId))
			.orderBy(report.id.desc())
			.fetch();
	}

	@Override
	public Report findByIdAndUserId(Long reportId, Long userId) {
		return queryFactory
			.selectFrom(report)
			.leftJoin(report.message).fetchJoin()
			.where(
				report.id.eq(reportId),
				report.user.id.eq(userId)
			)
			.fetchOne();
	}
}
