package com.gsclimbing.dto;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class TurbineDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private boolean defectsInspectionReport;
	private boolean examinationTransformer;
	private boolean measurements6KV;
	private boolean measurements690V400V;
	private boolean measurementsMwSwitchgear;
	private boolean onboardCraneInspectionReport;
	private boolean performanceReportRepairElevator;
	private boolean statutoryInspectionReport;
	private ProjectDto project;
	@Default
	private List<ReportDto> listReports = new ArrayList<>();
	
}
