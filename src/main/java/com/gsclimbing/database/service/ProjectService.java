package com.gsclimbing.database.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserService userService;
	
	public Optional<Project> getProjectById(int id){
		return projectRepository.findById(id);
	}
	
	public Project getProjectByName(String name){
		return projectRepository.findByName(name);
	}
	
	public Project getProject(int id){
		Optional<Project> project = projectRepository.findById(id);
		
		 return project.isPresent() ? project.get() : null;
	}
	
	public List<Project> getAllProjects(){
		return projectRepository.findAll();
	}
	
	public void createProject(Project project) {
		projectRepository.save(project);
	}
	
	public void deleteProject(Integer id) {
		projectRepository.deleteById(id);
	}
	
	public void deleteProject(String name) {
		for (User user : userService.getAllUsers()) {
			Set<Project> listaProjects = user.getProjects().stream().filter(project -> !name.equals(project.getName())).collect(Collectors.toSet());
			user.setProjects(listaProjects);
		}
		Project project = projectRepository.findByName(name);
		projectRepository.deleteById(project.getIdProject());
	}
	
	public void updateProject(Project project) {
		projectRepository.save(project);
	}

}
