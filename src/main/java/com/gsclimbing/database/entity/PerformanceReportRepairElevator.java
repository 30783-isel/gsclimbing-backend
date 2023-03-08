package com.gsclimbing.database.entity;

import java.util.HashMap;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PerformanceReportRepairElevator extends Report implements Cloneable { 

	private String reportNumber;
	private String inpectorsWorkers;
	private String statementOfwork;
	private String workCompleted;
	private String turbineOperable;
	private String placeDate;
	private String responsibleTechnician;
	private String performanceReport;

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("wtgType", "WTG Type");
		mapa.put("reportNumber", "Performance report no");
		mapa.put("inpectorsWorkers", "Inpectors Workers");
		mapa.put("statementOfwork", "Statement of work");
		mapa.put("placeDate", "Place date");
		mapa.put("responsibleTechnician", "Responsible technician");
		mapa.put("performanceReport", "Performance report");
		return mapa;
	}
	
	
}
