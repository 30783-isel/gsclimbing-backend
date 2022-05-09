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
@Table(name="statutory_inspection_report_inspection_report_ladder ")
public class StatutoryInspectionReportInspectionReportLadder implements StatutoryInspectionReportInt, Cloneable, Serializable{

	private static final long serialVersionUID = 1431932367462628800L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
    
    @OneToOne(mappedBy = "statutoryInspectionReportInspectionReportLadder")
    private StatutoryInspectionReport statutoryInspectionReport;
    
	private String inspectionReportLadderManufacturer;
	private String inspectionReportLadderType;
	private String inspectionReportLadderSerialNumber;
	private String inspectionReportLadderManufacturerFallArrestSystem;
	private String inspectionReportLadderType2;
	private String inspectionReportLadderSerialNumber2;
	private String inspectionReportLadderTypePlateTestBadge;

	private String inspectionReportLadderInspectors;
	private String inspectionReportLadderDate;
	private String inspectionReportLadderResultInspection;
	private String inspectionReportLadderRepairRequired;
	private String inspectionReportLadderNextInspection;

	private boolean inspectionReportLadder1Chk;
	private String inspectionReportLadder1Txt;
	private boolean inspectionReportLadder2Chk ;
	private String inspectionReportLadder2Txt ;
	private boolean inspectionReportLadder3Chk ;
	private String inspectionReportLadder3Txt ;
	private boolean inspectionReportLadder4Chk ;
	private String inspectionReportLadder4Txt ;
	private boolean inspectionReportLadder5Chk ;
	private String inspectionReportLadder5Txt ;
	private boolean inspectionReportLadder6Chk ;
	private String inspectionReportLadder6Txt ;
	private boolean inspectionReportLadder7Chk ;
	private String inspectionReportLadder7Txt ;
	private boolean inspectionReportLadder8Chk ;
	private String inspectionReportLadder8Txt ;
	private boolean inspectionReportLadder9Chk ;
	private String inspectionReportLadder9Txt ;
	private boolean inspectionReportLadder10Chk;
	private String inspectionReportLadder10Txt;
	private boolean inspectionReportLadder11Chk;
	private String inspectionReportLadder11Txt;
	private boolean inspectionReportLadder12Chk;
	private String inspectionReportLadder12Txt;
	private boolean inspectionReportLadder13Chk;
	private String inspectionReportLadder13Txt;
	private boolean inspectionReportLadder14Chk;
	private String inspectionReportLadder14Txt;
	private boolean inspectionReportLadder15Chk;
	private String inspectionReportLadder15Txt;
	private boolean inspectionReportLadder16Chk;
	private String inspectionReportLadder16Txt;
	private boolean inspectionReportLadder17Chk;
	private String inspectionReportLadder17Txt;
	private boolean inspectionReportLadder18Chk;
	private String inspectionReportLadder18Txt;
	private boolean inspectionReportLadder19Chk;
	private String inspectionReportLadder19Txt;
	private boolean inspectionReportLadder20Chk;
	private String inspectionReportLadder20Txt;
	private boolean inspectionReportLadder21Chk;
	private String inspectionReportLadder21Txt;
	private boolean inspectionReportLadder22Chk;
	private String inspectionReportLadder22Txt;
	private boolean inspectionReportLadder23Chk;
	private String inspectionReportLadder23Txt;
	private boolean inspectionReportLadder24Chk;
	private String inspectionReportLadder24Txt;
	private boolean inspectionReportLadder25Chk;
	private String inspectionReportLadder25Txt;
	private boolean inspectionReportLadder26Chk;
	private String inspectionReportLadder26Txt;
	private boolean inspectionReportLadder27Chk;
	private String inspectionReportLadder27Txt;
	private boolean inspectionReportLadder28Chk;
	private String inspectionReportLadder28Txt;
	private boolean inspectionReportLadder29Chk;
	private String inspectionReportLadder29Txt;
	private boolean inspectionReportLadder30Chk;
	private String inspectionReportLadder30Txt;
	private boolean inspectionReportLadder31Chk;
	private String inspectionReportLadder31Txt;
	private boolean inspectionReportLadder32Chk;
	private String inspectionReportLadder32Txt;
	private boolean inspectionReportLadder33Chk;
	private String inspectionReportLadder33Txt;
	private boolean inspectionReportLadder34Chk;
	private String inspectionReportLadder34Txt;
	private boolean inspectionReportLadder35Chk;
	private String inspectionReportLadder35Txt;
	private boolean inspectionReportLadder36Chk;
	private String inspectionReportLadder36Txt;
	private boolean inspectionReportLadder37Chk;
	private String inspectionReportLadder37Txt;
	private boolean inspectionReportLadder38Chk;
	private String inspectionReportLadder38Txt;
	private boolean inspectionReportLadder39Chk;
	private String inspectionReportLadder39Txt;
	private boolean inspectionReportLadder40Chk;
	private String inspectionReportLadder40Txt;
	private boolean inspectionReportLadder41Chk;
	private String inspectionReportLadder41Txt;
	private boolean inspectionReportLadder42Chk;
	private String inspectionReportLadder42Txt;
	private boolean inspectionReportLadder43Chk;
	private String inspectionReportLadder43Txt;
	private boolean inspectionReportLadder44Chk;
	private String inspectionReportLadder44Txt;
	private boolean inspectionReportLadder45Chk;
	private String inspectionReportLadder45Txt;
	private boolean inspectionReportLadder46Chk;
	private String inspectionReportLadder46Txt;
	private boolean inspectionReportLadder47Chk;
	private String inspectionReportLadder47Txt;
	private boolean inspectionReportLadder48Chk;
	private String inspectionReportLadder48Txt;
	private boolean inspectionReportLadder49Chk;
	private String inspectionReportLadder49Txt;

	private String inspectionReportLadderNotes;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
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
		return mapa;
	}
    
}
