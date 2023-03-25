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
public class OnboardCraneInspectionReport extends Report implements Cloneable {

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

	private boolean interruptVoltageSupplyChk;
	private String interruptVoltageSupplyTxt;
	
	private boolean circuitDiagramPositionChk;
	private String circuitDiagramPositionTxt;
	
	private boolean warningSignsChk;
	private String warningSignsTxt;
	
	private boolean cablesSignsChk;
	private String cablesSignsTxt;
	
	private boolean screwedCableGlandsChk;
	private String screwedCableGlandsTxt;

	private boolean openSwitchCabinetCoverChk;
	private String openSwitchCabinetCoverTxt;
	
	private boolean checkOperatingUnitChk;
	private String checkOperatingUnitTxt;
	
	private boolean checkLimitSwitchesChk;
	private String checkLimitSwitchesTxt;
	
	private boolean setVoltageSupplyChk;
	private String setVoltageSupplyTxt;
	
	private boolean checkMotorBrakeChk;
	private String checkMotorBrakeTxt;
	
	private boolean checkRopeMechanicalDamageChk;
	private String checkRopeMechanicalDamageTxt;
	
	private boolean checkLoadHookMechanicalChk;
	private String checkLoadHookMechanicalTxt;
	
	private boolean carryVisualInspectionChk;
	private String carryVisualInspectionTxt;

	private boolean usefeelerGaugeChk;
	private String usefeelerGaugeTxt;
	
	private boolean checkLimitSwitchRockersChk;
	private String checkLimitSwitchRockersTxt;
	
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

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("wtgType", "WTG Type");
		mapa.put("manufacturerOnboardCrane", "Manufacturer onboard crane");
		mapa.put("type", "Type");
		mapa.put("yearBuild", "Year Build");
		mapa.put("serialNumber", "Serial Number");
		mapa.put("typePlateTestBadge", "Type Plate Test Badge");
		mapa.put("inspectors", "Inspectors");
		mapa.put("date", "Date");
		mapa.put("resultInspection", "Result Inspection");
		mapa.put("repairRequired", "Repair Required");
		mapa.put("nextInspection", "Next Inspection");
		mapa.put("notes", "Notes");
		mapa.put("readingOperatingBusTxt", "Reading of the operating hours");
		mapa.put("readingOperatingBusChk", "Reading of the operating hours <Checkbox>");
		
		mapa.put("additionalField1Label", "Additional Field Label 1");
		mapa.put("additionalField2Label", "Additional Field Label 2");
		mapa.put("additionalField3Label", "Additional Field Label 3");
		mapa.put("additionalField4Label", "Additional Field Label 4");
		mapa.put("additionalField5Label", "Additional Field Label 5");
		mapa.put("additionalField6Label", "Additional Field Label 6");
		mapa.put("additionalField7Label", "Additional Field Label 7");

		mapa.put("additionalField1Text", "Additional Field Value 1");
		mapa.put("additionalField2Text", "Additional Field Value 2");
		mapa.put("additionalField3Text", "Additional Field Value 3");
		mapa.put("additionalField4Text", "Additional Field Value 4");
		mapa.put("additionalField5Text", "Additional Field Value 5");
		mapa.put("additionalField6Text", "Additional Field Value 6");
		mapa.put("additionalField7Text", "Additional Field Value 7");
		
		mapa.put("interruptVoltageSupplyTxt", "Interrupt the voltage supply to the crane");
		mapa.put("interruptVoltageSupplyChk", "Interrupt the voltage supply to the crane <Checkbox>");
		
		mapa.put("circuitDiagramPositionTxt", "Check that the circuit diagram is in position in the switch cabinet");
		mapa.put("circuitDiagramPositionChk", "Check that the circuit diagram is in position in the switch cabinet <Checkbox>");
		
		mapa.put("warningSignsTxt", "Check that the warning signs on the switch cabinet");
		mapa.put("warningSignsChk", "Check that the warning signs on the switch cabinet <Checkbox>");
		
		mapa.put("cablesSignsTxt", "Check all cables for signs of outer damage");
		mapa.put("cablesSignsChk", "Check all cables for signs of outer damage <Checkbox>");
		
		mapa.put("screwedCableGlandsTxt", "Check all the screwed cable glands for signs of outer damage and a firm fit");
		mapa.put("screwedCableGlandsChk", "Check all the screwed cable glands for signs of outer damage and a firm fit <Checkbox>");
		
		mapa.put("openSwitchCabinetCoverTxt", "Open the switch cabinet cover and check the firm fit of the cables.");
		mapa.put("openSwitchCabinetCoverChk", "Open the switch cabinet cover and check the firm fit of the cables. <Checkbox>");
		
		mapa.put("checkOperatingUnitTxt", "Check the operating unit for outer damage");
		mapa.put("checkOperatingUnitChk", "Check the operating unit for outer damage <Checkbox>");
		
		mapa.put("checkLimitSwitchesTxt", "Check the limit switches for signs of outer damage");
		mapa.put("checkLimitSwitchesChk", "Check the limit switches for signs of outer damage <Checkbox>");
		
		mapa.put("setVoltageSupplyTxt", "Set up the voltage supply again.");
		mapa.put("setVoltageSupplyChk", "Set up the voltage supply again. <Checkbox>");
		
		mapa.put("checkMotorBrakeTxt", "Check that the motor brake is working");
		mapa.put("checkMotorBrakeChk", "Check that the motor brake is working <Checkbox>");
		
		mapa.put("checkRopeMechanicalDamageTxt", "Check the rope for mechanical damage such as breaks");
		mapa.put("checkRopeMechanicalDamageChk", "Check the rope for mechanical damage such as breaks <Checkbox>");
		
		mapa.put("checkLoadHookMechanicalTxt", "Check the load hook for mechanical damage");
		mapa.put("checkLoadHookMechanicalChk", "Check the load hook for mechanical damage <Checkbox>");
		
		mapa.put("carryVisualInspectionTxt", "Carry out a visual inspection of the drive unit for damage and leaks.");
		mapa.put("carryVisualInspectionChk", "Carry out a visual inspection of the drive unit for damage and leaks. <Checkbox>");
		
		mapa.put("usefeelerGaugeTxt", "Use a feeler gauge to check the brake gap");
		mapa.put("usefeelerGaugeChk", "Use a feeler gauge to check the brake gap <Checkbox>");
		
		mapa.put("checkLimitSwitchRockersTxt", "Check the limit switch rockers to make sure they are present and move easily");
		mapa.put("checkLimitSwitchRockersChk", "Check the limit switch rockers to make sure they are present and move easily <Checkbox>");
		
		mapa.put("checkCraneBridgeTxt", "Check that the crane bridge is safely locked in place and that the locking lever is a firm fit");
		mapa.put("checkCraneBridgeChk", "Check that the crane bridge is safely locked in place and that the locking lever is a firm fit <Checkbox>");
		
		mapa.put("checkCrabTxt", "Check that the crab is safely locked in place and that the locking lever is a firm fit");
		mapa.put("checkCrabChk", "Check that the crab is safely locked in place and that the locking lever is a firm fit <Checkbox>");
		
		mapa.put("checkDeflectionRollersTxt", "Check the deflection rollers and the respective components");
		mapa.put("checkDeflectionRollersChk", "Check the deflection rollers and the respective components <Checkbox>");
		
		mapa.put("checkScrewJointsTxt", "Check all the screw joints to make sure they are in position and a firm fit");
		mapa.put("checkScrewJointsChk", "Check all the screw joints to make sure they are in position and a firm fit <Checkbox>");
		
		mapa.put("checkAllRollersBridgeTxt", "Check all the rollers on the bridge and on the crab for mechanical damage");
		mapa.put("checkAllRollersBridgeChk",  "Check all the rollers on the bridge and on the crab for mechanical damage <Checkbox>");
		
		mapa.put("checkAllStopBuffersTxt", "Check all the stop buffers to make sure they are in position and have no mechanical damage");
		mapa.put("checkAllStopBuffersChk", "Check all the stop buffers to make sure they are in position and have no mechanical damage <Checkbox>");
		
		mapa.put("checkAllComponentsTxt", "Check all the components to make sure the coating");
		mapa.put("checkAllComponentsChk", "Check all the components to make sure the coating <Checkbox>");
		
		mapa.put("ancorPointSafetyEquipmentTxt", "Ancor point safety equipment");
		mapa.put("ancorPointSafetyEquipmentChk", "Ancor point safety equipment <Checkbox>");
		
		mapa.put("loadTestTxt", "Load test");
		mapa.put("loadTestChk", "Load test <Checkbox>");
		
		return mapa;
	}

}
