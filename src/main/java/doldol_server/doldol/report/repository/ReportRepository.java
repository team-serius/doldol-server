package doldol_server.doldol.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
	List<Report> findByUserId(Long userId);
}
