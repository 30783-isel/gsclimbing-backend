package com.gsclimbing.historic;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
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
	
	
	
	public int getIdAlteration() {
		return idAlteration;
	}

	public int getIdHistoricReport() {
		return idHistoricReport;
	}
	public void setIdHistoricReport(int idHistoricReport) {
		this.idHistoricReport = idHistoricReport;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	public String getFieldOld() {
		return fieldOld;
	}
	public void setFieldOld(String fieldOld) {
		this.fieldOld = fieldOld;
	}
	
	public String getFieldNew() {
		return fieldNew;
	}
	public void setFieldNew(String fieldNew) {
		this.fieldNew = fieldNew;
	}

	public boolean isImage() {
		return image;
	}
	public void setImage(boolean image) {
		this.image = image;
	}

	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public int getImageChange() {
		return imageChange;
	}

	public void setImageChange(int imageChange) {
		this.imageChange = imageChange;
	}
	
	public void addImageChange() {
		this.imageChange = this.imageChange + 1;
	}
	
	public java.time.LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(java.time.LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public byte[] getOldPicByte() {
		return oldPicByte;
	}
	public void setOldPicByte(byte[] oldPicByte) {
		this.oldPicByte = oldPicByte;
	}

	public byte[] getNewPicByte() {
		return newPicByte;
	}
	public void setNewPicByte(byte[] newPicByte) {
		this.newPicByte = newPicByte;
	}
	
	
	
}
