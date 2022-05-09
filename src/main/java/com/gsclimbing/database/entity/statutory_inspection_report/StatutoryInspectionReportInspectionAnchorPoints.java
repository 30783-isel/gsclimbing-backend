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
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
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
