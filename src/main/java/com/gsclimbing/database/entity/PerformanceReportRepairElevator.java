package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

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

	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();

	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("reportNumber", "Performance report no");
		mapa.put("inpectorsWorkers", "Inpectors Workers");
		mapa.put("statementOfwork", "Statement of work");
		mapa.put("placeDate", "Place date");
		mapa.put("responsibleTechnician", "Responsible technician");
		mapa.put("performanceReport", "Performance report");
		return mapa;
	}
	
	
}
