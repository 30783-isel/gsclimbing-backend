package com.gsclimbing.database.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gsclimbing.dto.ProjectDto;

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
    
    @OneToMany(mappedBy = "project", cascade = { CascadeType.ALL } )
    @JsonIgnore
	private List<Turbine> turbines = new ArrayList<>();
    
    
    public ProjectDto mapper() {
    	return ProjectDto.builder()
    				.idProject(this.idProject)
    				.name(this.name)
    				.country(this.country)
    				.location(this.location)
    				.numberTurbines(this.numberTurbines)
    				.site(this.site)
    				.number(this.number)
    				.type(this.type)
    				//.turbines(this.turbines.stream().map(turbine -> turbine.mapper()).collect(Collectors.toList()))
    				.users(this.users.stream().map(user -> user.mapper()).collect(Collectors.toSet()))
    				.build();
    }
}
