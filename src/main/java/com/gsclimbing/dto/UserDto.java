package com.gsclimbing.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserDto implements Serializable{

	private static final long serialVersionUID = -8799969083917496826L;
	
	private Integer idUser;
	private String name;
	private String username;
	private String password;
	private String email;
	private String active;
	private String roles;
	
}
