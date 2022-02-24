package com.gsclimbing.historic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class HistoricReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idHistoricReport;
	private int idReport;
	private java.time.LocalDateTime localDateTime;
	private int typeReport;
	private String idProject;
	private String project;
	private String user;
	private String idUser;
	private int numAlterations;
	
	public void addNumAlterations() {
		this.numAlterations = this.numAlterations + 1;
	}
}
