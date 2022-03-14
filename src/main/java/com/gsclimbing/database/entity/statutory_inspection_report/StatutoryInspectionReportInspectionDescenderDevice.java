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
@Table(name = "statutory_inspection_report_inspection_descender_device")
public class StatutoryInspectionReportInspectionDescenderDevice  implements StatutoryInspectionReportInt{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
    @OneToOne(mappedBy = "statutoryInspectionReportInspectionDescenderDevice")
    private StatutoryInspectionReport statutoryInspectionReport;

	private String inspectionDescenderDeviceManufacturer;
	private String inspectionDescenderDeviceType;
	private String inspectionDescenderDeviceYearBuild;
	private String inspectionDescenderDeviceSerialNumber;
	private String inspectionDescenderDeviceTypePlateTestBadge;

	private String inspectionDescenderDeviceInspectors;
	private String inspectionDescenderDeviceDate;
	private String inspectionDescenderDeviceResultOfInspection;
	private String inspectionDescenderDeviceRepairRequired;
	private String inspectionDescenderDeviceNextInspection;

	private boolean inspectionDescenderDevice1Chk;
	private String inspectionDescenderDevice1Txt;
	private boolean inspectionDescenderDevice2Chk;
	private String inspectionDescenderDevice2Txt;
	private boolean inspectionDescenderDevice3Chk;
	private String inspectionDescenderDevice3Txt;
	private boolean inspectionDescenderDevice4Chk;
	private String inspectionDescenderDevice4Txt;
	private boolean inspectionDescenderDevice5Chk;
	private String inspectionDescenderDevice5Txt;
	private boolean inspectionDescenderDevice6Chk;
	private String inspectionDescenderDevice6Txt;
	
	private String inspectionDescenderDeviceNotes;
	
}
