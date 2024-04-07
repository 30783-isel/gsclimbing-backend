package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.HistoricReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricReportRepository  extends JpaRepository<HistoricReport, Integer>{
	HistoricReport findByIdHistoricReport(int idHistoricReport);
}
