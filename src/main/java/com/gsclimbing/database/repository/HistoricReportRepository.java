package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.HistoricReport;

public interface HistoricReportRepository  extends JpaRepository<HistoricReport, Integer>{
	List<HistoricReport> findByIdReportAndTypeReport(int idReport, int typeReport);
	HistoricReport findByIdHistoricReport(int idHistoricReport);
}
