package com.gsclimbing.database.entity.statutory_inspection_report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.StatutoryInspectionReportInt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "statutory_inspection_report_inspection_anchor_points")
public class StatutoryInspectionReportInspectionAnchorPoints implements StatutoryInspectionReportInt{

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
	private String inspectionAnchorPoints1Txt;
	private boolean inspectionAnchorPoints2Chk;
	private String inspectionAnchorPoints2Txt;
	private boolean inspectionAnchorPoints3Chk;
	private String inspectionAnchorPoints3Txt;
	private boolean inspectionAnchorPoints4Chk;
	private String inspectionAnchorPoints4Txt;
	private boolean inspectionAnchorPoints5Chk;
	private String inspectionAnchorPoints5Txt;
	private boolean inspectionAnchorPoints6Chk;
	private String inspectionAnchorPoints6Txt;
	private boolean inspectionAnchorPoints7Chk;
	private String inspectionAnchorPoints7Txt;
	private boolean inspectionAnchorPoints8Chk;
	private String inspectionAnchorPoints8Txt;
	private boolean inspectionAnchorPoints9Chk;
	private String inspectionAnchorPoints9Txt;
	private boolean inspectionAnchorPoints10Chk;
	private String inspectionAnchorPoints10Txt;
	private boolean inspectionAnchorPoints11Chk;
	private String inspectionAnchorPoints11Txt;
	private boolean inspectionAnchorPoints12Chk;
	private String inspectionAnchorPoints12Txt;
	private boolean inspectionAnchorPoints13Chk;
	private String inspectionAnchorPoints13Txt;
	private boolean inspectionAnchorPoints14Chk;
	private String inspectionAnchorPoints14Txt;
	private boolean inspectionAnchorPoints15Chk;
	private String inspectionAnchorPoints15Txt;
	private boolean inspectionAnchorPoints16Chk;
	private String inspectionAnchorPoints16Txt;
	private boolean inspectionAnchorPoints17Chk;
	private String inspectionAnchorPoints17Txt;
	private boolean inspectionAnchorPoints18Chk;
	private String inspectionAnchorPoints18Txt;
	private boolean inspectionAnchorPoints19Chk;
	private String inspectionAnchorPoints19Txt;
	private boolean inspectionAnchorPoints20Chk;
	private String inspectionAnchorPoints20Txt;
	private boolean inspectionAnchorPoints21Chk;
	private String inspectionAnchorPoints21Txt;
	private boolean inspectionAnchorPoints22Chk;
	private String inspectionAnchorPoints22Txt;
	private boolean inspectionAnchorPoints23Chk;
	private String inspectionAnchorPoints23Txt;
	private boolean inspectionAnchorPoints24Chk;
	private String inspectionAnchorPoints24Txt;
	private boolean inspectionAnchorPoints25Chk;
	private String inspectionAnchorPoints25Txt;
	private boolean inspectionAnchorPoints26Chk;
	private String inspectionAnchorPoints26Txt;
	private boolean inspectionAnchorPoints27Chk;
	private String inspectionAnchorPoints27Txt;
	private boolean inspectionAnchorPoints28Chk;
	private String inspectionAnchorPoints28Txt;
	private boolean inspectionAnchorPoints29Chk;
	private String inspectionAnchorPoints29Txt;
	private boolean inspectionAnchorPoints30Chk;
	private String inspectionAnchorPoints30Txt;

	private String inspectionAnchorPointsNotes;
	
}
