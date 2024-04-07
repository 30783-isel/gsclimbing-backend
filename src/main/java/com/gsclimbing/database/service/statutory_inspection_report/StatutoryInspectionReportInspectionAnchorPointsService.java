package com.gsclimbing.database.service.statutory_inspection_report;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public StatutoryInspectionReportInspectionAnchorPoints update(StatutoryInspectionReportInspectionAnchorPoints anchorPoints) {
		return statutoryInspectionReportInspectionAnchorPointsRepository.save(anchorPoints );
	}
}
