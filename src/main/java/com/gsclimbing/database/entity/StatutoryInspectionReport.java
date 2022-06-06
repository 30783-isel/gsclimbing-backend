package com.gsclimbing.database.entity;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_report_ladder_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
	private StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "internal_crane_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
	private StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_report_service_cabin_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
	private StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "inspection_anchor_points_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
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
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("wtgType", "WTG Type");
		mapa.put("yearConstruction", "Year of Construction");

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
		
		
		
		
		
		
		
		
		
		
		mapa.put("inspectionReportServiceCabin1Txt", "Visual inspection of Service lift. Visual Damage?");
		mapa.put("inspectionReportServiceCabin2Txt", "Connection Hoist and Stirrup is secured with nylock-nuts");
		mapa.put("inspectionReportServiceCabin3Txt", "All bolts and nylock nuts are fit and secured");
		mapa.put("inspectionReportServiceCabin4Txt", "Documents available in Service lift");
		mapa.put("inspectionReportServiceCabin5Txt", "All warning labels are still properly in place and readable.");
		mapa.put("inspectionReportServiceCabin6Txt", "Galleries are correct? (see 38916-OM-E, section");
		mapa.put("inspectionReportServiceCabin7Txt", "Locking system present on gallery?");
		mapa.put("inspectionReportServiceCabin8Txt", "All electrical cables strapped and not loose");
		mapa.put("inspectionReportServiceCabin9Txt", "MAIN SWITCH mounted on gallery");
		mapa.put("inspectionReportServiceCabin10Txt", "LOCKING SYSTEM on gallery: transfer key can only be removed, if gate is locked.");
		mapa.put("inspectionReportServiceCabin11Txt", "POWER ON LIGHT INSIDE SERVICE LIFT");
		mapa.put("inspectionReportServiceCabin12Txt", "Installation double storage bin correct? (450mm - 500mm under bottom landing");
		mapa.put("inspectionReportServiceCabin13Txt", "Proper collectioning of electical supply cable in storage bin?");
		mapa.put("inspectionReportServiceCabin14Txt", "Tension on the Guiding wire ropes is ok? (see position of red mark)");
		mapa.put("inspectionReportServiceCabin15Txt", "Rubber protection in all holes?");
		mapa.put("inspectionReportServiceCabin16Txt", "10kg Weights position and \"Suspension wire rope\" can rotate freely?");
		mapa.put("inspectionReportServiceCabin17Txt", "Refer notes, no._EMERGENCY STOP: up & down disabled; red safety integrity light is ON");
		mapa.put("inspectionReportServiceCabin18Txt", "DOOR SWITCH: if door is open, up & down disabled; red safety integrity light is ON ");
		mapa.put("inspectionReportServiceCabin19Txt", "TRANSFER KEY SWITCH: if not switched on, then up & down disabled; red light is ON");
		mapa.put("inspectionReportServiceCabin20Txt", "INSIDE steering: UP / DOWN control ");
		mapa.put("inspectionReportServiceCabin21Txt", "Control UP; safe zone (green light OFF); door locked");
		mapa.put("inspectionReportServiceCabin22Txt", "BOTTOM LIMIT BY-PASS. (Pay attention to the weights on the suspension cable) ");
		mapa.put("inspectionReportServiceCabin23Txt", "ULTIMATE BOTTOM LIMIT (bott. Trip plate):up & down disabled;red safety integrity light ON ");
		mapa.put("inspectionReportServiceCabin24Txt", "Check 'NO POWER' DESCENT' (Make sure speed will not increase) ");
		mapa.put("inspectionReportServiceCabin25Txt", "Manually trip overspeed and verify down direction is blocked (Electrically and with Brake)");
		mapa.put("inspectionReportServiceCabin26Txt", "Check hand wheel for damage and check funtionality to reset");
		mapa.put("inspectionReportServiceCabin27Txt", "OVERLOAD FUNCTIONALITY: 240kg (nominal operation); 300kg (up & down disabled)");
		mapa.put("inspectionReportServiceCabin28Txt", "TOP LIMIT: up disabled, down still enabled");
		mapa.put("inspectionReportServiceCabin29Txt", "ULTIMATE TOP LIMIT: up & down disabled; red safety integrity light is ON");
		mapa.put("inspectionReportServiceCabin30Txt", "ELECTRIC SUPPLY PLUG: check for fixation and damage. ");
		mapa.put("inspectionReportServiceCabin31Txt", "Bottom floor connection bolts");
		mapa.put("inspectionReportServiceCabin32Txt", "Disconnect motor-plug, (X6) Create overspeed (by opening brake)");
		mapa.put("inspectionReportServiceCabin33Txt", "Spray 1m of suspension wire and lift service lift up and down to let lubricated wire inside the hoist.");
		mapa.put("inspectionReportServiceCabin34Txt", "OUTSIDE steering: SET, then DOWN control (person on first level holds top limit to activate)");
		mapa.put("inspectionReportServiceCabin35Txt", "OUTSIDE steering: SET, then UP control (stop with main switch)");
		mapa.put("inspectionReportServiceCabin36Txt", "Check the condition of the 8.4mm Suspension wire rope.");
		mapa.put("inspectionReportServiceCabin37Txt", "Check the condition of the 8.4mm Safety wire rope.");
		mapa.put("inspectionReportServiceCabin38Txt", "Check the condition of both Guiding wires");
		mapa.put("inspectionReportServiceCabin39Txt", "Wire guides and wire fixes are working correctly");
		mapa.put("inspectionReportServiceCabin40Txt", "Guiding wires do not show any damage.");
		mapa.put("inspectionReportServiceCabin41Txt", "Verify that axial sway of service lift between two landings is acceptable and does not have any");
		mapa.put("inspectionReportServiceCabin42Txt", "Safezone (green light ON); door unlocked");
		mapa.put("inspectionReportServiceCabin43Txt", "LOCKING SYSTEM on gallery: gate can only be unlocked with transfer key");
		mapa.put("inspectionReportServiceCabin44Txt", "Galleries are correct? (see 38913-OM-E, section \"Galleries According MD2006/42/EC\")");
		mapa.put("inspectionReportServiceCabin45Txt", "Safezone (green light ON); door unlocked ");
		mapa.put("inspectionReportServiceCabin46Txt", "LOCKING SYSTEM on gallery: gate can only be unlocked with transfer key");
		mapa.put("inspectionReportServiceCabin47Txt", "Galleries are correct? (see 38913-OM-E, section \"Galleries According MD2006/42/EC\")");
		mapa.put("inspectionReportServiceCabin48Txt", "Lift will stop on equal level as the gallery? If not change position of striker plate");
		mapa.put("inspectionReportServiceCabin49Txt", "Check striker plate fixation.");
		mapa.put("inspectionReportServiceCabin50Txt", "Connection of Guiding steel wires. (min. 2Ton Shackle and lock pins facing to thge front)");
		mapa.put("inspectionReportServiceCabin51Txt", "Connection of 8.4mm steel wires. (min. 2Ton Shackle and lock pins facing to the front)");
		mapa.put("inspectionReportServiceCabin52Txt", "Condition of Shackles and check for wear.");
		mapa.put("inspectionReportServiceCabin53Txt", "When replacing the Steel-wires check hole in Suspension beam for damage");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		mapa.put("internalCrane1Txt", "Reading of the operating hours ");
		mapa.put("internalCrane2Txt", "Interrupt the voltage supply to the crane by removing the CEE plug from the top box in the powerhouse.");
		mapa.put("internalCrane3Txt", "Check that the circuit diagram is in position in the switch cabinet and is still legible");
		mapa.put("internalCrane4Txt", "Check that the warning signs on the switch cabinet");
		mapa.put("internalCrane5Txt", "Check all cables for signs of outer damage and firm attachment to the terminals");
		mapa.put("internalCrane6Txt", "Check all the screwed cable glands for signs of outer damage and a firm fit");
		mapa.put("internalCrane7Txt", "Open the switch cabinet cover and check the firm fit of the cables.");
		mapa.put("internalCrane8Txt", "Check the operating unit for outer damage, a firm cable fit and make sure strain relief is working.");
		mapa.put("internalCrane9Txt", "Check the limit switches for signs of outer damage, a firm cable fit and ease of movement of the switching rockers");
		mapa.put("internalCrane10Txt", "Set up the voltage supply again.");
		mapa.put("internalCrane11Txt", "Check that the motor brake is working.");
		mapa.put("internalCrane12Txt", "Check the rope for mechanical damage such as breaks");
		mapa.put("internalCrane13Txt", "Check the load hook for mechanical damage");
		mapa.put("internalCrane14Txt", "Carry out a visual inspection of the drive unit for damage and leaks.");
		mapa.put("internalCrane15Txt", "Use a feeler gauge to check the brake gap, it has to be 0.3 - 0.8 mm.");
		mapa.put("internalCrane16Txt", "Check the  limit switch rockers to make sure they are present and move easily.");
		mapa.put("internalCrane17Txt", "Check that the crane bridge is safely locked in place and that the locking lever is a firm fit");
		mapa.put("internalCrane18Txt", "Check that the crab is safely locked in place and that the locking lever is a firm fit");
		mapa.put("internalCrane19Txt", "Check the deflection rollers and the respective components");
		mapa.put("internalCrane20Txt", "Check all the screw joints to make sure they are in position and a firm fit");
		mapa.put("internalCrane21Txt", "Check all the rollers on the bridge and on the crab for mechanical damage");
		mapa.put("internalCrane22Txt", "Check all the stop buffers to make sure they are in position and have no mechanical damage");
		mapa.put("internalCrane23Txt", "Check all the components to make sure the coating");
		mapa.put("internalCrane24Txt", "Load test");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		mapa.put("inspectionReportLadder1Txt", "Is ladder secure and tight to structure");
		mapa.put("inspectionReportLadder2Txt", "Are ladder brackets attached");
		mapa.put("inspectionReportLadder3Txt", "Are all fasteners in place");
		mapa.put("inspectionReportLadder4Txt", "Are all welds in good condition");
		mapa.put("inspectionReportLadder5Txt", "Are connections between rais ok");
		mapa.put("inspectionReportLadder6Txt", "Any bent rails/rungs");
		mapa.put("inspectionReportLadder7Txt", "Any cracked rails/rungs");
		mapa.put("inspectionReportLadder8Txt", "Is red rust/corrosion present");
		mapa.put("inspectionReportLadder9Txt", "Unobstucted climb path");
		mapa.put("inspectionReportLadder10Txt", "Is the overall condition safe");
		mapa.put("inspectionReportLadder11Txt", "Is assembly secure and tight to Double-T");
		mapa.put("inspectionReportLadder12Txt", "Are attachment fasteners in place");
		mapa.put("inspectionReportLadder13Txt", "Is red rust/corrosion present");
		mapa.put("inspectionReportLadder14Txt", "Are swaged fittings in good condition");
		mapa.put("inspectionReportLadder15Txt", "Is eye bolt worn/cracked");
		mapa.put("inspectionReportLadder16Txt", "Are welds in good condition");
		mapa.put("inspectionReportLadder17Txt", "Any sign of wear or damage");
		mapa.put("inspectionReportLadder18Txt", "Is cable termination in good condition");
		mapa.put("inspectionReportLadder19Txt", "Does the overall condition appear safe");
		mapa.put("inspectionReportLadder20Txt", "Is assembly secure and tight to the structure");
		mapa.put("inspectionReportLadder21Txt", "Are channels secure to rungs");
		mapa.put("inspectionReportLadder22Txt", "Are all fasteners present and secure");
		mapa.put("inspectionReportLadder23Txt", "Is head anchor attachment secured");
		mapa.put("inspectionReportLadder24Txt", "Are welds free of cracks");
		mapa.put("inspectionReportLadder25Txt", "Are rungs bent or worn");
		mapa.put("inspectionReportLadder26Txt", "Is any red rust/corrosion present");
		mapa.put("inspectionReportLadder27Txt", "Does the overall condition appear safe");
		mapa.put("inspectionReportLadder28Txt", "Does the cable show any flattened,frayed or kinked section/areas along the overall length");
		mapa.put("inspectionReportLadder29Txt", "Is the assembly secure to the head assembly");
		mapa.put("inspectionReportLadder30Txt", "Is the cable tight to the tower face");
		mapa.put("inspectionReportLadder31Txt", "Is the climb path free of obstuction");
		mapa.put("inspectionReportLadder32Txt", "Is the cable attached through the cable guides");
		mapa.put("inspectionReportLadder33Txt", "Does the cable show signs of red rust");
		mapa.put("inspectionReportLadder34Txt", "Does the cable show signs of electrical arcing/burns");
		mapa.put("inspectionReportLadder35Txt", "Does the overall condition appear safe");
		mapa.put("inspectionReportLadder36Txt", "Are cable guides provided every 20 to 40 ft");
		mapa.put("inspectionReportLadder37Txt", "Are all fasteners present");
		mapa.put("inspectionReportLadder38Txt", "Are cable guides secure to the structure");
		mapa.put("inspectionReportLadder39Txt", "Are ladder rungs in good condition");
		mapa.put("inspectionReportLadder40Txt", "Are rubber grommets in good condition");
		mapa.put("inspectionReportLadder41Txt", "Is there any sign of red rust/corrosion");
		mapa.put("inspectionReportLadder42Txt", "Is assembly secure to the structure");
		mapa.put("inspectionReportLadder43Txt", "Are all clamp brackets/fasteners present");
		mapa.put("inspectionReportLadder44Txt", "Are the cable clips present and secure");
		mapa.put("inspectionReportLadder45Txt", "Is the cable taut at the assembly");
		mapa.put("inspectionReportLadder46Txt", "Is any rust/corrosion present");
		mapa.put("inspectionReportLadder47Txt", "Does the overall condition appear safe");
		mapa.put("inspectionReportLadder48Txt", "Is the cable safe climb system secure");
		mapa.put("inspectionReportLadder49Txt", "Does the overall condition appear safe");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		mapa.put("reportNumber", "");

		mapa.put("inspectionDescenderDeviceManufacturer", "Manufacturer descender device");
		mapa.put("inspectionDescenderDeviceType", "Type");
		mapa.put("inspectionDescenderDeviceYearBuild", "Year build");
		mapa.put("inspectionDescenderDeviceSerialNumber", "Serial number");
		mapa.put("inspectionDescenderDeviceTypePlateTestBadge", "Type plate and test badge");

		mapa.put("inspectionDescenderDeviceInspectors", "Inspectors");
		mapa.put("inspectionDescenderDeviceDate", "Date");
		mapa.put("inspectionDescenderDeviceResultOfInspection", "Result of inspection");
		mapa.put("inspectionDescenderDeviceRepairRequired", "Repair required");
		mapa.put("inspectionDescenderDeviceNextInspection", "Next inspection");

		mapa.put("inspectionDescenderDevice1Txt", "Visual check of descender device");
		mapa.put("inspectionDescenderDevice2Txt", "Visual check rope");
		mapa.put("inspectionDescenderDevice3Txt", "Visual check sling and carabiner");
		mapa.put("inspectionDescenderDevice4Txt", "Do function test");
		mapa.put("inspectionDescenderDevice5Txt", "Fill document");
		mapa.put("inspectionDescenderDevice6Txt", "Seal back and put sticker on");

		mapa.put("inspectionDescenderDeviceNotes", "Notes");
		
		
		
		
		
		
		
		
		
		
		
		
		mapa.put("inspectionAnchorPoints1Txt", "Inspect the anchorage device for legible serial");
		mapa.put("inspectionAnchorPoints2Txt", "Check that the anchorage device is tight in position");
		mapa.put("inspectionAnchorPoints3Txt", "Check that the bolt is going all way through the threaded hole of eye nut");
		mapa.put("inspectionAnchorPoints4Txt", "Inspect the steel surface for cracks and corrosion");
		mapa.put("inspectionAnchorPoints5Txt", "Record inspection");
		mapa.put("inspectionAnchorPoints6Txt", "List serial numbers.If anchoage device has no serial number");
		mapa.put("inspectionAnchorPoints7Txt",  "Anchorage point serial number - Serial no/position: 1 ");
		mapa.put("inspectionAnchorPoints8Txt",  "Anchorage point serial number - Serial no/position: 2 ");
		mapa.put("inspectionAnchorPoints9Txt",  "Anchorage point serial number - Serial no/position: 3 ");
		mapa.put("inspectionAnchorPoints10Txt", "Anchorage point serial number - Serial no/position: 4 ");
		mapa.put("inspectionAnchorPoints11Txt", "Anchorage point serial number - Serial no/position: 5 ");
		mapa.put("inspectionAnchorPoints12Txt", "Anchorage point serial number - Serial no/position: 6 ");
		mapa.put("inspectionAnchorPoints13Txt", "Anchorage point serial number - Serial no/position: 7 ");
		mapa.put("inspectionAnchorPoints14Txt", "Anchorage point serial number - Serial no/position: 8 ");
		mapa.put("inspectionAnchorPoints15Txt", "Anchorage point serial number - Serial no/position: 9 ");
		mapa.put("inspectionAnchorPoints16Txt", "Anchorage point serial number - Serial no/position: 10");
		mapa.put("inspectionAnchorPoints17Txt", "Anchorage point serial number - Serial no/position: 11");
		mapa.put("inspectionAnchorPoints18Txt", "Anchorage point serial number - Serial no/position: 12");
		mapa.put("inspectionAnchorPoints19Txt", "Anchorage point serial number - Serial no/position: 13");
		mapa.put("inspectionAnchorPoints20Txt", "Anchorage point serial number - Serial no/position: 14");
		mapa.put("inspectionAnchorPoints21Txt", "Anchorage point serial number - Serial no/position: 15");
		mapa.put("inspectionAnchorPoints22Txt", "Anchorage point serial number - Serial no/position: 16");
		mapa.put("inspectionAnchorPoints23Txt", "Anchorage point serial number - Serial no/position: 17");
		mapa.put("inspectionAnchorPoints24Txt", "Anchorage point serial number - Serial no/position: 18");
		mapa.put("inspectionAnchorPoints25Txt", "Anchorage point serial number - Serial no/position: 19");
		mapa.put("inspectionAnchorPoints26Txt", "Anchorage point serial number - Serial no/position: 20");
		mapa.put("inspectionAnchorPoints27Txt", "Anchorage point serial number - Serial no/position: 21");
		mapa.put("inspectionAnchorPoints28Txt", "Anchorage point serial number - Serial no/position: 22");
		mapa.put("inspectionAnchorPoints29Txt", "Anchorage point serial number - Serial no/position: 23");
		mapa.put("inspectionAnchorPoints30Txt", "Anchorage point serial number - Serial no/position: 24");

		return mapa;
	}
}
