package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HistoricReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idHistoricReport;
	private java.time.LocalDateTime localDateTime;
	private int typeReport;
	private String user;
	private String idUser;
	private int numAlterations;
	
	@ManyToOne
    @JoinColumn(name="idReport", nullable=true)
	@JsonIgnore
	private DefectsInspectionReport defectInspectionReport;
	
    @OneToMany(mappedBy = "historicReport", cascade = { CascadeType.ALL } )
    @JsonIgnore
	private List<Alteration> listAlternation = new ArrayList<>();
	
	public void addNumAlterations() {
		this.numAlterations = this.numAlterations + 1;
	}
}
