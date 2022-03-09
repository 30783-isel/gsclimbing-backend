package com.gsclimbing.database.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDevice;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;

import lombok.Data;

@Data
@Entity
public class StatutoryInspectionReport  extends Report implements Cloneable{
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspectionDescenderDeviceId", referencedColumnName = "id", nullable=true)
	private StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspectionReportLadderId", referencedColumnName = "id", nullable=true)
	private StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder;
    
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "internalCraneId", referencedColumnName = "id", nullable=true)
	private StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspectionReportServiceCabinId", referencedColumnName = "id", nullable=true)
	private StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspectionAnchorPointsId", referencedColumnName = "id", nullable=true)
	private StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints;
    

//    private int inspectionAnchorPointsId;
//    private int inspectionDescenderDeviceId;
//    private int inspectionReportLadderId;
//    private int internalCraneId;
//    private int inspectionReportServiceCabinId;
    

	private String reportNumber;
	private String client;
	private String clientContact;
	private String windPark;
	private String siteAddress;

	private String serviceCabinManufacturer;
	private String serviceCabinType;
	private String serviceCabinSerialNumber;
	private String serviceCabinInspectionPassedWithoutDefects;
	private String serviceCabinInspectionPassedWithSmallDefects;
	private String serviceCabinInspectionNotPassed;

	private String ladderTowerManufacturer;
	private String ladderTowerType;
	private String ladderTowerSerialNumber;
	private String ladderTowerInspectionPassedWithoutDefects;
	private String ladderTowerInspectionPassedWithSmallDefects;
	private String ladderTowerInspectionNotPassed;
	
	private String failArrestSystemTowerManufacturer;
	private String failArrestSystemTowerType;
	private String failArrestSystemTowerSerialNumber;
	private String failArrestSystemTowerInspectionPassedWithoutDefects;
	private String failArrestSystemTowerInspectionPassedWithSmallDefects;
	private String failArrestSystemTowerInspectionNotPassed;

	private String internalCraneManufacturer;
	private String internalCraneType;
	private String internalCraneSerialNumber;
	private String internalCraneInspectionPassedWithoutDefects;
	private String internalCraneInspectionPassedWithSmallDefects;
	private String internalCraneInspectionNotPassed;

	private String descenderDeviceManufacturer;
	private String descenderDeviceType;
	private String descenderDeviceSerialNumber;
	private String descenderDeviceInspectionPassedWithoutDefects;
	private String descenderDeviceInspectionPassedWithSmallDefects;
	private String descenderDeviceInspectionNotPassed;

	private String anchorPointsManufacturer;
	private String anchorPointsType;
	private String anchorPointsSerialNumber;
	private String anchorPointsInspectionPassedWithoutDefects;
	private String anchorPointsInspectionPassedWithSmallDefects;
	private String anchorPointsInspectionNotPassed;
	
	private String inspectors;
	private String date;
	private String resultOfInspection;
	private String repairRequired;
	private String nextInspection;

	private String siteDate;
	private String responsibleTechnician;	

}


