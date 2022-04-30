package com.gsclimbing.database.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.ProjectRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	
	public Project getProjectById(Integer id){
		return projectRepository.findById(id).orElse(null);
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
		
		List<Report> listaReports = project.getTurbines().stream().map(turbine -> turbine.getListReports()).flatMap(Collection::stream).collect(Collectors.toList());
		List<FileData> listFileData = listaReports.stream().map(report->report.getListaFileData()).flatMap(Collection::stream).collect(Collectors.toList());
		listFileData.stream().forEach(fileData -> {
			fileService.deleteFile(fileData.getFileId());
			FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
			for (int i = 0; i <= 5; i++) {
				String path = "/oldImages/" + i + "/" + fileData.getHash();
				FTPDownloadFiles.deleteFile2FTPServer(path);
			}
		});
		projectRepository.deleteById(project.getIdProject());

	}
	
	public Project updateProject(Project project) {
		return projectRepository.save(project);
	}

}
