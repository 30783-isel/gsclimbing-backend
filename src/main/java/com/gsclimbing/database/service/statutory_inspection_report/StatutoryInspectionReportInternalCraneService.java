package com.gsclimbing.database.service.statutory_inspection_report;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInternalCraneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatutoryInspectionReportInternalCraneService {

	@Autowired
	private StatutoryInspectionReportInternalCraneRepository statutoryInspectionReportInternalCraneRepository;
	
	public void salvar( StatutoryInspectionReportInternalCrane internalCrane) {
		statutoryInspectionReportInternalCraneRepository.save( internalCrane );
	}
	
	public void apagar( StatutoryInspectionReportInternalCrane internalCrane) {
		statutoryInspectionReportInternalCraneRepository.delete( internalCrane );
	}
	
	public StatutoryInspectionReportInternalCrane updateInternalCrane( StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane ) {
		return statutoryInspectionReportInternalCraneRepository.save(statutoryInspectionReportInternalCrane);
	}
	
}
