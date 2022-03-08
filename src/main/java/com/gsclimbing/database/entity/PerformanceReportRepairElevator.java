package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
public class PerformanceReportRepairElevator extends Report implements Cloneable{

	private String reportNumber;
	private String inpectorsWorkers;
	private String statementOfwork;
	private boolean workCompletedYes;
	private boolean workCompletedNo;
	private boolean turbineOperableYes;
	private boolean turbineOperableNo;
	private boolean turbineOperableLimited;
	private String placeDate;
	private String responsibleTechnician;
	private String performanceReport;
	
	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();

	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}
}


