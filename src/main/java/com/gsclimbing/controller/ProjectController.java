package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.QProject;
import com.gsclimbing.database.entity.QReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.dto.FilterProjectDTO;
import com.gsclimbing.dto.ProjectDto;
import com.gsclimbing.dto.TurbineDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/project")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TurbineService turbineService;

	@Autowired
	private UserService userService;

	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;

	@Autowired
	private EntityManager entityManager;
	
	
	@PostMapping(value = "/search")
	public List<QProject> searchProjects(@RequestBody FilterProjectDTO filter) {
		QProject project = QProject.project;
		JPAQuery<QProject> query = new JPAQuery<>(entityManager);
		if(filter.getName() != null) {
			query.from(project).where(project.name.contains(filter.getName()));
		}
		if(filter.getCountry() != null) {
			query.from(project).where(project.country.contains(filter.getCountry()));
		}
		if(filter.getLocation() != null) {
			query.from(project).where(project.location.contains(filter.getLocation()));
		}
		if(filter.getSite() != null) {
			query.from(project).where(project.site.contains(filter.getSite()));
		}
		List<QProject> lista = query.fetch();
		
		return lista;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/create")
	public ResponseEntity<?> createProject(@RequestBody Project project) {
		if (projectService.getProjectByName(project.getName()) == null) {
			List<Turbine> turbinas = getListTurbines(project.getNumberTurbines(), project);
			project.setTurbines(turbinas);
			projectService.createProject(project);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>("There is already a project with that name.", HttpStatus.EXPECTATION_FAILED);
		}
	}

	List<Turbine> getListTurbines(int numOfElements, Project project){
	     return IntStream.range(0, numOfElements)
	              .mapToObj(i -> new Turbine(project))  
	              .collect(Collectors.toList()); 
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public List<ProjectDto> getProjects() {
		return projectService.getAllProjects().stream().map(project -> project.mapper()).collect(Collectors.toList());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/project-by-id/{idProject}")
	public ProjectDto getProjectByName(@PathVariable Integer idProject) {
		return projectService.getProjectById(idProject).mapper();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects-by-user/{username}")
	public List<ProjectDto> getProjectsByUserId(final @PathVariable String username) {
		User user = userService.getUserByUsername(username);
		List<Project> filteredList = null;
		if (user != null && user.getRoles().equals("ADMIN")) {
			filteredList = projectService.getAllProjects();
		}
		if (user != null && user.getRoles().equals("TECH")) {
			filteredList = new ArrayList<>(user.getProjects());
		}
		return filteredList.stream().map(project -> project.mapper()).collect(Collectors.toList());
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{name}")
	public void deleteProject(@PathVariable final String name) {
		projectService.deleteProject(name);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/add-turbine/{id}")
	public ResponseEntity<?> addTurbine(@PathVariable final Integer id) {
		String message = null;
		Project project = projectService.getProjectById(id);
		if (project!= null) {
			Turbine turbine = new Turbine(project);
			project.setNumberTurbines(project.getNumberTurbines() + 1);
			turbineService.createTurbine(turbine);
			project.getTurbines().add(turbine);
			return null;
		}
		message = "Cannot insert the turbine.";
		return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/update-turbine")
	public ResponseEntity<?> updateTurbine(@RequestParam("turbineId") String turbineId, @RequestParam("defectsInspectionReport") boolean defectsInspectionReport,
			@RequestParam("examinationTransformer") boolean examinationTransformer, @RequestParam("measurements6KV") boolean measurements6KV, @RequestParam("measurements690V400V") boolean measurements690V400V,
			@RequestParam("measurementsMwSwitchgear") boolean measurementsMwSwitchgear, @RequestParam("onboardCraneInspectionReport") boolean onboardCraneInspectionReport,
			@RequestParam("performanceReportRepairElevator") boolean performanceReportRepairElevator, @RequestParam("statutoryInspectionReport") boolean statutoryInspectionReport) {
		try {
			Turbine turbine = turbineService.getTurbine(Integer.parseInt(turbineId));
			
			turbine.setDefectsInspectionReport(defectsInspectionReport);
			turbine.setDefectsInspectionReport(defectsInspectionReport);
			turbine.setExaminationTransformer(examinationTransformer);
			turbine.setMeasurements6KV(measurements6KV);
			turbine.setMeasurements690V400V(measurements690V400V);
			turbine.setMeasurementsMwSwitchgear(measurementsMwSwitchgear);
			turbine.setOnboardCraneInspectionReport(onboardCraneInspectionReport);
			turbine.setPerformanceReportRepairElevator(performanceReportRepairElevator);
			turbine.setStatutoryInspectionReport(statutoryInspectionReport);
			
			turbineService.updateTurbine(turbine);
		} catch (Exception e) {
			return new ResponseEntity<>("Turbine not updated", HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update-turbine-name")
	public ResponseEntity<?> updateTurbineName(@RequestParam("turbineId") String turbineId, @RequestParam("name") String name) {
		try {
			Turbine turbine = turbineService.getTurbine(Integer.parseInt(turbineId));
			turbine.setName(name);
			turbineService.updateTurbine(turbine);
		} catch (Exception e) {
			return new ResponseEntity<>("Turbine not updated", HttpStatus.EXPECTATION_FAILED);
		}
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/turbine-by-id/{id}")
	public TurbineDto getTurbine(@PathVariable int id) {
		TurbineDto turbineDto = turbineService.getTurbine(id) != null ? turbineService.getTurbine(id).mapper() : null;
		return turbineDto;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/turbines/{idProject}")
	public List<TurbineDto> getTurbine(@PathVariable final Integer idProject) {
		return turbineService.getTurbinesByProject(projectService.getProject(idProject) )  != null ? turbineService.getTurbinesByProject(projectService.getProject(idProject)).stream().map(turbine -> turbine.mapper()).collect(Collectors.toList()) : null;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete-turbine/{id}")
	public ResponseEntity<?> deleteTurbine(@PathVariable int id) {
		Turbine turbine = turbineService.getTurbine(id);
		int idProject = 0;
		if (turbine != null) {
		} else {
			return new ResponseEntity<>("Cannot find the turbine", HttpStatus.EXPECTATION_FAILED);
		}
		try {
			if (turbine != null) {
				turbineService.deleteTurbine(turbine);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error deleting the Turbine", HttpStatus.EXPECTATION_FAILED);
		}
		Project project = turbine.getProject();
		int numberTurbines = project.getNumberTurbines();
		project.setNumberTurbines(numberTurbines - 1);
		projectService.updateProject(project);
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/update-users-project/{projectId}/{users}")
	public void updateUserData(@PathVariable("projectId") final String projectId, @PathVariable("users") final String users) {
		
		Project project = projectService.getProjectById(Integer.valueOf(projectId));
		if (project != null) {
			Set<User> listaUsers = null;
			if (!ObjectUtils.isEmpty(users)) {
				listaUsers = Arrays.asList(users.split("-")).stream().map( id -> userService.getUserById(Integer.valueOf(id)) .orElse(null)).collect(Collectors.toSet());
				project.setUsers(listaUsers);
			} else {
				project.setUsers(null);
			}
			projectService.updateProject(project);
		}
	}
	
}
