package com.gsclimbing.database.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
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
		Project project = projectRepository.findByName(name);
		projectRepository.deleteById(project.getIdProject());
	}
	
	public void updateProject(Project project) {
		projectRepository.save(project);
	}

}
