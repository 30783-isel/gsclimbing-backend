package com.gsclimbing.database.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Alteration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idAlteration;
	private int idHistoricReport;
	private String field;
	private String fieldOld;
	private String fieldNew;
	private boolean image;
	private String hash;
	private int imageChange;
	private java.time.LocalDateTime localDateTime;
	
	private byte[] oldPicByte;
	private byte[] newPicByte;
	
	public void addImageChange() {
		this.imageChange = this.imageChange + 1;
	}
}
