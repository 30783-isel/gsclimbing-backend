package com.gsclimbing.database.service.statutory_inspection_report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadderRepository;

@Service
public class StatutoryInspectionReportInspectionReportLadderService {

	@Autowired
	private StatutoryInspectionReportInspectionReportLadderRepository statutoryInspectionReportInspectionReportLadderRepository;
	
	public void salvar( StatutoryInspectionReportInspectionReportLadder reportLadder) {
		statutoryInspectionReportInspectionReportLadderRepository.save(reportLadder );
	}
	
	public void apagar( StatutoryInspectionReportInspectionReportLadder reportLadder) {
		statutoryInspectionReportInspectionReportLadderRepository.delete(reportLadder );
	}
	
	public StatutoryInspectionReportInspectionReportLadder update( StatutoryInspectionReportInspectionReportLadder reportLadder) {
		return statutoryInspectionReportInspectionReportLadderRepository.save(reportLadder );
	}
}
