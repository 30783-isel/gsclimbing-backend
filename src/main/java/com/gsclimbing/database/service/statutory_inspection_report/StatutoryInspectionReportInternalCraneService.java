package com.gsclimbing.database.service.statutory_inspection_report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInternalCraneRepository;

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
	
	public StatutoryInspectionReportInternalCrane getByUuid( String uuid ) {
		return  statutoryInspectionReportInternalCraneRepository.findByUuid( uuid );
	}
	
	public StatutoryInspectionReportInternalCrane updateInternalCrane( StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane ) {
		return statutoryInspectionReportInternalCraneRepository.save(statutoryInspectionReportInternalCrane);
	}
	
}
