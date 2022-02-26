package com.gsclimbing.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Turbine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private boolean defectsInspectionReport;
	private boolean examinationTransformer;
	private boolean measurements6KV;	
	private boolean measurements690V400V;
	private boolean measurementsMwSwitchgear;
	private boolean onboardCraneInspectionReport;
	private boolean performanceReportRepairElevator;
	private boolean statutoryInspectionReport;
	
	@OneToOne(mappedBy = "turbine")
	private DefectsInspectionReport defectsInspectionReportOnTurbine;
	
	@ManyToOne
    @JoinColumn(name="idProject", nullable=false)
	private Project project;

	public Turbine(Project project) {
		super();
		this.project = project;
	}
	
	
	
}
