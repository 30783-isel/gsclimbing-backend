package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
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

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.email.SendEmail;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.historic.Historic;
import com.gsclimbing.reports.extract.ExtractDefectsInspection;
import com.gsclimbing.reports.populater.DefectsInspectionPopulater;

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

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Autowired
	private DefectsInspectionPopulater defectsInspectionPopulater;
	
	@Autowired
	private HistoricReportService historicReportService;
	
	@Autowired
	private AlterationService alterationService;
	
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
			validateString = validateReport(defectsInspectionReport);
			if (ObjectUtils.isEmpty(validateString)) {
				String username = defectsInspectionReportService.getCurrentLoggedUser();
				User user = userService.getUser(username);
				Project project = projectService.getProjectById(defectsInspectionReport.getProjectozinhoId());
				String subject = "User " + user.getUsername() + " inserted a new Defects Inspection Report on project " + project.getName();
				byte[] bytes = null;
				bytes = defectsInspectionPopulater.generatePDF(defectsInspectionReport);
				runnable = new SendEmail(subject, "Defects Inspection Report.pdf", bytes);
				Thread t = new Thread(runnable);
				t.start();
			} else {
				throw new Exception("Exception message");
			}

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

	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file, @RequestParam("idReport") Integer idReport) {
		String message = "";
		String validateString = null;
		DefectsInspectionReport defectsInspectionReport = null;
		SendEmail runnable = null;
		try {
			defectsInspectionReport = extractData.readPDF(file, null, null, idReport, "UPDATE");
			validateString = validateReport(defectsInspectionReport);
			return null;
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
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete-report/{id}")
	public void deleteDefectsInspectionReport(@PathVariable Integer id) {
		defectsInspectionReportService.deleteDefectsInspectionReport(id);
	}

	@RequestMapping("/turbine-report/{turbineId}")
	public DefectsInspectionReport readDefectsInspectionReportByTurbine(final @PathVariable Integer turbineId) {
		Turbine turbine = turbineService.getTurbine(turbineId);
		return turbine.getDefectsInspectionReportOnTurbine();
	}

	@RequestMapping("/report/{id}")
	public DefectsInspectionReport readDefectsInspectionReport(@PathVariable Integer id) {
		return defectsInspectionReportService.readDefectsInspectionReport(id);
	}

	@RequestMapping("/permission2edit/{id}")
	public DefectsInspectionReport permission2edit(@PathVariable Integer id) {
		DefectsInspectionReport report = defectsInspectionReportService.readDefectsInspectionReport(id);
		if (report != null) {
			report.setPermission2Edit("true");
			defectsInspectionReportService.updateDefectsInspectionReport(report);
			SendEmail runnable = null;
			String username = defectsInspectionReportService.getCurrentLoggedUser();
			User user = userService.getUser(username);
			Project project = projectService.getProjectById(report.getProjectozinhoId());
			String subject = "User " + user.getUsername() + " asked permission to edit a Defects Inspection Report on project " + project.getName();
			Optional<FileData> fileData = null;
			byte[] bytes = null;
			List<FileData> list = fileService.readFile(report.getUuid());
			fileData = list.stream().filter(filex -> filex.getMimeType().equals("application/pdf")).findAny();
			bytes = defectsInspectionPopulater.generatePDF(report);
			runnable = new SendEmail(subject, "Defects Inspection Report.pdf", bytes);
			Thread t = new Thread(runnable);
			t.start();
		}
		return report;
	}

	@RequestMapping("/permission2edit_granted/{id}")
	public DefectsInspectionReport permission2edit_granted(@PathVariable Integer id) {
		DefectsInspectionReport report = defectsInspectionReportService.readDefectsInspectionReport(id);
		if (report != null) {
			report.setLocked("false");
			report.setPermission2Edit("false");
			defectsInspectionReportService.updateDefectsInspectionReport(report);
		}
		return report;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/historic/{id}")
	public List<Historic> getHistoricDefectsInspectionReport(@PathVariable Integer id) {
		List<Historic> listHistoric = new ArrayList<Historic>();
		List<HistoricReport> listHistoricRecord = historicReportService.getHistoricReportByIdReportAndTypeReport(id, 1);
		for (HistoricReport historicReport : listHistoricRecord) {
			List<Alteration> listAlterations = new ArrayList<Alteration>();
			listAlterations = alterationService.getListAlterationsByIdHistoricReport(historicReport.getIdHistoricReport());
			List<Alteration> listAlterations_ = addImages2Alterations(listAlterations);
			Historic historic = new Historic();
			historic.setHistoricRecord(historicReport);
			historic.setListAlterations(listAlterations_);
			listHistoric.add(historic);
		}
		Collections.sort(listHistoric, Collections.reverseOrder());
		return listHistoric;
	}

	private List<Alteration> addImages2Alterations(List<Alteration> listAlterations) {
		List<Alteration> listAlterationsWithImages = new ArrayList<Alteration>();
		for (int i = 0; i < listAlterations.size(); i++) {
			Alteration alteration = listAlterations.get(i);
			if (alteration.isImage()) {
				byte[] bytesOldImage = null;
				byte[] bytesNewImage = null;
				bytesOldImage = FTPDownloadFiles.downloadImageFromOldImages(alteration.getHash(), alteration.getImageChange());
				if (alteration.getImageChange() < 5) {
					int idx = alteration.getImageChange() + 1;
					if (idx < 5) {
						bytesNewImage = FTPDownloadFiles.downloadImageFromOldImages(alteration.getHash(), alteration.getImageChange() + 1);
					}
				}
				if (bytesNewImage == null) {
					bytesNewImage = FTPDownloadFiles.downloadFile2FTPServer(alteration.getHash());
				}
				alteration.setOldPicByte(bytesOldImage);
				alteration.setNewPicByte(bytesNewImage);
			}
			listAlterationsWithImages.add(alteration);
		}
		return listAlterationsWithImages;
	}

	private String validateReport(DefectsInspectionReport defectsInspectionReport) {
//		List<String> lista = defectsInspectionReportService.chkIfAllFieldsNull(defectsInspectionReport);
//		StringBuilder string = new StringBuilder();
//		if (defectsInspectionReport.getSite() == null || defectsInspectionReport.getSite().isEmpty()) {
//			string.append(System.lineSeparator() + "Field Site empty");
//		}
//		if (defectsInspectionReport.getWtgNumber() == null || defectsInspectionReport.getWtgNumber().isEmpty()) {
//			string.append(System.lineSeparator() + "Field WTG Number empty");
//		}
//		if (defectsInspectionReport.getWtgType() == null || defectsInspectionReport.getWtgType().isEmpty()) {
//			string.append(System.lineSeparator() + "Field WTG Type empty");
//		}
//		if (defectsInspectionReport.getYearConstruction() == null || defectsInspectionReport.getYearConstruction().isEmpty()) {
//			string.append(System.lineSeparator() + "Field Year of Construction empty");
//		}
//		for (FileData fileData : defectsInspectionReport.getListImages()) {
//			if (fileData.getDescription() == null || fileData.getDescription().isEmpty()) {
//				string.append(System.lineSeparator() + "Field " + fileData.getNameField() + " empty");
//			}
//			if (!fileData.isInsertedOnFtpServer()) {
//				string.append(System.lineSeparator() + "Image from " + fileData.getNameField() + " empty");
//			}
//		}
//		return string.toString();
		return null;
	}

}
