package com.gsclimbing.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Turbine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
//	@ManyToOne
//	@JoinColumn(name = "projectId", referencedColumnName = "id")
//	private Project project;
	
	private int projectId;
	private String projectName;
	
	private int numberTurbine;
	
	private boolean defectsInspectionReport;
	private int defectsInspectionReportNumber = 1;
	private int defectsInspectionReportInserted = 0;
	
	private boolean examinationTransformer;
	private int examinationTransformerNumber = 1;
	private int examinationTransformerInserted = 0;
	
	private boolean measurements6KV;
	private int measurements6KVNumber = 1;
	private int measurements6KVInserted = 0;
	
	private boolean measurements690V400V;
	private int measurements690V400VNumber = 1;
	private int measurements690V400VInserted = 0;
	
	private boolean measurementsMwSwitchgear;
	private int measurementsMwSwitchgearNumber = 1;
	private int measurementsMwSwitchgearInserted = 0;
	
	private boolean onboardCraneInspectionReport;
	private int onboardCraneInspectionReportNumber = 1;
	private int onboardCraneInspectionReportInserted = 0;
	
	private boolean performanceReportRepairElevator;
	private int performanceReportRepairElevatorNumber = 1;
	private int performanceReportRepairElevatorInserted = 0;
	
	private boolean statutoryInspectionReport;
	private int statutoryInspectionReportNumber = 1;
	private int statutoryInspectionReportInserted = 0;
	
	
	
}
