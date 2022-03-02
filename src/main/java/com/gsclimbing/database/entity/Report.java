package com.gsclimbing.database.entity;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
	
	public void addOneMorePicture() {
		this.numberPictures = this.numberPictures + 1;
	}
}
