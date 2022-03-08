package com.gsclimbing.database.repository.statutory_inspection_report;

import org.springframework.data.repository.CrudRepository;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;

public interface StatutoryInspectionReportServiceCabinRepository extends CrudRepository<StatutoryInspectionReportServiceCabin, Integer>{

	StatutoryInspectionReportServiceCabin findByUuid( String uuid );
	
}
