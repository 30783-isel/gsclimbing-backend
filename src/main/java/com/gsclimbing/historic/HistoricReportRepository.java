package com.gsclimbing.historic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricReportRepository  extends JpaRepository<HistoricReport, Integer>{
	
	List<HistoricReport> findByIdReportAndTypeReport(int idReport, int typeReport);
	
	HistoricReport findByIdHistoricReport(int idHistoricReport);

}
