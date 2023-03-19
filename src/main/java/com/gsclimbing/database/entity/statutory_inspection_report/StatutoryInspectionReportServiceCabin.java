package com.gsclimbing.database.entity.statutory_inspection_report;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.StatutoryInspectionReportInt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statutory_inspection_report_service_cabin")
public class StatutoryInspectionReportServiceCabin implements StatutoryInspectionReportInt, Cloneable, Serializable{

	private static final long serialVersionUID = 5153989820053889377L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
    @OneToOne(mappedBy = "statutoryInspectionReportServiceCabin")
    private StatutoryInspectionReport statutoryInspectionReport;

	@Column(length = 100)
	private String inspectionReportServiceCabinManufacturer;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinType;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinSerialNumber;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinSerialNumberHoist;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinYearBuild;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin5yearInspectionRequired;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinHourMeterReading;

	@Column(length = 100)
	private String inspectionReportServiceCabinInspectors;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinDate;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinResultOfInspection;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinRepairRequired;
	
	@Column(length = 100)
	private String inspectionReportServiceCabinNextInspection;
	@Column(length = 100)
	private String inspectionReportServiceCabinNextInspectionSticker;
	
	private boolean inspectionReportServiceCabin1Chk ;
	private boolean inspectionReportServiceCabin1Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin1Txt ;
	private boolean inspectionReportServiceCabin2Chk ;
	private boolean inspectionReportServiceCabin2Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin2Txt ;
	private boolean inspectionReportServiceCabin3Chk ;
	private boolean inspectionReportServiceCabin3Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin3Txt ;
	private boolean inspectionReportServiceCabin4Chk ;
	private boolean inspectionReportServiceCabin4Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin4Txt ;
	private boolean inspectionReportServiceCabin5Chk ;
	private boolean inspectionReportServiceCabin5Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin5Txt ;
	private boolean inspectionReportServiceCabin6Chk ;
	private boolean inspectionReportServiceCabin6Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin6Txt ;
	private boolean inspectionReportServiceCabin7Chk ;
	private boolean inspectionReportServiceCabin7Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin7Txt ;
	private boolean inspectionReportServiceCabin8Chk ;
	private boolean inspectionReportServiceCabin8Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin8Txt ;
	private boolean inspectionReportServiceCabin9Chk ;
	private boolean inspectionReportServiceCabin9Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin9Txt ;
	private boolean inspectionReportServiceCabin10Chk;
	private boolean inspectionReportServiceCabin10Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin10Txt;
	private boolean inspectionReportServiceCabin11Chk;
	private boolean inspectionReportServiceCabin11Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin11Txt;
	private boolean inspectionReportServiceCabin12Chk;
	private boolean inspectionReportServiceCabin12Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin12Txt;
	private boolean inspectionReportServiceCabin13Chk;
	private boolean inspectionReportServiceCabin13Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin13Txt;
	private boolean inspectionReportServiceCabin14Chk;
	private boolean inspectionReportServiceCabin14Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin14Txt;
	private boolean inspectionReportServiceCabin15Chk;
	private boolean inspectionReportServiceCabin15Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin15Txt;
	private boolean inspectionReportServiceCabin16Chk;
	private boolean inspectionReportServiceCabin16Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin16Txt;
	private boolean inspectionReportServiceCabin17Chk;
	private boolean inspectionReportServiceCabin17Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin17Txt;
	private boolean inspectionReportServiceCabin18Chk;
	private boolean inspectionReportServiceCabin18Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin18Txt;
	private boolean inspectionReportServiceCabin19Chk;
	private boolean inspectionReportServiceCabin19Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin19Txt;
	private boolean inspectionReportServiceCabin20Chk;
	private boolean inspectionReportServiceCabin20Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin20Txt;
	private boolean inspectionReportServiceCabin21Chk;
	private boolean inspectionReportServiceCabin21Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin21Txt;
	private boolean inspectionReportServiceCabin22Chk;
	private boolean inspectionReportServiceCabin22Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin22Txt;
	private boolean inspectionReportServiceCabin23Chk;
	private boolean inspectionReportServiceCabin23Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin23Txt;
	private boolean inspectionReportServiceCabin24Chk;
	private boolean inspectionReportServiceCabin24Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin24Txt;
	private boolean inspectionReportServiceCabin25Chk;
	private boolean inspectionReportServiceCabin25Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin25Txt;
	private boolean inspectionReportServiceCabin26Chk;
	private boolean inspectionReportServiceCabin26Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin26Txt;
	private boolean inspectionReportServiceCabin27Chk;
	private boolean inspectionReportServiceCabin27Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin27Txt;
	private boolean inspectionReportServiceCabin28Chk;
	private boolean inspectionReportServiceCabin28Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin28Txt;
	private boolean inspectionReportServiceCabin29Chk;
	private boolean inspectionReportServiceCabin29Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin29Txt;
	private boolean inspectionReportServiceCabin30Chk;
	private boolean inspectionReportServiceCabin30Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin30Txt;
	private boolean inspectionReportServiceCabin31Chk;
	private boolean inspectionReportServiceCabin31Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin31Txt;
	private boolean inspectionReportServiceCabin32Chk;
	private boolean inspectionReportServiceCabin32Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin32Txt;
	private boolean inspectionReportServiceCabin33Chk;
	private boolean inspectionReportServiceCabin33Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin33Txt;
	private boolean inspectionReportServiceCabin34Chk;
	private boolean inspectionReportServiceCabin34Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin34Txt;
	private boolean inspectionReportServiceCabin35Chk;
	private boolean inspectionReportServiceCabin35Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin35Txt;
	private boolean inspectionReportServiceCabin36Chk;
	private boolean inspectionReportServiceCabin36Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin36Txt;
	private boolean inspectionReportServiceCabin37Chk;
	private boolean inspectionReportServiceCabin37Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin37Txt;
	private boolean inspectionReportServiceCabin38Chk;
	private boolean inspectionReportServiceCabin38Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin38Txt;
	private boolean inspectionReportServiceCabin39Chk;
	private boolean inspectionReportServiceCabin39Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin39Txt;
	private boolean inspectionReportServiceCabin40Chk;
	private boolean inspectionReportServiceCabin40Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin40Txt;
	private boolean inspectionReportServiceCabin41Chk;
	private boolean inspectionReportServiceCabin41Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin41Txt;
	private boolean inspectionReportServiceCabin42Chk;
	private boolean inspectionReportServiceCabin42Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin42Txt;
	private boolean inspectionReportServiceCabin43Chk;
	private boolean inspectionReportServiceCabin43Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin43Txt;
	private boolean inspectionReportServiceCabin44Chk;
	private boolean inspectionReportServiceCabin44Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin44Txt;
	private boolean inspectionReportServiceCabin45Chk;
	private boolean inspectionReportServiceCabin45Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin45Txt;
	private boolean inspectionReportServiceCabin46Chk;
	private boolean inspectionReportServiceCabin46Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin46Txt;
	private boolean inspectionReportServiceCabin47Chk;
	private boolean inspectionReportServiceCabin47Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin47Txt;
	private boolean inspectionReportServiceCabin48Chk;
	private boolean inspectionReportServiceCabin48Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin48Txt;
	private boolean inspectionReportServiceCabin49Chk;
	private boolean inspectionReportServiceCabin49Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin49Txt;
	private boolean inspectionReportServiceCabin50Chk;
	private boolean inspectionReportServiceCabin50Chkn;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin50Txt;
	private boolean inspectionReportServiceCabin51Chk ;
	private boolean inspectionReportServiceCabin51Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin51Txt ;
	private boolean inspectionReportServiceCabin52Chk ;
	private boolean inspectionReportServiceCabin52Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin52Txt ;
	private boolean inspectionReportServiceCabin53Chk ;
	private boolean inspectionReportServiceCabin53Chkn ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin53Txt ;

	@Column(length = 100)
	private String inspectionReportServiceCabinNotes;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
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

		return mapa;
	}
}
