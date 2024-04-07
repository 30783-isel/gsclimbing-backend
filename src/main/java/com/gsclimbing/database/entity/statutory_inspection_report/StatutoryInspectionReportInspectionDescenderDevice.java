package com.gsclimbing.database.entity.statutory_inspection_report;

import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.StatutoryInspectionReportInt;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
@Entity
@Table(name = "statutory_inspection_report_inspection_descender_device")
public class StatutoryInspectionReportInspectionDescenderDevice implements StatutoryInspectionReportInt, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	private boolean inspectionDescenderDevice1Chkn;
	private String inspectionDescenderDevice1Txt;
	
	private boolean inspectionDescenderDevice2Chk;
	private boolean inspectionDescenderDevice2Chkn;
	private String inspectionDescenderDevice2Txt;
	
	private boolean inspectionDescenderDevice3Chk;
	private boolean inspectionDescenderDevice3Chkn;
	private String inspectionDescenderDevice3Txt;
	
	private boolean inspectionDescenderDevice4Chk;
	private boolean inspectionDescenderDevice4Chkn;
	private String inspectionDescenderDevice4Txt;
	
	private boolean inspectionDescenderDevice5Chk;
	private boolean inspectionDescenderDevice5Chkn;
	private String inspectionDescenderDevice5Txt;
	
	private boolean inspectionDescenderDevice6Chk;
	private boolean inspectionDescenderDevice6Chkn;
	private String inspectionDescenderDevice6Txt;

	private String inspectionDescenderDeviceNotes;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
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

		return mapa;
	}

}
