package com.gsclimbing.database.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gsclimbing.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer idUser;
	private String username;
	private String password;
	private String email;
	private String active;
	private String roles;
	
    @ManyToMany()
    @JoinTable(
        name = "UserProject", 
        joinColumns = { @JoinColumn(name = "idUser") }, 
        inverseJoinColumns = { @JoinColumn(name = "idProject") }
    )
    @JsonIgnoreProperties({"users", "projects"})
    Set<Project> projects = new HashSet<>();
    
    public UserDto mapper() {
    	return UserDto.builder()
    		.idUser(idUser)
    		.username(username)
    		.password(password)
    		.email(email)
    		.active(active)
    		.roles(roles)
    		.build();
    }
	
}

