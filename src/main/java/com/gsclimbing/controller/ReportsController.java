package com.gsclimbing.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.email.SendEmail;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.reports.extract.ExtractDefectsInspection;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/reports")
public class ReportsController {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TurbineService turbineService;
	
	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;

	@Autowired
	private ExtractDefectsInspection extractData;

	@Autowired
	private FileService fileService;

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("project") String projectId, @RequestParam("turbineId") Integer turbineId) {
		String message = "";
		String validateString = null;
		DefectsInspectionReport defectsInspectionReport = null;
		SendEmail runnable = null;
		try {

			defectsInspectionReport = extractData.readPDF(file, projectId, turbineId, null, "UPLOAD");
			Turbine turbine = turbineService.getTurbine(turbineId);
			turbine.setDefectsInspectionReportOnTurbine(defectsInspectionReport);
			log.info("Turbine updated");
//			validateString = validateReport(defectsInspectionReport);

//			if (validateString.isEmpty()) {
//
//				Optional<Turbine> turbine = turbineService.getTurbine(Integer.parseInt(turbineId));
//
//				int val = turbine.get().getDefectsInspectionReportInserted() + 1;
//				
//				turbine.get().setDefectsInspectionReportInserted(val);
//
//				turbineService.updateTurbine(turbine.get());
//
//				String username = defectsInspectionReportService.getCurrentLoggedUser();
//				Optional<User> user = userService.findByUsername(username);
//			
//				Optional<Project> projectI = projectService.getProjectById(Integer.parseInt(defectsInspectionReport.getProjectId()));
//				String subject = "User " + user.get().getUsername() + " inserted a new Defects Inspection Report on project "+ projectI.get().getName();
//
//				byte[] bytes = null;
//				bytes = populater.generatePDF(defectsInspectionReport);
//				
//				runnable = new SendEmail(subject, "Defects Inspection Report.pdf", bytes);
//				Thread t = new Thread(runnable);
//				t.start();
//
//			} else {
//				throw new Exception("Exception message");
//			}
//			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			//return new ResponseEntity<>(turbineService.getTurbine(turbineId), HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(turbine);
		} catch (Exception e) {

			message = "Could not upload the file: " + file.getOriginalFilename() + "!!!\n" + validateString;

			if (defectsInspectionReport != null) {

				String uuid = defectsInspectionReport.getUuid();
				List<FileData> listFileData = fileService.readFile(uuid);

				listFileData.stream().forEach(fileData -> {
					fileService.deleteFile(fileData.getFileId());
					FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
				});
				defectsInspectionReportService.deleteDefectsInspectionReport(defectsInspectionReport.getReportId());
			}
			return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping("/turbine-report/{turbineId}")
	public DefectsInspectionReport readDefectsInspectionReportByTurbine(final @PathVariable Integer turbineId ) {
		Turbine turbine = turbineService.getTurbine(turbineId);
		return turbine.getDefectsInspectionReportOnTurbine();
	}

}
