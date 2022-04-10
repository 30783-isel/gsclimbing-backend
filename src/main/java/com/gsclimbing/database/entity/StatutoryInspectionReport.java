package com.gsclimbing.database.entity;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDevice;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StatutoryInspectionReport extends Report implements Cloneable, Serializable {

	private static final long serialVersionUID = -1843885893406746547L;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_descender_device_id", referencedColumnName = "id", nullable = true)
	private StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_report_ladder_id", referencedColumnName = "id", nullable = true)
	private StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "internal_crane_id", referencedColumnName = "id", nullable = true)
	private StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_report_service_cabin_id", referencedColumnName = "id", nullable = true)
	private StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_anchor_points_id", referencedColumnName = "id", nullable = true)
	private StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints;

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

	@Override
	public Object clone() {
		StatutoryInspectionReport statutoryInspectionReport = null;
		try {
			statutoryInspectionReport = (StatutoryInspectionReport) super.clone();
			statutoryInspectionReport.statutoryInspectionReportInspectionAnchorPoints = (StatutoryInspectionReportInspectionAnchorPoints) this.statutoryInspectionReportInspectionAnchorPoints.clone();
			statutoryInspectionReport.statutoryInspectionReportInspectionDescenderDevice = (StatutoryInspectionReportInspectionDescenderDevice) this.statutoryInspectionReportInspectionDescenderDevice.clone();
			statutoryInspectionReport.statutoryInspectionReportInspectionReportLadder = (StatutoryInspectionReportInspectionReportLadder) this.statutoryInspectionReportInspectionReportLadder.clone();
			statutoryInspectionReport.statutoryInspectionReportInternalCrane = (StatutoryInspectionReportInternalCrane) this.statutoryInspectionReportInternalCrane.clone();
			statutoryInspectionReport.statutoryInspectionReportServiceCabin = (StatutoryInspectionReportServiceCabin) this.statutoryInspectionReportServiceCabin.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e.getMessage());
		}
		return statutoryInspectionReport;
	}

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("reportNumber", "");

		mapa.put("reportNumber", "Report number");
		mapa.put("client", "Client");
		mapa.put("clientContact", "Client contact");
		mapa.put("windPark", "Wind Park");
		mapa.put("siteAddress", "Site Address");

		mapa.put("serviceCabinManufacturer", "Service cabin / Manufacturer");
		mapa.put("serviceCabinType", "Service cabin / Type");
		mapa.put("serviceCabinSerialNumber", "Service cabin / Serial number");
		mapa.put("serviceCabinInspectionPassedWithoutDefects", "Service cabin / Inspection passed without defects");
		mapa.put("serviceCabinInspectionPassedWithSmallDefects", "Service cabin / Inspection passed with small defects");
		mapa.put("serviceCabinInspectionNotPassed", "Service cabin / Inspection not passed");

		mapa.put("ladderTowerManufacturer", "Ladder Tower / Manufacturer");
		mapa.put("ladderTowerType", "Ladder Tower / Type");
		mapa.put("ladderTowerSerialNumber", "Ladder Tower / Serial number");
		mapa.put("ladderTowerInspectionPassedWithoutDefects", "Ladder Tower / Inspection passed without defects");
		mapa.put("ladderTowerInspectionPassedWithSmallDefects", "Ladder Tower / Inspection passed with small defects");
		mapa.put("ladderTowerInspectionNotPassed", "Ladder Tower / Inspection not passed");

		mapa.put("failArrestSystemTowerManufacturer", "Fail arrest system tower / Manufacturer");
		mapa.put("failArrestSystemTowerType", "Fail arrest system tower / Type");
		mapa.put("failArrestSystemTowerSerialNumber", "Fail arrest system tower / Serial number");
		mapa.put("failArrestSystemTowerInspectionPassedWithoutDefects", "Fail arrest system tower / Inspection passed without defects");
		mapa.put("failArrestSystemTowerInspectionPassedWithSmallDefects", "Fail arrest system tower / Inspection passed with small defects");
		mapa.put("failArrestSystemTowerInspectionNotPassed", "Fail arrest system tower / Inspection not passed");

		mapa.put("internalCraneManufacturer", "Internal crane / Manufacturer");
		mapa.put("internalCraneType", "Internal crane / Type");
		mapa.put("internalCraneSerialNumber", "Internal crane / Serial number");
		mapa.put("internalCraneInspectionPassedWithoutDefects", "Internal crane / Inspection passed without defects");
		mapa.put("internalCraneInspectionPassedWithSmallDefects", "Internal crane / Inspection passed with small defects");
		mapa.put("internalCraneInspectionNotPassed", "Internal crane / Inspection not passed");

		mapa.put("descenderDeviceManufacturer", "Descender device / Manufacturer");
		mapa.put("descenderDeviceType", "Descender device / Type");
		mapa.put("descenderDeviceSerialNumber", "Descender device / Serial number");
		mapa.put("descenderDeviceInspectionPassedWithoutDefects", "Descender device / Inspection passed without defects");
		mapa.put("descenderDeviceInspectionPassedWithSmallDefects", "Descender device / Inspection passed with small defects");
		mapa.put("descenderDeviceInspectionNotPassed", "Descender device / Inspection not passed");

		mapa.put("anchorPointsManufacturer", "Anchor points / Manufacturer");
		mapa.put("anchorPointsType", "Anchor points / Type");
		mapa.put("anchorPointsSerialNumber", "Anchor points / Serial number");
		mapa.put("anchorPointsInspectionPassedWithoutDefects", "Anchor points / Inspection passed without defects");
		mapa.put("anchorPointsInspectionPassedWithSmallDefects", "Anchor points / Inspection passed with small defects");
		mapa.put("anchorPointsInspectionNotPassed", "Anchor points / Inspection not passed");

		mapa.put("inspectors", "Inspectors");
		mapa.put("date", "Date");
		mapa.put("resultOfInspection", "Result of inspection");
		mapa.put("repairRequired", "Repair required");
		mapa.put("nextInspection", "Next inspection");

		mapa.put("siteDate", "Site, date");
		mapa.put("responsibleTechnician", "Responsible technician");

		return mapa;
	}
}
