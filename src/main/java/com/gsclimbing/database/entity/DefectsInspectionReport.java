package com.gsclimbing.database.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DefectsInspectionReport implements Cloneable {

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
	
    private Integer projectozinhoId;
    private Integer turbinazinhaId;
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turbine_id")
    @JsonIgnore
	private Turbine turbine;
	
    @OneToMany(mappedBy = "defectInspectionReport", cascade = { CascadeType.ALL } )
    @JsonIgnore
	private List<HistoricReport> listHistoric = new ArrayList<>();
	
	@Transient
	private List<FileData> listImages = new ArrayList<FileData>();
	
	public void addImgOnListImages(FileData fileData) {
		this.listImages.add(fileData);
	}
	
	public void addOneMorePicture() {
		this.numberPictures = this.numberPictures + 1;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
