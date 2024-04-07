package com.gsclimbing.dto;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProjectDto implements Serializable{
	
	private static final long serialVersionUID = 1387505742704228105L;
	
	private Integer idProject;
	private String name;
	private String country;
	private String location;
	private Integer numberTurbines;
	private String site;
	private String number;
	private String type;
	@Default
	private Set<UserDto> users = new HashSet<>();
	@Default
	private List<TurbineDto> turbines = new ArrayList<>();

}
