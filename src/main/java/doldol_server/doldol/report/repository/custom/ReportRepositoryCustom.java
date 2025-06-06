package doldol_server.doldol.report.repository.custom;

import java.util.List;
import java.util.Optional;

import doldol_server.doldol.report.entity.Report;

public interface ReportRepositoryCustom {
	List<Report> findReportsByUserId(Long userId);
	Optional<Report> findByIdAndUserId(Long reportId, Long userId);
}
