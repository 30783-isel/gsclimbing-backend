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
	private Integer numberPictures;
	private Integer type;
    private Integer projectoId;
    private Integer turbinaId;
	private Turbine turbine;
	
}
