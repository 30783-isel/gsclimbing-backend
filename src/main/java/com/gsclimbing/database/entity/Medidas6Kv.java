package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Medidas6Kv extends Report implements Cloneable{

	private String dateOfMeasurement;

	private boolean chk1;
	private boolean chk2;
	private boolean chk3;

	private String voltage1;
	private String length1;
	private String visualInspection1;

	private String equipmentType1;
	private String serialNumber1;
	private String calibrationDate1;
	private String nextCalibrationDate1;

	@Column(length = 100)
	private String box1_1 ;
	
	@Column(length = 100)
	private String box1_2 ;
	
	@Column(length = 100)
	private String box1_3 ;
	
	@Column(length = 100)
	private String box1_4 ;
	
	@Column(length = 100)
	private String box1_5 ;
	
	@Column(length = 100)
	private String box1_6 ;
	
	@Column(length = 100)
	private String box1_7 ;
	
	@Column(length = 100)
	private String box1_8;
	
	@Column(length = 100)
	private String box1_9;
	
	@Column(length = 100)
	private String box1_10;
	
	@Column(length = 100)
	private String box1_11;
	
	@Column(length = 100)
	private String box1_12;
	
	@Column(length = 100)
	private String box1_13;
	
	@Column(length = 100)
	private String box1_14;
	
	@Column(length = 100)
	private String box1_15;
	
	@Column(length = 100)
	private String box1_16;
	
	@Column(length = 100)
	private String box1_17;
	
	@Column(length = 100)
	private String box1_18;

	@Column(length = 100)
	private String equipmentType2;
	
	@Column(length = 100)
	private String serialNumber2;
	
	@Column(length = 100)
	private String calibrationDate2;
	
	@Column(length = 100)
	private String nextCalibrationDate2;

	@Column(length = 100)
	private String box2_1 ;
	
	@Column(length = 100)
	private String box2_2 ;
	
	@Column(length = 100)
	private String box2_3 ;
	
	@Column(length = 100)
	private String box2_4 ;
	
	@Column(length = 100)
	private String box2_5 ;
	
	@Column(length = 100)
	private String box2_6 ;

	@Column(length = 100)
	private String box3_1 ;
	
	@Column(length = 100)
	private String box3_2;

	@Column(length = 100)
	private String type2;
	
	@Column(length = 100)
	private String voltage2;
	
	@Column(length = 100)
	private String length2;
	
	@Column(length = 100)
	private String visualInspection2;

	@Column(length = 100)
	private String equipmentType3;
	
	@Column(length = 100)
	private String serialNumber3;
	
	@Column(length = 100)
	private String calibrationDate3;
	
	@Column(length = 100)
	private String nextCalibrationDate3;

	@Column(length = 100)
	private String box4_1 ;
	
	@Column(length = 100)
	private String box4_2 ;
	
	@Column(length = 100)
	private String box4_3 ;
	
	@Column(length = 100)
	private String box4_4 ;
	
	@Column(length = 100)
	private String box4_5 ;
	
	@Column(length = 100)
	private String box4_6 ;
	
	@Column(length = 100)
	private String box4_7 ;
	
	@Column(length = 100)
	private String box4_8 ;
	
	@Column(length = 100)
	private String box4_9 ;
	
	@Column(length = 100)
	private String box4_10;
	
	@Column(length = 100)
	private String box4_11;
	
	@Column(length = 100)
	private String box4_12;
	
	@Column(length = 100)
	private String box4_13;
	
	@Column(length = 100)
	private String box4_14;
	
	@Column(length = 100)
	private String box4_15;
	
	@Column(length = 100)
	private String box4_16;
	
	@Column(length = 100)
	private String box4_17;
	
	@Column(length = 100)
	private String box4_18;

	@Column(length = 100)
	private String equipmentType4;
	
	@Column(length = 100)
	private String serialNumber4;
	
	@Column(length = 100)
	private String calibrationDate4;
	
	@Column(length = 100)
	private String nextCalibrationDate4;

	@Column(length = 100)
	private String box5_1 ;
	
	@Column(length = 100)
	private String box5_2 ;
	
	@Column(length = 100)
	private String box5_3 ;
	
	@Column(length = 100)
	private String box5_4 ;
	
	@Column(length = 100)
	private String box5_5 ;
	
	@Column(length = 100)
	private String box5_6;

	@Column(length = 100)
	private String box6_1 ;
	
	@Column(length = 100)
	private String box6_2 ;

	@Column(length = 100)
	private String type3;
	
	@Column(length = 100)
	private String voltage3;
	
	@Column(length = 100)
	private String length3;
	
	@Column(length = 100)
	private String visualInspection3;

	@Column(length = 100)
	private String equipmentType5;
	
	@Column(length = 100)
	private String serialNumber5;
	
	@Column(length = 100)
	private String calibrationDate5;
	
	@Column(length = 100)
	private String nextCalibrationDate5;

	@Column(length = 100)
	private String box7_1 ;
	
	@Column(length = 100)
	private String box7_2 ;
	
	@Column(length = 100)
	private String box7_3 ;
	
	@Column(length = 100)
	private String box7_4 ;
	
	@Column(length = 100)
	private String box7_5 ;
	
	@Column(length = 100)
	private String box7_6 ;
	
	@Column(length = 100)
	private String box7_7 ;
	
	@Column(length = 100)
	private String box7_8 ;
	
	@Column(length = 100)
	private String box7_9 ;
	
	@Column(length = 100)
	private String box7_10;
	
	@Column(length = 100)
	private String box7_11;
	
	@Column(length = 100)
	private String box7_12;
	
	@Column(length = 100)
	private String box7_13;
	
	@Column(length = 100)
	private String box7_14;
	
	@Column(length = 100)
	private String box7_15;
	
	@Column(length = 100)
	private String box7_16;
	
	@Column(length = 100)
	private String box7_17;
	
	@Column(length = 100)
	private String box7_18;

	@Column(length = 100)
	private String equipmentType6;
	
	@Column(length = 100)
	private String serialNumber6;
	
	@Column(length = 100)
	private String calibrationDate6;
	
	@Column(length = 100)
	private String nextCalibrationDate6;

	@Column(length = 100)
	private String box8_1 ;
	
	@Column(length = 100)
	private String box8_2 ;
	
	@Column(length = 100)
	private String box8_3 ;
	
	@Column(length = 100)
	private String box8_4 ;
	
	@Column(length = 100)
	private String box8_5 ;
	
	@Column(length = 100)
	private String box8_6 ;

	@Column(length = 100)
	private String box9_1 ;
	
	@Column(length = 100)
	private String box9_2 ;

	private String conclusion;
	
	@Column(length = 100)
	private String performedBy;
	
	@Column(length = 100)
	private String closedDate;

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("dateOfMeasurement", "Date of measurement");
		mapa.put("chk1", "Visual inspection");
		mapa.put("chk2", "Measurement the resistance of the line insulation from the switchgear to the transformer in the ground fault system");
		mapa.put("chk3", "Voltage test with the VLF device");
		mapa.put("voltage1", "Volts");
		mapa.put("length1", "Length");
		mapa.put("visualInspection1", "Visual Inspection");
		mapa.put("equipmentType1", "Equipment type");
		mapa.put("serialNumber1", "Serial Number");
		mapa.put("calibrationDate1", "Calibration Date");
		mapa.put("nextCalibrationDate1", "Next Calibration Date");
		mapa.put("box1_1", "P1-PE / Voltage[kV]");
		mapa.put("box1_2", "P1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_3", "P1-PE / Allowable value in [MΩ]");
		mapa.put("box1_4", "P2-PE / Voltage[kV]");
		mapa.put("box1_5", "P2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_6", "P2-PE / Allowable value in [MΩ]");
		mapa.put("box1_7", "P3-PE / Voltage[kV]");
		mapa.put("box1_8", "P3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_9", "P3-PE / Allowable value in [MΩ]");
		mapa.put("box1_10", "SH1-PE / Voltage[kV]");
		mapa.put("box1_11", "SH1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_12", "SH1-PE / Allowable value in [MΩ]");
		mapa.put("box1_13", "SH2-PE / Voltage[kV]");
		mapa.put("box1_14", "SH2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_15", "SH2-PE / Allowable value in [MΩ]");
		mapa.put("box1_16", "Sh3-PE / Voltage[kV]");
		mapa.put("box1_17", "Sh3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_18", "Sh3-PE / Allowable value in [MΩ]");                      
		mapa.put("equipmentType2", "Equipment type");
		mapa.put("serialNumber2", "Serial Number");
		mapa.put("calibrationDate2", "Calibration Date");
		mapa.put("nextCalibrationDate2", "Next Calibration Date");
		mapa.put("box2_1", "L1 / Voltage[kV]");
		mapa.put("box2_2", "L1 / Result[Positivo/Negativo]");
		mapa.put("box2_3", "L2 / Voltage[kV]");
		mapa.put("box2_4", "L2 / Result[Positivo/Negativo]");
		mapa.put("box2_5", "L3 / Voltage[kV]");
		mapa.put("box2_6", "L3 / Result[Positivo/Negativo]");
		mapa.put("box3_1", "L1-L2-L3 / Voltage[kV]");
		mapa.put("box3_2", "L1-L2-L3 / Result[Positivo/Negativo]");
		mapa.put("type2", "Type");
		mapa.put("voltage2", "Volts");
		mapa.put("length2", "Aproximate length");
		mapa.put("visualInspection2", "Visual inspection");
		mapa.put("equipmentType3", "Equipment Type");
		mapa.put("serialNumber3", "Serial number");
		mapa.put("calibrationDate3", "Calibration Date");
		mapa.put("nextCalibrationDate3", "Next Calibration Date");
		mapa.put("box4_1", "P1-PE / Voltage[kV]");
		mapa.put("box4_2", "P1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_3", "P1-PE / Allowable value in [MΩ]");
		mapa.put("box4_4", "P2-PE / Voltage[kV]");
		mapa.put("box4_5", "P2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_6", "P2-PE / Allowable value in [MΩ]");
		mapa.put("box4_7", "P3-PE / Voltage[kV]");
		mapa.put("box4_8", "P3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_9", "P3-PE / Allowable value in [MΩ]");
		mapa.put("box4_10", "SH1-PE / Voltage[kV]");
		mapa.put("box4_11", "SH1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_12", "SH1-PE / Allowable value in [MΩ]");
		mapa.put("box4_13", "SH2-PE / Voltage[kV]");
		mapa.put("box4_14", "SH2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_15", "SH2-PE / Allowable value in [MΩ]");
		mapa.put("box4_16", "Sh3-PE / Voltage[kV]");
		mapa.put("box4_17", "Sh3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_18", "Sh3-PE / Allowable value in [MΩ]");                      
		mapa.put("equipmentType4", "Equipment Type");
		mapa.put("serialNumber4", "Serial Number");
		mapa.put("calibrationDate4", "Calibration Date");
		mapa.put("nextCalibrationDate4", "Next Calibration Date");
		mapa.put("box5_1", "L1 / Voltage[kV]");
		mapa.put("box5_2", "L1 / Result[Positivo/Negativo]");
		mapa.put("box5_3", "L2 / Voltage[kV]");
		mapa.put("box5_4", "L2 / Result[Positivo/Negativo]");
		mapa.put("box5_5", "L3 / Voltage[kV]");
		mapa.put("box5_6", "L3 / Result[Positivo/Negativo]");
		mapa.put("box6_1", "L1-L2-L3 / Voltage[kV]");
		mapa.put("box6_2", "L1-L2-L3 / Result[Positivo/Negativo]");
		mapa.put("type3", "Type");
		mapa.put("voltage3", "Voltage");
		mapa.put("length3", "Length");
		mapa.put("equipmentType5", "Equipment Type");
		mapa.put("serialNumber5", "Serial Number");
		mapa.put("calibrationDate5", "Calibration Date");
		mapa.put("nextCalibrationDate5", "Next Calibration Date");
		mapa.put("box7_1", "P1-PE / Voltage[kV]");
		mapa.put("box7_2", "P1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_3", "P1-PE / Allowable value in [MΩ]");
		mapa.put("box7_4", "P2-PE / Voltage[kV]");
		mapa.put("box7_5", "P2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_6", "P2-PE / Allowable value in [MΩ]");
		mapa.put("box7_7", "P3-PE / Voltage[kV]");
		mapa.put("box7_8", "P3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_9", "P3-PE / Allowable value in [MΩ]");
		mapa.put("box7_10", "SH1-PE / Voltage[kV]");
		mapa.put("box7_11", "SH1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_12", "SH1-PE / Allowable value in [MΩ]");
		mapa.put("box7_13", "SH2-PE / Voltage[kV]");
		mapa.put("box7_14", "SH2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_15", "SH2-PE / Allowable value in [MΩ]");
		mapa.put("box7_16", "Sh3-PE / Voltage[kV]");
		mapa.put("box7_17", "Sh3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box7_18", "Sh3-PE / Allowable value in [MΩ]");                      
		mapa.put("equipmentType6", "Equipment Type");
		mapa.put("serialNumber6", "Serial Number");
		mapa.put("calibrationDate6", "Calibration Date");
		mapa.put("nextCalibrationDate6", "Next Calibration Date");
		mapa.put("box8_1", "L1 / Voltage[kV]");
		mapa.put("box8_2", "L1 / Result[Positivo/Negativo]");
		mapa.put("box8_3", "L2 / Voltage[kV]");
		mapa.put("box8_4", "L2 / Result[Positivo/Negativo]");
		mapa.put("box8_5", "L3 / Voltage[kV]");
		mapa.put("box8_6", "L3 / Result[Positivo/Negativo]");
		mapa.put("box9_1", "L1-L2-L3 / Voltage[kV]");
		mapa.put("box9_2", "L1-L2-L3 / Result[Positivo/Negativo]");
		mapa.put("conclusion", "Conclusion");
		mapa.put("performedBy", "Performed By");
		mapa.put("closedDate", "Closed Date");
		return mapa;
	}
		
}


