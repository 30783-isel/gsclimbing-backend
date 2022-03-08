package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;

@Data
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
	
	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();

	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}

}


