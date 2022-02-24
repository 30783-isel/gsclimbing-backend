package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.database.service.UserService;

import com.gsclimbing.commons.ResponseMessage;

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

		String message = null;
		if (projectService.getProjectByName(project.getName()) == null) {
			projectService.createProject(project);

			for (int i = 1; i < project.getNumberTurbines() + 1; i++) {

				Turbine turbine = new Turbine();
				turbine.setProjectId(project.getIdProject());
				turbine.setProjectName(project.getName());
				turbine.setNumberTurbine(i);
				turbineService.createTurbine(turbine);
			}

			return null;
		}
		message = "There is already a project with that name.";
		return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects-by-user/{username}")
	public List<Project> getProjectsByUserId(final @PathVariable String username) {
		Optional<User> user = userService.getUser(username);
		Set<Project> listProjectUsers = user.get().getProjects();

		List<Project> filteredList = null;

		if (user.isPresent() && user.get().getRoles().equals("ADMIN")) {
			filteredList = projectService.getAllProjects();
		}
		if (user.isPresent() && user.get().getRoles().equals("TECH")) {
			filteredList = new ArrayList<>(listProjectUsers);
		}
		return filteredList;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{name}")
	public void deleteProject(@PathVariable String name) {
		Project project = projectService.getProjectByName(name);
		List<Turbine> listaTurbinas = turbineService.getTurbinesByProjectId(project.getIdProject());
		listaTurbinas.stream().forEach(turbine -> deleteByTurbine(turbine));
		projectService.deleteProject(name);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/all")
	public List<Project> getProjects() {
		return projectService.getAllProjects();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/add-turbine/{id}")
	public ResponseEntity<?> addTurbine(@PathVariable int id) {
		String message = null;
		Optional<Project> project = projectService.getProjectById(id);
		if (project.isPresent()) {
			Turbine turbine = new Turbine();
			turbine.setProjectId(project.get().getIdProject());
			turbine.setProjectName(project.get().getName());
			turbine.setNumberTurbine(project.get().getNumberTurbines() + 1);
			project.get().setNumberTurbines(project.get().getNumberTurbines() + 1);
			turbineService.createTurbine(turbine);
			message = "Turbine added.";
			return null;
		}
		message = "Cannot insert the turbine.";
		return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
	}

	private void deleteByTurbine(Turbine turbine) {
		turbineService.deleteTurbine(turbine);
		String turbineId = String.valueOf(turbine.getId());

		defectsInspectionReportService.deleteDefectsInspectionReportByTurbineId(turbineId);
//		examinationTransformerService.deleteExaminationTransformerByTurbineId(turbineId);
//		masurementsMwSwitchgearService.deleteMeasurementsMwSwitchgearByTurbineId(turbineId);
//		medidas690V400VService.deleteMedidas690V400VByTurbineId(turbineId);
//		medidas6KvService.deleteMedidas6KvByTurbineId(turbineId);
//		onboardCraneInspectionReportService.deleteOnboardCraneInspectionReportByTurbineId(turbineId);
//		performanceReportRepairElevatorService.deletePerformanceReportRepairElevatorByTurbineId(turbineId);
//		statutoryInspectionReportService.deleteStatutoryInspectionReportByTurbineId(turbineId);
	}
}
