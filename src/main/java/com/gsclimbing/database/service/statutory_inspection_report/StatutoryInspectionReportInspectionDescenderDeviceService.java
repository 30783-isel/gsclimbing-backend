package com.gsclimbing.database.service.statutory_inspection_report;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDevice;
import com.gsclimbing.database.repository.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatutoryInspectionReportInspectionDescenderDeviceService {

	@Autowired
	StatutoryInspectionReportInspectionDescenderDeviceRepository statutoryInspectionReportInspectionDescenderDeviceRepository;
	
	public void salvar( StatutoryInspectionReportInspectionDescenderDevice descenderDevice) {
		statutoryInspectionReportInspectionDescenderDeviceRepository.save(descenderDevice );
	}
	
	public void apagar( StatutoryInspectionReportInspectionDescenderDevice descenderDevice) {
		statutoryInspectionReportInspectionDescenderDeviceRepository.delete(descenderDevice );
	}

	public StatutoryInspectionReportInspectionDescenderDevice update(StatutoryInspectionReportInspectionDescenderDevice report) {
		return statutoryInspectionReportInspectionDescenderDeviceRepository.save(report );
	}
	
}
