package com.gsclimbing.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.HistoricReport;

public interface HistoricReportRepository  extends JpaRepository<HistoricReport, Integer>{
	HistoricReport findByIdHistoricReport(int idHistoricReport);
}
