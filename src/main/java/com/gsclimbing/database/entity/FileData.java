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

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
