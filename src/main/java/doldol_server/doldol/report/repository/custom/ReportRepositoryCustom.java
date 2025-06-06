package doldol_server.doldol.report.repository.custom;

import java.util.List;

import doldol_server.doldol.report.entity.Report;

public interface ReportRepositoryCustom {
	List<Report> findReportsByUserId(Long userId);
	Report findByIdAndUserId(Long reportId, Long userId);
}
