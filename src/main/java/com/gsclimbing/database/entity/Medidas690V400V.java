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
public class Medidas690V400V extends Report implements Cloneable{

	@Column(length = 100)
	private String dateOfMeasurement;

	@Column(length = 100)
	private String type1;
	
	@Column(length = 100)
	private String voltage1;
	
	@Column(length = 100)
	private String length1;
	
	@Column(length = 100)
	private String visual1;

	
	@Column(length = 100)
	private String box1_1;
	
	@Column(length = 100)
	private String box1_2;
	
	@Column(length = 100)
	private String box1_3;
	
	@Column(length = 100)
	private String box1_4;
	
	@Column(length = 100)
	private String box1_5;
	
	@Column(length = 100)
	private String box1_6;
	
	@Column(length = 100)
	private String box1_7;
	
	@Column(length = 100)
	private String box1_8;
	
	@Column(length = 100)
	private String box1_9;

	@Column(length = 100)
	private String type2;
	
	@Column(length = 100)
	private String voltage2;
	
	@Column(length = 100)
	private String length2;
	
	@Column(length = 100)
	private String visual2;

	@Column(length = 100)
	private String box2_1;
	
	@Column(length = 100)
	private String box2_2;
	
	@Column(length = 100)
	private String box2_3;
	
	@Column(length = 100)
	private String box2_4;
	
	@Column(length = 100)
	private String box2_5;
	
	@Column(length = 100)
	private String box2_6;
	
	@Column(length = 100)
	private String box2_7;
	
	@Column(length = 100)
	private String box2_8;
	
	@Column(length = 100)
	private String box2_9;

	
	@Column(length = 100)
	private String type3;
	
	@Column(length = 100)
	private String voltage3;
	
	@Column(length = 100)
	private String length3;
	
	@Column(length = 100)
	private String visual3;

	@Column(length = 100)
	private String box3_1;
	
	@Column(length = 100)
	private String box3_2;
	
	@Column(length = 100)
	private String box3_3;
	
	@Column(length = 100)
	private String box3_4;
	
	@Column(length = 100)
	private String box3_5;
	
	@Column(length = 100)
	private String box3_6;
	
	@Column(length = 100)
	private String box3_7;
	
	@Column(length = 100)
	private String box3_8;
	
	@Column(length = 100)
	private String box3_9;

	@Column(length = 100)
	private String type4;
	
	@Column(length = 100)
	private String voltage4;
	
	@Column(length = 100)
	private String length4;
	
	@Column(length = 100)
	private String visual4;

	@Column(length = 100)
	private String box4_1;
	
	@Column(length = 100)
	private String box4_2;
	
	@Column(length = 100)
	private String box4_3;
	
	@Column(length = 100)
	private String box4_4;
	
	@Column(length = 100)
	private String box4_5;
	
	@Column(length = 100)
	private String box4_6;
	
	@Column(length = 100)
	private String box4_7;
	
	@Column(length = 100)
	private String box4_8;
	
	@Column(length = 100)
	private String box4_9;

	@Column(length = 100)
	private String type5;
	
	@Column(length = 100)
	private String voltage5;
	
	@Column(length = 100)
	private String length5;
	
	@Column(length = 100)
	private String visual5;

	
	@Column(length = 100)
	private String box5_1;
	
	@Column(length = 100)
	private String box5_2;
	
	@Column(length = 100)
	private String box5_3;
	
	@Column(length = 100)
	private String box5_4;
	
	@Column(length = 100)
	private String box5_5;
	
	@Column(length = 100)
	private String box5_6;
	
	@Column(length = 100)
	private String box5_7;
	
	@Column(length = 100)
	private String box5_8;
	
	@Column(length = 100)
	private String box5_9;
	
	@Column(length = 100)
	private String box5_10;
	
	@Column(length = 100)
	private String box5_11;
	
	@Column(length = 100)
	private String box5_12;

	
	@Column(length = 100)
	private String box6_1;
	
	@Column(length = 100)
	private String box6_2;
	
	@Column(length = 100)
	private String box6_3;
	
	@Column(length = 100)
	private String box6_4;
	
	@Column(length = 100)
	private String box6_5;
	
	@Column(length = 100)
	private String box6_6;
	
	@Column(length = 100)
	private String box6_7;
	
	@Column(length = 100)
	private String box6_8;
	
	@Column(length = 100)
	private String box6_9;
	
	@Column(length = 100)
	private String box6_10;
	
	@Column(length = 100)
	private String box6_11;
	
	@Column(length = 100)
	private String box6_12;
	
	@Column(length = 100)
	private String box6_13;
	
	@Column(length = 100)
	private String box6_14;
	
	@Column(length = 100)
	private String box6_15;
	
	@Column(length = 100)
	private String box6_16;
	
	@Column(length = 100)
	private String box6_17;
	
	@Column(length = 100)
	private String box6_18;
	
	@Column(length = 100)
	private String box6_19;
	
	@Column(length = 100)
	private String box6_20;
	
	@Column(length = 100)
	private String box6_21;
	
	@Column(length = 100)
	private String box6_22;
	
	@Column(length = 100)
	private String box6_23;
	
	@Column(length = 100)
	private String box6_24;

	@Column(length = 100)
	private String type6;
	
	@Column(length = 100)
	private String voltage6;
	
	@Column(length = 100)
	private String visual6;

	
	@Column(length = 100)
	private String box7_1;
	
	@Column(length = 100)
	private String box7_2;
	
	@Column(length = 100)
	private String box7_3;
	
	@Column(length = 100)
	private String box7_4;
	
	@Column(length = 100)
	private String box7_5;
	
	@Column(length = 100)
	private String box7_6;
	
	@Column(length = 100)
	private String box7_7;
	
	@Column(length = 100)
	private String box7_8;
	
	@Column(length = 100)
	private String box7_9;
	
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
	private String box7_19;
	
	@Column(length = 100)
	private String box7_20;
	
	@Column(length = 100)
	private String box7_21;
	
	@Column(length = 100)
	private String box7_22;
	
	@Column(length = 100)
	private String box7_23;
	
	@Column(length = 100)
	private String box7_24;
	
	@Column(length = 100)
	private String box7_25;
	
	@Column(length = 100)
	private String box7_26;
	
	@Column(length = 100)
	private String box7_27;

	@Column(length = 100)
	private String equipmentType;
	
	@Column(length = 100)
	private String serialNumber;
	
	@Column(length = 100)
	private String calibrationDate;
	
	@Column(length = 100)
	private String nextCalibrationDate;

	private String conclusion;
	
	@Column(length = 100)
	private String performedBy;
	
	@Column(length = 100)
	private String closedDate;
	
	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("dateMeasurement", "Date of measurement");
		mapa.put("type1", "Type");
		mapa.put("voltage1", "Voltage");
		mapa.put("length1", "Aproximate length");
		mapa.put("visual1", "Visual inspection");
		mapa.put("box1_1", "L1-PE / Voltage[kV]");
		mapa.put("box1_2", "L1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_3", "L1-PE / Allowable value in [MΩ]");
		mapa.put("box1_4", "L2-PE / Voltage[kV]");
		mapa.put("box1_5", "L2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_6", "L2-PE / Allowable value in [MΩ]");
		mapa.put("box1_7", "L3-PE / Voltage[kV]");
		mapa.put("box1_8", "L3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box1_9", "L3-PE / Allowable value in [MΩ]");                                     
		mapa.put("type2", "Type");
		mapa.put("voltage2", "Voltage");
		mapa.put("length2", "Aproximate length");
		mapa.put("visual2", "Visual inspection");
		mapa.put("box2_1", "L1-PE / Voltage[kV]");
		mapa.put("box2_2", "L1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box2_3", "L1-PE / Allowable value in [MΩ]");
		mapa.put("box2_4", "L2-PE / Voltage[kV]");
		mapa.put("box2_5", "L2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box2_6", "L2-PE / Allowable value in [MΩ]");
		mapa.put("box2_7", "L3-PE / Voltage[kV]");
		mapa.put("box2_8", "L3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box2_9", "L3-PE / Allowable value in [MΩ]");                    
		mapa.put("type3", "Type");
		mapa.put("voltage3", "Voltage");
		mapa.put("length3", "Aproximate length");
		mapa.put("visual3", "Visual inspection");
		mapa.put("box3_1", "L1-PE / Voltage[kV]");
		mapa.put("box3_2", "L1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box3_3", "L1-PE / Allowable value in [MΩ]");
		mapa.put("box3_4", "L2-PE / Voltage[kV]");
		mapa.put("box3_5", "L2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box3_6", "L2-PE / Allowable value in [MΩ]");
		mapa.put("box3_7", "L3-PE / Voltage[kV]");
		mapa.put("box3_8", "L3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box3_9", "L3-PE / Allowable value in [MΩ]");                    
		mapa.put("type4", "Type");
		mapa.put("voltage4", "Voltage");
		mapa.put("length4", "Aproximate length");
		mapa.put("visual4", "Visual inspection");
		mapa.put("box4_1", "L1-PE / Voltage[kV]");
		mapa.put("box4_2", "L1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_3", "L1-PE / Allowable value in [MΩ]");
		mapa.put("box4_4", "L2-PE / Voltage[kV]");
		mapa.put("box4_5", "L2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_6", "L2-PE / Allowable value in [MΩ]");
		mapa.put("box4_7", "L3-PE / Voltage[kV]");
		mapa.put("box4_8", "L3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box4_9", "L3-PE / Allowable value in [MΩ]");                    
		mapa.put("type5", "Type");
		mapa.put("voltage5", "Voltage");
		mapa.put("length5", "Aproximate length");
		mapa.put("visual5", "Visual inspection");
		mapa.put("box5_1", "K1-PE / Voltage[kV]");
		mapa.put("box5_2", "K1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box5_3", "K1-PE / Allowable value in [MΩ]");                   
		mapa.put("box5_4", "K2-PE / Voltage[kV]");
		mapa.put("box5_5", "K2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box5_6", "K2-PE / Allowable value in [MΩ]");                   
		mapa.put("box5_7", "K3-PE / Voltage[kV]");
		mapa.put("box5_8", "K3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box5_9", "K3-PE / Allowable value in [MΩ]");                   
		mapa.put("box5_10", "K4-PE / Voltage[kV]");
		mapa.put("box5_11", "K4-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box5_12", "K4-PE / Allowable value in [MΩ]");                   
		mapa.put("box6_1", "L1-PE / Voltage[kV]");
		mapa.put("box6_2", "L1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_3", "L1-PE / Allowable value in [MΩ]");                   
		mapa.put("box6_4", "L2-PE / Voltage[kV]");
		mapa.put("box6_5", "L2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_6", "L2-PE / Allowable value in [MΩ]");                                   
		mapa.put("box6_7", "L3-PE / Voltage[kV]");
		mapa.put("box6_8", "L3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_9", "L3-PE / Allowable value in [MΩ]");                    
		mapa.put("box6_10", "L4-PE / Voltage[kV]");
		mapa.put("box6_11", "L4-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_12", "L4-PE / Allowable value in [MΩ]");                    
		mapa.put("box6_13", "M1-PE / Voltage[kV]");
		mapa.put("box6_14", "M1-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_15", "M1-PE / Allowable value in [MΩ]");                    
		mapa.put("box6_16", "M2-PE / Voltage[kV]");
		mapa.put("box6_17", "M2-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_18", "M2-PE / Allowable value in [MΩ]");                    
		mapa.put("box6_19", "M3-PE / Voltage[kV]");
		mapa.put("box6_20", "M3-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_21", "M3-PE / Allowable value in [MΩ]");                    
		mapa.put("box6_22", "M4-PE / Voltage[kV]");
		mapa.put("box6_23", "M4-PE / Insulation resistance in [MΩ] after [60s]");
		mapa.put("box6_24", "M4-PE / Allowable value in [MΩ]");                    
		mapa.put("type6", "Type");
		mapa.put("voltage6", "Voltage");
		mapa.put("visual6", "Visual inspection");
		mapa.put("box7_1", "Blade 1 (A) / Number");
		mapa.put("box7_2", "Blade 1 (A) / Resistance in [mΩ] after [60s]");
		mapa.put("box7_3", "Blade 1 (A) / Allowable value in [mΩ]");
		mapa.put("box7_4", "Blade 2 (B) / Number");
		mapa.put("box7_5", "Blade 2 (B) / Resistance in [mΩ] after [60s]");
		mapa.put("box7_6", "Blade 2 (B) / Allowable value in [mΩ]");
		mapa.put("box7_7", "Blade 3 (C) / Number");
		mapa.put("box7_8", "Blade 3 (C) / Resistance in [mΩ] after [60s]");
		mapa.put("box7_9", "Blade 3 (C) / Allowable value in [mΩ]");
		mapa.put("box7_10", "Nacelle rail roof / Number");
		mapa.put("box7_11", "Nacelle rail roof / Resistance in [mΩ] after [60s]");
		mapa.put("box7_12", "Nacelle rail roof / Allowable value in [mΩ]");
		mapa.put("box7_13", "Weather station / Number");
		mapa.put("box7_14", "Weather station / Resistance in [mΩ] after [60s]");
		mapa.put("box7_15", "Weather station / Allowable value in [mΩ]");
		mapa.put("box7_16", "Generator / Number");
		mapa.put("box7_17", "Generator / Resistance in [mΩ] after [60s]");
		mapa.put("box7_18", "Generator / Allowable value in [mΩ]");
		mapa.put("box7_19", "Gearbox / Number");
		mapa.put("box7_20", "Gearbox / Resistance in [mΩ] after [60s]");
		mapa.put("box7_21", "Gearbox / Allowable value in [mΩ]");
		mapa.put("box7_22", "Main frame / Number");
		mapa.put("box7_23", "Main frame / Resistance in [mΩ] after [60s]");	
		mapa.put("box7_24", "Main frame / Allowable value in [mΩ]");
		mapa.put("box7_25", "Nacelle grounding bus bar / Number");
		mapa.put("box7_26", "Nacelle grounding bus bar / Resistance in [mΩ] after [60s]");
		mapa.put("box7_27", "Nacelle grounding bus bar / Allowable value in [mΩ]");
		mapa.put("equipmentType", "Equipment type");
		mapa.put("serialNumber", "Serial Number");
		mapa.put("calibrationDate", "Calibration Date");
		mapa.put("nextCalibrationDate", "Next Calibration Date");
		mapa.put("conclusion", "Conclusion");
		mapa.put("performedBy", "Test performed by");
		mapa.put("closedDate", "Date");
		return mapa;
	}
	
}


