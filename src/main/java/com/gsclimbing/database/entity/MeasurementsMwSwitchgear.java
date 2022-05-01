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
public class MeasurementsMwSwitchgear extends Report implements Cloneable{

	@Column(length = 100)
	private String dateMeasurement;
	
	@Column(length = 100)
	private String manufacturerDate;
	
	@Column(length = 100)
	private String type;
	
	@Column(length = 100)
	private String serialNumber;
	
	@Column(length = 10)
	private String mvsgCorrect;
	
	@Column(length = 10)
	private String mvsgNotCorrect;
	
	@Column(length = 10)
	private String sf6Correct;
	
	@Column(length = 10)
	private String sf6NotCorrect;

	@Column(length = 100)
	private String equipamentType1;
	
	@Column(length = 100)
	private String serialNumber1;
	
	@Column(length = 100)
	private String calibrationDate1;
	
	@Column(length = 100)
	private String nextCalibrationDate1;

	@Column(length = 100)
	private String equipamentType2;
	
	@Column(length = 100)
	private String serialNumber2;
	
	@Column(length = 100)
	private String calibrationDate2;
	
	@Column(length = 100)
	private String nextCalibrationDate2;

	@Column(length = 100)
	private String equipamentType3;
	
	@Column(length = 100)
	private String serialNumber3;
	
	@Column(length = 100)
	private String calibrationDate3;
	
	@Column(length = 100)
	private String nextCalibrationDate3;

	@Column(length = 100)
	private String equipamentType4;
	
	@Column(length = 100)
	private String serialNumber4;
	
	@Column(length = 100)
	private String calibrationDate4;
	
	@Column(length = 100)
	private String nextCalibrationDate4;

	@Column(length = 100)
	private String pongo1;
	
	@Column(length = 100)
	private String pongo2;
	
	@Column(length = 100)
	private String pongo3;
	
	@Column(length = 100)
	private String pongo4;
	
	@Column(length = 100)
	private String pongo5;
	
	@Column(length = 100)
	private String pongo6;
	
	@Column(length = 100)
	private String pongo7;

	@Column(length = 100)
	private String pruebo1;
	
	@Column(length = 100)
	private String pruebo2;
	
	@Column(length = 100)
	private String pruebo3;
	
	@Column(length = 100)
	private String pruebo4;
	
	@Column(length = 100)
	private String pruebo5;
	
	@Column(length = 100)
	private String pruebo6;
	
	@Column(length = 100)
	private String pruebo7;

	@Column(length = 100)
	private String conjuno1;
	
	@Column(length = 100)
	private String conjuno2;
	
	@Column(length = 100)
	private String conjuno3;
	
	@Column(length = 100)
	private String conjuno4;
	
	@Column(length = 100)
	private String conjuno5;
	
	@Column(length = 100)
	private String conjuno6;
	
	@Column(length = 100)
	private String conjuno7;

	@Column(length = 100)
	private String prueba1;
	
	@Column(length = 100)
	private String prueba2;
	
	@Column(length = 100)
	private String prueba3;
	
	@Column(length = 100)
	private String prueba4;
	
	@Column(length = 100)
	private String prueba5;
	
	@Column(length = 100)
	private String prueba6;
	
	@Column(length = 100)
	private String prueba7;

	@Column(length = 100)
	private String resultado1;
	
	@Column(length = 100)
	private String resultado2;
	
	@Column(length = 100)
	private String resultado3;
	
	@Column(length = 100)
	private String resultado4;
	
	@Column(length = 100)
	private String resultado5;
	
	@Column(length = 100)
	private String resultado6;
	
	@Column(length = 100)
	private String resultado7;

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
	private String voltage7;
	
	@Column(length = 100)
	private String voltage8;
	
	@Column(length = 100)
	private String voltage9;
	
	@Column(length = 100)
	private String voltage10;
	
	@Column(length = 100)
	private String voltage11;
	
	@Column(length = 100)
	private String voltage12;

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
	private String resistencia7;
	
	@Column(length = 100)
	private String resistencia8;
	
	@Column(length = 100)
	private String resistencia9;
	
	@Column(length = 100)
	private String resistencia10;
	
	@Column(length = 100)
	private String resistencia11;
	
	@Column(length = 100)
	private String resistencia12;

	@Column(length = 100)
	private String resistenciaPermisible1;
	
	@Column(length = 100)
	private String resistenciaPermisible2;
	
	@Column(length = 100)
	private String resistenciaPermisible3;
	
	@Column(length = 100)
	private String resistenciaPermisible4;
	
	@Column(length = 100)
	private String resistenciaPermisible5;
	
	@Column(length = 100)
	private String resistenciaPermisible6;
	
	@Column(length = 100)
	private String resistenciaPermisible7;
	
	@Column(length = 100)
	private String resistenciaPermisible8;
	
	@Column(length = 100)
	private String resistenciaPermisible9;
	
	@Column(length = 100)
	private String resistenciaPermisible10;
	
	@Column(length = 100)
	private String resistenciaPermisible11;
	
	@Column(length = 100)
	private String resistenciaPermisible12;

	@Column(length = 100)
	private String resultadoMili1;
	
	@Column(length = 100)
	private String resultadoMili2;
	
	@Column(length = 100)
	private String resultadoMili3;
	
	@Column(length = 100)
	private String resultadoMili4;
	
	@Column(length = 100)
	private String resultadoMili5;

	@Column(length = 100)
	private String valorPermisibleMili1;
	
	@Column(length = 100)
	private String valorPermisibleMili2;
	
	@Column(length = 100)
	private String valorPermisibleMili3;
	
	@Column(length = 100)
	private String valorPermisibleMili4;
	
	@Column(length = 100)
	private String valorPermisibleMili5;
	
	@Column(length = 100)
	private String resultadoPosNeg1;
	
	@Column(length = 100)
	private String resultadoPosNeg2;
	
	@Column(length = 100)
	private String resultadoPosNeg3;
	
	@Column(length = 100)
	private String resultadoPosNeg4;
	
	@Column(length = 100)
	private String resultadoPosNeg5;
	
	@Column(length = 100)
	private String testPerformedBy;
	@Column(length = 100)
	private String closedDate;

	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("dateMeasurement", "Date of measurement");
		mapa.put("manufacturerDate", "Manufacturer/Date");
		mapa.put("type", "Type");
		mapa.put("serialNumber", "Serial number");
		mapa.put("mvsgCorrect", "MVSG - Correct");
		mapa.put("mvsgNotCorrect", "MVSG - Not correct");
		mapa.put("sf6Correct", "SF6 - Correct");
		mapa.put("sf6NotCorrect", "SF6 - Not correct");
		mapa.put("equipamentType1", "Test of the relay protection system - Equipment type");
		mapa.put("serialNumber1", "Test of the relay protection system - Serial number");
		mapa.put("calibrationDate1", "Test of the relay protection system - Calibration date");
		mapa.put("nextCalibrationDate1", "Test of the relay protection system - Next calibration date");
		mapa.put("equipamentType2", "Measurement of the insulation of MV switch gear - Equipment type");
		mapa.put("serialNumber2", "Measurement of the insulation of MV switch gear - Serial number");
		mapa.put("calibrationDate2", "Measurement of the insulation of MV switch gear - Calibration date");
		mapa.put("nextCalibrationDate2", "Measurement of the insulation of MV switch gear - Next calibration date");
		mapa.put("equipamentType3", "Measurement of the insulation of Stator Switch Cabinet - Equipment type");
		mapa.put("serialNumber3", "Measurement of the insulation of Stator Switch Cabinet - Serial number");
		mapa.put("calibrationDate3", "Measurement of the insulation of Stator Switch Cabinet - Calibration date");
		mapa.put("nextCalibrationDate3", "Measurement of the insulation of Stator Switch Cabinet - Next calibration date");
		mapa.put("equipamentType4", "Measurement of the continuity of the grounding system - Equipment type");
		mapa.put("serialNumber4", "Measurement of the continuity of the grounding system - Serial number");
		mapa.put("calibrationDate4", "Measurement of the continuity of the grounding system - Calibration date");
		mapa.put("nextCalibrationDate4", "Measurement of the continuity of the grounding system - Next calibration date");
		mapa.put("pongo1", "Fase[L1]>| / Pongo[UN]"); 
		mapa.put("pongo2", "Fase[L2]>| / Pongo[UN]");
		mapa.put("pongo3", "Fase[L3]>| / Pongo[UN]");
		mapa.put("pongo4", "Fase[L1]>>| / Pongo[UN]");
		mapa.put("pongo5", "Fase[L2]>>| / Pongo[UN]");
		mapa.put("pongo6", "Fase[L3]>>| / Pongo[UN]");
		mapa.put("pongo7", "Fase >|o / Pongo[UN]");
		mapa.put("pruebo1", "Fase[L1]>| / Pruebo[UN]"); 
		mapa.put("pruebo2", "Fase[L2]>| / Pruebo[UN]");
		mapa.put("pruebo3", "Fase[L3]>| / Pruebo[UN]");
		mapa.put("pruebo4", "Fase[L1]>>| / Pruebo[UN]");
		mapa.put("pruebo5", "Fase[L2]>>| / Pruebo[UN]");
		mapa.put("pruebo6", "Fase[L3]>>| / Pruebo[UN]");
		mapa.put("pruebo7", "Fase >|o / Pruebo[UN]");
		mapa.put("conjuno1", "Fase[L1]>| / T Conjuno[s]");  
		mapa.put("conjuno2", "Fase[L2]>| / T Conjuno[s]");
		mapa.put("conjuno3", "Fase[L3]>| / T Conjuno[s]");
		mapa.put("conjuno4", "Fase[L1]>>| / T Conjuno[s]");
		mapa.put("conjuno5", "Fase[L2]>>| / T Conjuno[s]");
		mapa.put("conjuno6", "Fase[L3]>>| / T Conjuno[s]");
		mapa.put("conjuno7", "Fase >|o / T Conjuno[s]");  
		mapa.put("prueba1", "Fase[L1]>| / T Prueba[s]");  
		mapa.put("prueba2", "Fase[L2]>| / T Prueba[s]");
		mapa.put("prueba3", "Fase[L3]>| / T Prueba[s]");
		mapa.put("prueba4", "Fase[L1]>>| / T Prueba[s]");
		mapa.put("prueba5", "Fase[L2]>>| / T Prueba[s]");
		mapa.put("prueba6", "Fase[L3]>>| / T Prueba[s]");
		mapa.put("prueba7", "Fase >|o / T Prueba[s]");
		mapa.put("resultado1", "Fase[L1]>| / Result[Positivo/Negativo]"); 
		mapa.put("resultado2", "Fase[L2]>| / Result[Positivo/Negativo]");
		mapa.put("resultado3", "Fase[L3]>| / Result[Positivo/Negativo]");
		mapa.put("resultado4", "Fase[L1]>>| / Result[Positivo/Negativo]");
		mapa.put("resultado5", "Fase[L2]>>| / Result[Positivo/Negativo]");
		mapa.put("resultado6", "Fase[L3]>>| / Result[Positivo/Negativo]");
		mapa.put("resultado7", "Fase >|o / Result[Positivo/Negativo]");
		mapa.put("voltage1",  "Measurement of the insulation of MV switch gear - Fase[P1-PE] / Voltaje[KV]");
		mapa.put("voltage2",  "Measurement of the insulation of MV switch gear - Fase[P2-PE] / Voltaje[KV]");
		mapa.put("voltage3",  "Measurement of the insulation of MV switch gear - Fase[P3-PE] / Voltaje[KV]");
		mapa.put("voltage4",  "Measurement of the insulation of MV switch gear - Fase[P1-P2] / Voltaje[KV]");
		mapa.put("voltage5",  "Measurement of the insulation of MV switch gear - Fase[P1-P3] / Voltaje[KV]");
		mapa.put("voltage6",  "Measurement of the insulation of MV switch gear - Fase[P2-P3] / Voltaje[KV]");
		mapa.put("voltage7",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-PE] / Voltaje[KV]");
		mapa.put("voltage8",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-PE] / Voltaje[KV]");
		mapa.put("voltage9",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P3-PE] / Voltaje[KV]");
		mapa.put("voltage10", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P2] / Voltaje[KV]");
		mapa.put("voltage11", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P3] / Voltaje[KV]");
		mapa.put("voltage12", "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-P3] / Voltaje[KV]");
		
		mapa.put("resistencia1",  "Measurement of the insulation of MV switch gear - Fase[P1-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia2",  "Measurement of the insulation of MV switch gear - Fase[P2-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia3",  "Measurement of the insulation of MV switch gear - Fase[P3-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia4",  "Measurement of the insulation of MV switch gear - Fase[P1-P2] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia5",  "Measurement of the insulation of MV switch gear - Fase[P1-P3] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia6",  "Measurement of the insulation of MV switch gear - Fase[P2-P3] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia7",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia8",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia9",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P3-PE] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia10", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P2] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia11", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P3] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistencia12", "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-P3] / Insulation Resistance[MΩ] after 60[s]");
		mapa.put("resistenciaPermisible1",  "Measurement of the insulation of MV switch gear - Fase[P1-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible2",  "Measurement of the insulation of MV switch gear - Fase[P2-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible3",  "Measurement of the insulation of MV switch gear - Fase[P3-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible4",  "Measurement of the insulation of MV switch gear - Fase[P1-P2] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible5",  "Measurement of the insulation of MV switch gear - Fase[P1-P3] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible6",  "Measurement of the insulation of MV switch gear - Fase[P2-P3] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible7",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible8",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible9",  "Measurement of the insulation of Stator Switch Cabinet - Fase[P3-PE] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible10", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P2] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible11", "Measurement of the insulation of Stator Switch Cabinet - Fase[P1-P3] / Allowable Resistance[MΩ]");
		mapa.put("resistenciaPermisible12", "Measurement of the insulation of Stator Switch Cabinet - Fase[P2-P3] / Allowable Resistance[MΩ]");
		mapa.put("resultadoMili1", "MV SG armario / Result[mΩ]");
		mapa.put("resultadoMili2", "SSC Armario / Result[mΩ]");
		mapa.put("resultadoMili3", "Trafo 30kV / Result[mΩ]");
		mapa.put("resultadoMili4", "Trafo 0,6kV / Result[mΩ]");
		mapa.put("resultadoMili5", "Director de cable / Result[mΩ]");
		mapa.put("valorPermisibleMili1", "MV SG armario / Allowable value[mΩ]");
		mapa.put("valorPermisibleMili2", "SSC Armario / Allowable value[mΩ]");
		mapa.put("valorPermisibleMili3", "Trafo 30kV / Allowable value[mΩ]");
		mapa.put("valorPermisibleMili4", "Trafo 0,6kV / Allowable value[mΩ]");
		mapa.put("valorPermisibleMili5", "Director de cable / Allowable value[mΩ]");
		mapa.put("resultadoPosNeg1", "MV SG armario / Result[Positive/Negative]");
		mapa.put("resultadoPosNeg2", "SSC Armario / Result[Positive/Negative]");
		mapa.put("resultadoPosNeg3", "Trafo 30kV / Result[Positive/Negative]");
		mapa.put("resultadoPosNeg4", "Trafo 0,6kV / Result[Positive/Negative]");
		mapa.put("resultadoPosNeg5", "Director de cable / Result[Positive/Negative]");
		mapa.put("testPerformedBy", "/Test performed by");
		mapa.put("closedDate", "Date");
		return mapa;
		
	}
}


