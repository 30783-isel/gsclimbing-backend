package com.gsclimbing.historic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoricReportService {
	
	@Autowired
	private HistoricReportRepository historicReportRepository;

	public List<HistoricReport> getHistoricReportByIdReportAndTypeReport(int idReport, int typeReport) {
		List<HistoricReport> historicReport = historicReportRepository.findByIdReportAndTypeReport(idReport, typeReport);
		return historicReport;
	}
	
	public HistoricReport getHistoricReportByIdHistoricReport(int idHistoricReport) {
		HistoricReport historicReport = historicReportRepository.findByIdHistoricReport(idHistoricReport);
		return historicReport;
	}
	
	
	
	public void addHistoricReportByIdReportAndTypeReport(HistoricReport historicReport) {
		historicReportRepository.save(historicReport);
	}
	
	public void deleteHistoricReport(int id) {
		historicReportRepository.deleteById(id);
	}

}