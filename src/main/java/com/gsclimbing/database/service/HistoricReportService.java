package com.gsclimbing.database.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.repository.HistoricReportRepository;

@Service
public class HistoricReportService {
	
	@Autowired
	private HistoricReportRepository historicReportRepository;
	
	public HistoricReport getHistoricReportByIdHistoricReport(int idHistoricReport) {
		HistoricReport historicReport = historicReportRepository.findByIdHistoricReport(idHistoricReport);
		return historicReport;
	}
	
	public HistoricReport addHistoricReportByIdReportAndTypeReport(HistoricReport historicReport) {
		return historicReportRepository.save(historicReport);
	}
	
	public void deleteHistoricReport(int id) {
		historicReportRepository.deleteById(id);
	}
	
	public void saveHistoricReport( HistoricReport historicReport ) {
		historicReportRepository.save(historicReport);
	}
}