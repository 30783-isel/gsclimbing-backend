package com.gsclimbing.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
public class Alteration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idAlteration;
	private String field;
	private String fieldOld;
	private String fieldNew;
	private boolean image;
	private String hash;
	private int imageChange;
	private java.time.LocalDateTime localDateTime;
	
	private byte[] oldPicByte;
	private byte[] newPicByte;
	
	@ManyToOne
    @JoinColumn(name="idHistoricReport", nullable=true)
	@JsonIgnore
	private HistoricReport historicReport;
	
	public void addImageChange() {
		this.imageChange = this.imageChange + 1;
	}
}
