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
@Table(name="statutory_inspection_report_internal_crane")
public class StatutoryInspectionReportInternalCrane  implements StatutoryInspectionReportInt, Cloneable, Serializable{

	private static final long serialVersionUID = -8959005404213795630L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
    @OneToOne(mappedBy = "statutoryInspectionReportInternalCrane")
    private StatutoryInspectionReport statutoryInspectionReport;

	private String internalCraneManufacturer_;
	private String internalCraneType_;
	private String internalCraneSerialNumber_;
	private String internalCraneYearBuild;
	private String internalCraneTypePlateTestBadge;

	private String internalCraneInspectors;
	private String internalCraneDate;
	private String internalCraneResultInspection;
	private String internalCraneRepairRequired;
	private String internalCraneNextInspection;

	private boolean internalCrane1Chk;
	private String internalCrane1Txt ;
	private boolean internalCrane2Chk ;
	private String internalCrane2Txt ;
	private boolean internalCrane3Chk ;
	private String internalCrane3Txt ;
	private boolean internalCrane4Chk ;
	private String internalCrane4Txt ;
	private boolean internalCrane5Chk ;
	private String internalCrane5Txt ;
	private boolean internalCrane6Chk ;
	private String internalCrane6Txt ;
	private boolean internalCrane7Chk ;
	private String internalCrane7Txt ;
	private boolean internalCrane8Chk ;
	private String internalCrane8Txt ;
	private boolean internalCrane9Chk ;
	private String internalCrane9Txt ;
	private boolean internalCrane10Chk;
	private String internalCrane10Txt;
	private boolean internalCrane11Chk;
	private String internalCrane11Txt;
	private boolean internalCrane12Chk;
	private String internalCrane12Txt;
	private boolean internalCrane13Chk;
	private String internalCrane13Txt;
	private boolean internalCrane14Chk;
	private String internalCrane14Txt;
	private boolean internalCrane15Chk;
	private String internalCrane15Txt;
	private boolean internalCrane16Chk;
	private String internalCrane16Txt;
	private boolean internalCrane17Chk;
	private String internalCrane17Txt;
	private boolean internalCrane18Chk;
	private String internalCrane18Txt;
	private boolean internalCrane19Chk;
	private String internalCrane19Txt;
	private boolean internalCrane20Chk;
	private String internalCrane20Txt;
	private boolean internalCrane21Chk;
	private String internalCrane21Txt;
	private boolean internalCrane22Chk;
	private String internalCrane22Txt;
	private boolean internalCrane23Chk;
	private String internalCrane23Txt;
	private boolean internalCrane24Chk;
	private String internalCrane24Txt;

	private String internalCraneNotes;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public HashMap<String, String> mapeamento() {
		
		HashMap<String, String> mapa = new HashMap<String, String>();
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
		return mapa;
	}
	
}
