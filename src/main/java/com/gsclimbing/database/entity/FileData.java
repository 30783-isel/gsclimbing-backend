package com.gsclimbing.database.entity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FileData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer fileId;
	private String uuid;
	private String teamId;
	private String userId;
	private long size;
	private String name;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	private String mimeType;
	private String hash;
	private String nameField;
	private String description;
	private int imageChange;
	private boolean insertedOnFtpServer;;


	
	private static final int RADIX = 16;

	public FileData() {
	}

	@Override
	public String toString() {
		return String.format("Image[uuid=%s, mimeType=%s, teamId='%d', userId='%d'']", uuid, mimeType, teamId, userId);
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getHash() {
		return hash;
	}
	
	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isInsertedOnFtpServer() {
		return insertedOnFtpServer;
	}

	public void setInsertedOnFtpServer(boolean insertedOnFtpServer) {
		this.insertedOnFtpServer = insertedOnFtpServer;
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

	public void setHash() throws NoSuchAlgorithmException{
		String transformedName = new StringBuilder().append(this.name).append(this.mimeType).append(this.size).append(new Date().getTime()).toString();
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(transformedName.getBytes(StandardCharsets.UTF_8));
		this.hash = new BigInteger(1, messageDigest.digest()).toString(RADIX);
	}
	
}
