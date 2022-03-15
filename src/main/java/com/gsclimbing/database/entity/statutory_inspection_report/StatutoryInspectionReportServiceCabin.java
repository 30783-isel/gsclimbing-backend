package com.gsclimbing.database.entity.statutory_inspection_report;

import java.io.Serializable;

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
	private String inspectionStickerReportServiceCabinNextInspection;

	private boolean inspectionReportServiceCabin1Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin1Txt ;
	private boolean inspectionReportServiceCabin2Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin2Txt ;
	private boolean inspectionReportServiceCabin3Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin3Txt ;
	private boolean inspectionReportServiceCabin4Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin4Txt ;
	private boolean inspectionReportServiceCabin5Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin5Txt ;
	private boolean inspectionReportServiceCabin6Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin6Txt ;
	private boolean inspectionReportServiceCabin7Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin7Txt ;
	private boolean inspectionReportServiceCabin8Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin8Txt ;
	private boolean inspectionReportServiceCabin9Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin9Txt ;
	private boolean inspectionReportServiceCabin10Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin10Txt;
	private boolean inspectionReportServiceCabin11Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin11Txt;
	private boolean inspectionReportServiceCabin12Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin12Txt;
	private boolean inspectionReportServiceCabin13Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin13Txt;
	private boolean inspectionReportServiceCabin14Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin14Txt;
	private boolean inspectionReportServiceCabin15Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin15Txt;
	private boolean inspectionReportServiceCabin16Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin16Txt;
	private boolean inspectionReportServiceCabin17Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin17Txt;
	private boolean inspectionReportServiceCabin18Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin18Txt;
	private boolean inspectionReportServiceCabin19Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin19Txt;
	private boolean inspectionReportServiceCabin20Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin20Txt;
	private boolean inspectionReportServiceCabin21Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin21Txt;
	private boolean inspectionReportServiceCabin22Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin22Txt;
	private boolean inspectionReportServiceCabin23Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin23Txt;
	private boolean inspectionReportServiceCabin24Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin24Txt;
	private boolean inspectionReportServiceCabin25Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin25Txt;
	private boolean inspectionReportServiceCabin26Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin26Txt;
	private boolean inspectionReportServiceCabin27Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin27Txt;
	private boolean inspectionReportServiceCabin28Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin28Txt;
	private boolean inspectionReportServiceCabin29Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin29Txt;
	private boolean inspectionReportServiceCabin30Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin30Txt;
	private boolean inspectionReportServiceCabin31Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin31Txt;
	private boolean inspectionReportServiceCabin32Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin32Txt;
	private boolean inspectionReportServiceCabin33Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin33Txt;
	private boolean inspectionReportServiceCabin34Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin34Txt;
	private boolean inspectionReportServiceCabin35Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin35Txt;
	private boolean inspectionReportServiceCabin36Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin36Txt;
	private boolean inspectionReportServiceCabin37Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin37Txt;
	private boolean inspectionReportServiceCabin38Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin38Txt;
	private boolean inspectionReportServiceCabin39Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin39Txt;
	private boolean inspectionReportServiceCabin40Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin40Txt;
	private boolean inspectionReportServiceCabin41Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin41Txt;
	private boolean inspectionReportServiceCabin42Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin42Txt;
	private boolean inspectionReportServiceCabin43Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin43Txt;
	private boolean inspectionReportServiceCabin44Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin44Txt;
	private boolean inspectionReportServiceCabin45Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin45Txt;
	private boolean inspectionReportServiceCabin46Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin46Txt;
	private boolean inspectionReportServiceCabin47Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin47Txt;
	private boolean inspectionReportServiceCabin48Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin48Txt;
	private boolean inspectionReportServiceCabin49Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin49Txt;
	private boolean inspectionReportServiceCabin50Chk;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin50Txt;
	private boolean inspectionReportServiceCabin51Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin51Txt ;
	private boolean inspectionReportServiceCabin52Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin52Txt ;
	private boolean inspectionReportServiceCabin53Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin53Txt ;
	private boolean inspectionReportServiceCabin54Chk ;
	
	@Column(length = 100)
	private String inspectionReportServiceCabin54Txt;

	@Column(length = 100)
	private String inspectionReportServiceCabinNotes;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
