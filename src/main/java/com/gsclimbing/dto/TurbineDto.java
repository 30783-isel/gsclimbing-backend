package com.gsclimbing.dto;

import java.io.Serializable;

import com.gsclimbing.database.entity.DefectsInspectionReport;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TurbineDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private boolean defectsInspectionReport;
	private boolean examinationTransformer;
	private boolean measurements6KV;
	private boolean measurements690V400V;
	private boolean measurementsMwSwitchgear;
	private boolean onboardCraneInspectionReport;
	private boolean performanceReportRepairElevator;
	private boolean statutoryInspectionReport;
	
	private DefectsInspectionReport defectsInspectionReportOnTurbine;
	
}
