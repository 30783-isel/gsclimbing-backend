package com.gsclimbing.database.entity;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExaminationTransformer extends Report implements Cloneable {

	@Column(length = 100)
	private String dateOfMeasurement;

	@Column(length = 100)
	private String manufacturer;

	@Column(length = 100)
	private String type;

	@Column(length = 100)
	private String equipamentSerialNumber;

	@Column(length = 10)
	private String ratioIdentified;

	@Column(length = 10)
	private String visualInspectionTransformer;

	@Column(length = 100)
	private String equipamentType1;

	@Column(length = 100)
	private String serialNumber1;

	@Column(length = 100)
	private String calibrationDate1;

	@Column(length = 100)
	private String nextCalibrationDate1;

	@Column(length = 100)
	private String terminals1_1;

	@Column(length = 100)
	private String terminals1_2;

	@Column(length = 100)
	private String terminals1_3;

	@Column(length = 100)
	private String terminals2_1;

	@Column(length = 100)
	private String terminals2_2;

	@Column(length = 100)
	private String terminals2_3;

	@Column(length = 100)
	private String terminals3_1;

	@Column(length = 100)
	private String terminals3_2;

	@Column(length = 100)
	private String terminals3_3;

	@Column(length = 100)
	private String tolerancia1;

	@Column(length = 100)
	private String tolerancia2;

	@Column(length = 100)
	private String tolerancia3;

	@Column(length = 100)
	private String tolerancia4;

	@Column(length = 100)
	private String tolerancia5;

	@Column(length = 100)
	private String tolerancia6;

	@Column(length = 100)
	private String equipamentType2;

	@Column(length = 100)
	private String serialNumber2;

	@Column(length = 100)
	private String calibrationDate2;

	@Column(length = 100)
	private String nextCalibrationDate2;

	@Column(length = 100)
	private String voltage1;

	@Column(length = 100)
	private String voltage2;

	@Column(length = 100)
	private String voltage3;

	@Column(length = 100)
	private String voltage4;

	@Column(length = 100)
	private String voltage5;

	@Column(length = 100)
	private String voltage6;

	@Column(length = 100)
	private String resistencia1;

	@Column(length = 100)
	private String resistencia2;

	@Column(length = 100)
	private String resistencia3;

	@Column(length = 100)
	private String resistencia4;

	@Column(length = 100)
	private String resistencia5;

	@Column(length = 100)
	private String resistencia6;

	@Column(length = 100)
	private String medida1;

	@Column(length = 100)
	private String medida2;

	@Column(length = 100)
	private String medida3;

	@Column(length = 100)
	private String medida4;

	@Column(length = 100)
	private String medida5;

	@Column(length = 100)
	private String medida6;

	@Column(length = 100)
	private String equipamentType3;

	@Column(length = 100)
	private String serialNumber3;

	@Column(length = 100)
	private String calibrationDate3;

	@Column(length = 100)
	private String nextCalibrationDate3;

	@Column(length = 100)
	private String voltage;

	@Column(length = 100)
	private String corrent1;

	@Column(length = 100)
	private String corrent2;

	@Column(length = 100)
	private String equipamentType4;

	@Column(length = 100)
	private String serialNumber4;

	@Column(length = 100)
	private String calibrationDate4;

	@Column(length = 100)
	private String nextCalibrationDate4;

	@Column(length = 100)
	private String insulationResistance;

	@Column(length = 100)
	private String ratioTest;

	private String conclusion;

	@Column(length = 100)
	private String performedBy;

	@Column(length = 100)
	private String date;

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("dateOfMeasurement", "Date of measurement");
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("manufacturer", "Manufacturer");
		mapa.put("type", "Type");
		mapa.put("equipamentSerialNumber", "Serial number");
//		mapa.put("correct1", "Correct");
//		mapa.put("notCorrect1", "Not correct");
		mapa.put("equipamentType1", "Equipment type");
		mapa.put("serialNumber1", "Seral number:");
		mapa.put("calibrationDate1", "Calibration date");
		mapa.put("nextCalibrationDate1", "Next calibration date");
		mapa.put("terminals1_1", "Winding MV / 1U-1V");
		mapa.put("terminals1_2", "Winding MV / 1V-1W");
		mapa.put("terminals1_3", "Winding MV / 1U-1W");
		mapa.put("terminals2_1", "Winding LV1 / 1U-1V");
		mapa.put("terminals2_2", "Winding LV1 / 1V-1W");
		mapa.put("terminals2_3", "Winding LV1 / 1U-1W");
		mapa.put("terminals3_1", "Winding LV2 / 1U-1V");
		mapa.put("terminals3_2", "Winding LV2 / 1V-1W");
		mapa.put("terminals3_3", "Winding LV2 / 1U-1W");
		mapa.put("tolerancia1", "Winding MV / Average");
		mapa.put("tolerancia2", "Winding MV [MΩ]/ Average");
		mapa.put("tolerancia3", "Winding LV1 / Average");
		mapa.put("tolerancia4", "Winding LV1 [MΩ]/ Average");
		mapa.put("tolerancia5", "Winding LV2 / Average");
		mapa.put("tolerancia6", "Winding LV2 [MΩ]/ Average");
		mapa.put("equipamentType2", "Equipment type");
		mapa.put("serialNumber2", "Serial number number");
		mapa.put("calibrationDate2", "Calibration date:");
		mapa.put("nextCalibrationDate2", "Next calibration date");
		mapa.put("voltage1", "Transformer 20 KV 5000 VDC to Ground U-V-W to Ground / Voltage[V]");
		mapa.put("voltage2", "Transformer 6KV 5000VDC to Ground U-V-W to Ground / Voltage[V]");
		mapa.put("voltage3", "Transformer 690V 1000VDC to ground U-V-W to ground / Voltage[V]");
		mapa.put("voltage4", "Transformer 20KV to 6KV 5000DC U-V-W are connected through delta link and than against starpoint 6KV / Voltage[V]");
		mapa.put("voltage5", "Transformer 20KV to 690V 5000VDC U-V-W are connected through delta link and than against starpoint 690V / Voltage[V]");
		mapa.put("voltage6", "Transformer 6KV to 690V 5000V Starpoint 6KV to Starpoint 690V / Voltage[V]");
		mapa.put("resistencia1", "Transformer 20 KV 5000 VDC to Ground U-V-W to Ground / [MΩ][60s]");
		mapa.put("resistencia2", "Transformer 6KV 5000VDC to Ground U-V-W to Ground / [MΩ][60s]");
		mapa.put("resistencia3", "Transformer 690V 1000VDC to ground U-V-W to ground / [MΩ][60s]");
		mapa.put("resistencia4", "Transformer 20KV to 6KV 5000DC U-V-W are connected through delta link and than against starpoint 6KV / [MΩ ][60s]");
		mapa.put("resistencia5", "Transformer 20KV to 690V 5000VDC U-V-W are connected through delta link and than against starpoint 690V / [MΩ ][60s]");
		mapa.put("resistencia6", "Transformer 6KV to 690V 5000V Starpoint 6KV to Starpoint 690V / [MΩ ][60s]");
		mapa.put("medida1", "Transformer 20 KV 5000 VDC to Ground U-V-W to Ground / (Positivo)");
		mapa.put("medida2", "Transformer 6KV 5000VDC to Ground U-V-W to Ground / (Positivo)");
		mapa.put("medida3", "Transformer 690V 1000VDC to ground U-V-W to ground / (Positivo)");
		mapa.put("medida4", "Transformer 20KV to 6KV 5000DC U-V-W are connected through delta link and than against starpoint 6KV / (Positivo)");
		mapa.put("medida5", "Transformer 20KV to 690V 5000VDC U-V-W are connected through delta link and than against starpoint 690V / (Positivo)");
		mapa.put("medida6", "Transformer 6KV to 690V 5000V Starpoint 6KV to Starpoint 690V / (Positivo)");
		mapa.put("equipamentType3", "Equipament type");
		mapa.put("serialNumber3", "Serial number");
		mapa.put("calibrationDate3", "Calibration date");
		mapa.put("nextCalibrationDate3", "Next calibration date");
		mapa.put("voltage", "Voltaje [V]");
		mapa.put("corrent1", "Current 1");
		mapa.put("corrent2", "Current 2");
		mapa.put("equipamentType4", "Equipment type type:");
		mapa.put("serialNumber4", "Serial number:");
		mapa.put("calibrationDate4", "Calibration date:");
		mapa.put("nextCalibrationDate4", "Next calibration date:");
		mapa.put("insulationResistance", "Insulation resistance test");
		mapa.put("ratioTest", "Radio test");
		mapa.put("visualInspectionTransformer", "Visual Inspection Transformer");
		mapa.put("ratioIdentified", "Ratio Identified");
		mapa.put("conclusion", "Conclusion");
		mapa.put("performedBy", "Test performed by");
		mapa.put("date", "Date");
		return mapa;
	}

}
