package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OnboardCraneInspectionReport extends Report implements Cloneable{

	private String manufacturerOnboardCrane;
	private String type;
	private String yearBuild;
	private String serialNumber;
	private String typePlateTestBadge;
	private String inspectors;
	private String date;
	private String resultInspection;
	private String repairRequired;
	private String nextInspection;
	
	private boolean readingOperatingBusChk;
	private String readingOperatingBusTxt;
	private boolean InterruptVoltageSupplyChk;
	private String InterruptVoltageSupplyTXt;
	private boolean circuitDiagramPositionChk;
	private String circuitDiagramPositionTxt;
	private boolean warningSignsChk;
	private String warningSignsTxt;
	private boolean cablesSignsChk;
	private String cablesSignsTxt;
	private boolean screwedCableGlandsChk;
	private String screwedCableGlandsTXt;
	private boolean openSwitchCabinetCoverChk;
	private String openSwitchCabinetCoverTxt;
	private boolean checkOperatingUnitChk;
	private String checkOperatingUnitTxt;
	private boolean checkLimitSwitchesChk;
	private String checkLimitSwitchesTxt;
	private boolean setVoltageSupplyChk;
	private String setVoltageSupplyTxt;
	private boolean checkMotorBrakeChk;
	private String checkMotorBrakeTXt;
	private boolean checkRopeMechanicalDamageChk;
	private String checkRopeMechanicalDamageTxt;
	private boolean checkLoadHookMechanicalChk;
	private String checkLoadHookMechanicalTxt;
	private boolean carryVisualInspectionChk;
	private String carryVisualInspectionTXt;
	private boolean usefeelerGaugeChk;
	private String usefeelerGaugeTxt;
	private boolean checkLimitSwitchRockersChk;
	private String checkLimitSwitchRockersTXt;
	private boolean checkCraneBridgeChk;
	private String checkCraneBridgeTxt;
	private boolean checkCrabChk;
	private String checkCrabTxt;
	private boolean checkDeflectionRollersChk;
	private String checkDeflectionRollersTxt;
	private boolean checkScrewJointsChk;
	private String checkScrewJointsTxt;
	private boolean checkAllRollersBridgeChk;
	private String checkAllRollersBridgeTxt;
	private boolean checkAllStopBuffersChk;
	private String checkAllStopBuffersTxt;
	private boolean checkAllComponentsChk;
	private String checkAllComponentsTxt;
	private boolean ancorPointSafetyEquipmentChk;
	private String ancorPointSafetyEquipmentTxt;
	private boolean loadTestChk;
	private String loadTestTxt;
	private String notes;

	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();

	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}
}


