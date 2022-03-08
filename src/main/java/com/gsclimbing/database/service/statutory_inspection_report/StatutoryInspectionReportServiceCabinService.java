package com.gsclimbing.database.service.statutory_inspection_report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportServiceCabinRepository;

@Service
public class StatutoryInspectionReportServiceCabinService {

	@Autowired
	private StatutoryInspectionReportServiceCabinRepository statutoryInspectionReportServiceCabinRepository;
	
	public void salvar( StatutoryInspectionReportServiceCabin serviceCabin) {
		statutoryInspectionReportServiceCabinRepository.save( serviceCabin );
	}
	
	public void apagar( StatutoryInspectionReportServiceCabin serviceCabin) {
		statutoryInspectionReportServiceCabinRepository.delete( serviceCabin );
	}
	
	public StatutoryInspectionReportServiceCabin getByUuid( String uuid ) {
		return statutoryInspectionReportServiceCabinRepository.findByUuid( uuid );
	}

	public StatutoryInspectionReportServiceCabin updateServiceCabin(StatutoryInspectionReportServiceCabin serviceCabin) {
		return statutoryInspectionReportServiceCabinRepository.save( serviceCabin );
	}
}
