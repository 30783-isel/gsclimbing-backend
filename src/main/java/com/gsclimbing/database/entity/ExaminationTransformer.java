package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExaminationTransformer extends Report implements Cloneable{

	@Column(length = 100)
	private String dateOfMeasurement;
	
	@Column(length = 100)
	private String site;
	
	@Column(length = 100)
	private String wtgNumber;
	
	@Column(length = 100)
	private String manufacturer;
	
	@Column(length = 100)
	private String type;
	
	@Column(length = 100)
	private String equipamentSerialNumber;
	
	@Column(length = 5)
	private String correct1;
	
	@Column(length = 5)
	private String notCorrect1;

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

	@Column(length = 5)
	private String correct2;
	
	@Column(length = 6)
	private String notCorrect2;
	
	private String conclusion;
	
	@Column(length = 100)
	private String performedBy;
	
	@Column(length = 100)
	private String date;
}


