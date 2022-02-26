package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.database.service.UserService;

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

	@RequestMapping(method = RequestMethod.POST, value = "/create")
	public ResponseEntity<?> createProject(@RequestBody Project project) {
		if (projectService.getProjectByName(project.getName()) == null) {
			List<Turbine> turbinas = getListTurbines(project.getNumberTurbines(), project);
			project.setTurbines(turbinas);
			projectService.createProject(project);
			return null;
		}
		return new ResponseEntity<>("There is already a project with that name.", HttpStatus.EXPECTATION_FAILED);
	}

	List<Turbine> getListTurbines(int numOfElements, Project project){
	     return IntStream.range(0, numOfElements)
	              .mapToObj(i -> new Turbine(project))  
	              .collect(Collectors.toList()); 
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public List<Project> getProjects() {
		return projectService.getAllProjects();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/project-by-id/{idProject}")
	public Project getProjectByName(@PathVariable Integer idProject) {
		return projectService.getProjectById(idProject);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects-by-user/{username}")
	public List<Project> getProjectsByUserId(final @PathVariable String username) {
		User user = userService.getUser(username);
		List<Project> filteredList = null;
		if (user != null && user.getRoles().equals("ADMIN")) {
			filteredList = projectService.getAllProjects();
		}
		if (user != null && user.getRoles().equals("TECH")) {
			filteredList = new ArrayList<>(user.getProjects());
		}
		return filteredList;
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
	
	@RequestMapping(method = RequestMethod.GET, value = "/turbine-by-id/{id}")
	public Turbine getTurbine(@PathVariable int id) {
		return turbineService.getTurbine(id);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/turbines/{idProject}")
	public List<Turbine> getTurbine(@PathVariable final Integer idProject) {
		return turbineService.getTurbinesByProject(projectService.getProject(idProject));
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
}
