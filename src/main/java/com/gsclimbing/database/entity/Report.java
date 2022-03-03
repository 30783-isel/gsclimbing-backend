package com.gsclimbing.database.entity;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.gsclimbing.dto.ReportDto;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class Report {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	private int numberPictures;
	private Integer type;
    private Integer projectoId;
    private Integer turbinaId;
	
	
	public void addOneMorePicture() {
		this.numberPictures = this.numberPictures + 1;
	}
	
	public ReportDto mapper() {
		return ReportDto.builder()
			.reportId(reportId)
			.uuid(uuid)
			.createDate(createDate)
			.modifiedDate(modifiedDate)
			.locked(locked)
			.permission2Edit(permission2Edit)
			.site(site)
			.wtgNumber(wtgNumber)
			.wtgType(wtgType)
			.yearConstruction(yearConstruction)
			.numberPictures(numberPictures)
			.type(type)
			.projectoId(projectoId)
			.turbinaId(turbinaId)
			.build();
	}
}
