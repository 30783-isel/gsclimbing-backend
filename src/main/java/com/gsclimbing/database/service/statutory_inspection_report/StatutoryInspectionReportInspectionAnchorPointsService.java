package com.gsclimbing.database.service.statutory_inspection_report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPointsRepository;

@Service
public class StatutoryInspectionReportInspectionAnchorPointsService {

	@Autowired
	StatutoryInspectionReportInspectionAnchorPointsRepository statutoryInspectionReportInspectionAnchorPointsRepository;
	
	
	public void salvar( StatutoryInspectionReportInspectionAnchorPoints anchorPoints) {
		statutoryInspectionReportInspectionAnchorPointsRepository.save(anchorPoints );
	}
	
	public void apagar( StatutoryInspectionReportInspectionAnchorPoints anchorPoints) {
		statutoryInspectionReportInspectionAnchorPointsRepository.delete(anchorPoints );
	}
	
	public StatutoryInspectionReportInspectionAnchorPoints getByUuid( String uuid ) {
		return statutoryInspectionReportInspectionAnchorPointsRepository.findByUuid( uuid );
	}

	public StatutoryInspectionReportInspectionAnchorPoints update(StatutoryInspectionReportInspectionAnchorPoints anchorPoints) {
		return statutoryInspectionReportInspectionAnchorPointsRepository.save(anchorPoints );
	}
}
