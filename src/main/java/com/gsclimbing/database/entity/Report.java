package com.gsclimbing.database.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsclimbing.dto.ReportDto;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
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
	private Integer typeReport;
    private Integer projectoId;
    private Integer turbinaId;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turbineId", nullable=true)
    @JsonIgnore
	private Turbine turbine;
	
    @OneToMany(mappedBy = "report", cascade = { CascadeType.ALL } )
    @JsonIgnore
	private List<HistoricReport> listHistoric = new ArrayList<>();
    
	@OneToMany(mappedBy = "report", cascade = { CascadeType.ALL } )
	@JsonIgnore
	private List<FileData> listaFileData = new ArrayList<>();
	
	public void addImgOnListImages(FileData fileData) {
		this.listaFileData.add(fileData);
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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
			.typeReport(typeReport)
			.projectoId(projectoId)
			.turbinaId(turbinaId)
			.build();
	}
	
	public HashMap<String, String> mapeamento(){
		HashMap<String, String> mapa = new HashMap<String, String>();	
		mapa.put("reportId", "Id");
		mapa.put("uuid", "UUID");
		mapa.put("createDate", "Creation Date");
		mapa.put("modifiedDate", "Modified Date");
		mapa.put("locked", "Locked");
		mapa.put("permission2Edit", "Permission to Edit");
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("wtgType", "WTG Type");
		mapa.put("yearConstruction", "Year of Construction");
		mapa.put("typeReport", "Type of Report");
		mapa.put("projectoId", "Project Id");
		mapa.put("turbinaId", "Turbine Id");
		return mapa;
	}
}
