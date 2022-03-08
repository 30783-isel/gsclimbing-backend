package com.gsclimbing.database.repository.statutory_inspection_report;

import org.springframework.data.repository.CrudRepository;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;

public interface StatutoryInspectionReportInspectionReportLadderRepository  extends CrudRepository<StatutoryInspectionReportInspectionReportLadder, Integer>{

	StatutoryInspectionReportInspectionReportLadder findByUuid( String uuid );
	
}
