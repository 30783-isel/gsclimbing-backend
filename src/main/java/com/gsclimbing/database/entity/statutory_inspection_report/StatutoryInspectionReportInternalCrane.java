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
@Table(name="statutory_inspection_report_internal_crane")
public class StatutoryInspectionReportInternalCrane  implements StatutoryInspectionReportInt{

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
	
}
