package com.gsclimbing.database.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsclimbing.dto.TurbineDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	
	@OneToOne(mappedBy = "turbine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	private DefectsInspectionReport defectsInspectionReportOnTurbine;
	
	@ManyToOne
    @JoinColumn(name="idProject", nullable=false)
	private Project project;

	public Turbine(Project project) {
		super();
		this.project = project;
	}
	
	public TurbineDto mapper() {
		return TurbineDto.builder()
			.id(id)
			.defectsInspectionReport(this.defectsInspectionReport)
			.examinationTransformer(this.examinationTransformer)
			.measurements6KV(this.measurements6KV)
			.measurements690V400V(this.measurements690V400V)
			.measurementsMwSwitchgear(this.measurementsMwSwitchgear)
			.onboardCraneInspectionReport(this.onboardCraneInspectionReport)
			.performanceReportRepairElevator(this.performanceReportRepairElevator)
			.statutoryInspectionReport(this.statutoryInspectionReport)
			.defectsInspectionReportOnTurbine(this.defectsInspectionReportOnTurbine)
			.build();
	}
	
	
	
}
