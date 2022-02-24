package com.gsclimbing.database.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer idProject;
	private String name;
	private String country;
	private String location;
	private Integer numberTurbines;
	private String site;
	private String number;
	private String type;
	
    @ManyToMany(mappedBy = "projects")
    @JsonIgnoreProperties({"users", "projects"})
    private Set<User> users = new HashSet<>();
	
}
