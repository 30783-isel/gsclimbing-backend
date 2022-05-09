package com.gsclimbing.dto;

import java.time.LocalDateTime;

import com.gsclimbing.database.entity.Turbine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {

	private Integer reportId;
	private String uuid;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	private String locked;
	private String permission2Edit;
	private String site;
	private String wtgNumber;
	private String wtgType;
	private String yearConstruction;
	private String insertImagesChk;
    private String additionalField1Label;
    private String additionalField1Text;
    private String additionalField2Label;
    private String additionalField2Text;
    private String additionalField3Label;
    private String additionalField3Text;
    private String additionalField4Label;
    private String additionalField4Text;
    private String additionalField5Label;
    private String additionalField5Text;
    private String additionalField6Label;
    private String additionalField6Text;
    private String additionalField7Label;
    private String additionalField7Text;
	private Integer numberPictures;
	private Integer typeReport;
    private Integer projectoId;
    private Integer turbinaId;
	private Turbine turbine;
	
}
