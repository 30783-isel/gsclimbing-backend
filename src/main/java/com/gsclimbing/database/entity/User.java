package com.gsclimbing.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gsclimbing.dto.UserDto;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
	private String name;
	private String username;
	private String password;
	private String email;
	private String active;
	private String roles;
	
    @ManyToMany(mappedBy = "users", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JsonIgnoreProperties({"projects", "users"})
    private Set<Project> projects = new HashSet<>();
    
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

