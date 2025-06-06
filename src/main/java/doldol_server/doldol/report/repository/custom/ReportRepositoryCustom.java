package doldol_server.doldol.report.repository.custom;

import java.util.List;

import doldol_server.doldol.report.dto.response.ReportResponse;

public interface ReportRepositoryCustom {
	List<ReportResponse> findReportsByUserId(Long userId);
	ReportResponse findByReportIdAndUserId(Long reportId, Long userId);
}
