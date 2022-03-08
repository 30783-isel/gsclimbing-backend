package com.gsclimbing.database.repository.statutory_inspection_report;

import org.springframework.data.repository.CrudRepository;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;

public interface StatutoryInspectionReportInternalCraneRepository extends CrudRepository<StatutoryInspectionReportInternalCrane, Integer>{

	StatutoryInspectionReportInternalCrane findByUuid( String uuid );
	
}
