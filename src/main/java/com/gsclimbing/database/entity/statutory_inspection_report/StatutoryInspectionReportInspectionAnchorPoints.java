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
@Table(name = "statutory_inspection_report_inspection_anchor_points")
public class StatutoryInspectionReportInspectionAnchorPoints implements StatutoryInspectionReportInt, Cloneable, Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
    @OneToOne(mappedBy = "statutoryInspectionReportInspectionAnchorPoints")
    private StatutoryInspectionReport statutoryInspectionReport;

	private String inspectionAnchorPointsManufacturer;
	private String inspectionAnchorPointsType;
	private String inspectionAnchorPointsTypePlateTestBadge;

	private String inspectionAnchorPointsInspectors;
	private String inspectionAnchorPointsDate;
	private String inspectionAnchorPointsResultOfInspection;
	private String inspectionAnchorPointsRepairRequired;
	private String inspectionAnchorPointsNextInspection;

	private boolean inspectionAnchorPoints1Chk;
	private boolean inspectionAnchorPoints1Chkn;
	private String inspectionAnchorPoints1Txt;
	
	private boolean inspectionAnchorPoints2Chk;
	private boolean inspectionAnchorPoints2Chkn;
	private String inspectionAnchorPoints2Txt;
	
	private boolean inspectionAnchorPoints3Chk;
	private boolean inspectionAnchorPoints3Chkn;
	private String inspectionAnchorPoints3Txt;
	
	private boolean inspectionAnchorPoints4Chk;
	private boolean inspectionAnchorPoints4Chkn;
	private String inspectionAnchorPoints4Txt;
	
	private boolean inspectionAnchorPoints5Chk;
	private boolean inspectionAnchorPoints5Chkn;
	private String inspectionAnchorPoints5Txt;
	
	private boolean inspectionAnchorPoints6Chk;
	private boolean inspectionAnchorPoints6Chkn;
	private String inspectionAnchorPoints6Txt;
	
	private boolean inspectionAnchorPoints7Chk;
	private boolean inspectionAnchorPoints7Chkn;
	private String inspectionAnchorPoints7Txt;
	
	private boolean inspectionAnchorPoints8Chk;
	private boolean inspectionAnchorPoints8Chkn;
	private String inspectionAnchorPoints8Txt;
	
	private boolean inspectionAnchorPoints9Chk;
	private boolean inspectionAnchorPoints9Chkn;
	private String inspectionAnchorPoints9Txt;
	
	private boolean inspectionAnchorPoints10Chk;
	private boolean inspectionAnchorPoints10Chkn;
	private String inspectionAnchorPoints10Txt;
	
	private boolean inspectionAnchorPoints11Chk;
	private boolean inspectionAnchorPoints11Chkn;
	private String inspectionAnchorPoints11Txt;
	
	private boolean inspectionAnchorPoints12Chk;
	private boolean inspectionAnchorPoints12Chkn;
	private String inspectionAnchorPoints12Txt;
	
	private boolean inspectionAnchorPoints13Chk;
	private boolean inspectionAnchorPoints13Chkn;
	private String inspectionAnchorPoints13Txt;
	
	private boolean inspectionAnchorPoints14Chk;
	private boolean inspectionAnchorPoints14Chkn;
	private String inspectionAnchorPoints14Txt;
	
	private boolean inspectionAnchorPoints15Chk;
	private boolean inspectionAnchorPoints15Chkn;
	private String inspectionAnchorPoints15Txt;
	
	private boolean inspectionAnchorPoints16Chk;
	private boolean inspectionAnchorPoints16Chkn;
	private String inspectionAnchorPoints16Txt;
	
	private boolean inspectionAnchorPoints17Chk;
	private boolean inspectionAnchorPoints17Chkn;
	private String inspectionAnchorPoints17Txt;
	
	private boolean inspectionAnchorPoints18Chk;
	private boolean inspectionAnchorPoints18Chkn;
	private String inspectionAnchorPoints18Txt;
	
	private boolean inspectionAnchorPoints19Chk;
	private boolean inspectionAnchorPoints19Chkn;
	private String inspectionAnchorPoints19Txt;
	
	private boolean inspectionAnchorPoints20Chk;
	private boolean inspectionAnchorPoints20Chkn;
	private String inspectionAnchorPoints20Txt;
	
	private boolean inspectionAnchorPoints21Chk;
	private boolean inspectionAnchorPoints21Chkn;
	private String inspectionAnchorPoints21Txt;
	
	private boolean inspectionAnchorPoints22Chk;
	private boolean inspectionAnchorPoints22Chkn;
	private String inspectionAnchorPoints22Txt;
	
	private boolean inspectionAnchorPoints23Chk;
	private boolean inspectionAnchorPoints23Chkn;
	private String inspectionAnchorPoints23Txt;
	
	private boolean inspectionAnchorPoints24Chk;
	private boolean inspectionAnchorPoints24Chkn;
	private String inspectionAnchorPoints24Txt;
	
	private boolean inspectionAnchorPoints25Chk;
	private boolean inspectionAnchorPoints25Chkn;
	private String inspectionAnchorPoints25Txt;
	
	private boolean inspectionAnchorPoints26Chk;
	private boolean inspectionAnchorPoints26Chkn;
	private String inspectionAnchorPoints26Txt;
	
	private boolean inspectionAnchorPoints27Chk;
	private boolean inspectionAnchorPoints27Chkn;
	private String inspectionAnchorPoints27Txt;
	
	private boolean inspectionAnchorPoints28Chk;
	private boolean inspectionAnchorPoints28Chkn;
	private String inspectionAnchorPoints28Txt;
	
	private boolean inspectionAnchorPoints29Chk;
	private boolean inspectionAnchorPoints29Chkn;
	private String inspectionAnchorPoints29Txt;
	
	private boolean inspectionAnchorPoints30Chk;
	private boolean inspectionAnchorPoints30Chkn;
	private String inspectionAnchorPoints30Txt;

	private String inspectionAnchorPointsNotes;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		

		return mapa;
	}
	
}
