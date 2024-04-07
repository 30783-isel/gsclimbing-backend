package com.gsclimbing.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
	private Report report;
	
    @OneToMany(mappedBy = "historicReport", cascade = { CascadeType.ALL } )
    @JsonIgnore
	private List<Alteration> listAlternation = new ArrayList<>();
	
	public void addNumAlterations() {
		this.numAlterations = this.numAlterations + 1;
	}
}
