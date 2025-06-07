package doldol_server.doldol.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.report.entity.Report;
import doldol_server.doldol.report.repository.custom.ReportRepositoryCustom;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
}
