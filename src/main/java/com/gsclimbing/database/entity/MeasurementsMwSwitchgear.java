package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
public class MeasurementsMwSwitchgear extends Report implements Cloneable{

	@Column(length = 100)
	private String dateMeasurement;
	
	@Column(length = 100)
	private String site;
	
	@Column(length = 100)
	private String wtgNumber;
	
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

	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();

	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}	
}


