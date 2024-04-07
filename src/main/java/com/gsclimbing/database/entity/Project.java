package com.gsclimbing.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gsclimbing.dto.ProjectDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JoinTable(
        name = "ProjectUser", 
        joinColumns = { @JoinColumn(name = "idProject") }, 
        inverseJoinColumns = { @JoinColumn(name = "idUser") }
    )
    @JsonIgnoreProperties({"projects", "users"})
    Set<User> users = new HashSet<>();
    
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
    
    public boolean removeUsers(User user) {
    	this.users.remove(user);
    	user.getProjects().remove(user);
    	return true;
    }
}
