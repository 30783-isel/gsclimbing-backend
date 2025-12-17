package com.gsclimbing.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class FileData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer fileId;
	private String uuid;
	private long size;
	private String name;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	private String mimeType;
	private String hash;
	private String nameField;
	private String description;
	private String url;
	private int imageChange;
	private String isDeleted;
	private boolean insertedOnFtpServer;

	@ManyToOne
	@JoinColumn(name="idReport", nullable=true)
	@JsonIgnore
	private Report report;
	
	private static final int RADIX = 16;

	public void addImageChange() {
		this.imageChange = this.imageChange + 1;
	}

	public void setHash() throws NoSuchAlgorithmException{
		String transformedName = new StringBuilder().append(this.name).append(this.mimeType).append(this.size).append(new Date().getTime()).toString();
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(transformedName.getBytes(StandardCharsets.UTF_8));
		this.hash = new BigInteger(1, messageDigest.digest()).toString(RADIX);
	}

	public boolean isActive() {
		return !"Y".equals(isDeleted);
	}

	public void markAsDeleted() {
		this.isDeleted = "Y";
	}

}
