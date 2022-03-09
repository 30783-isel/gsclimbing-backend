package com.gsclimbing.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.ExaminationTransformer;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.MeasurementsMwSwitchgear;
import com.gsclimbing.database.entity.Medidas690V400V;
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.entity.OnboardCraneInspectionReport;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.ReportService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.dto.ReportDto;
import com.gsclimbing.email.SendEmail;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.historic.Historic;
import com.gsclimbing.reports.extract.ExtractDataExaminationTransformer;
import com.gsclimbing.reports.extract.ExtractDataMeasurementsMwSwitchgear;
import com.gsclimbing.reports.extract.ExtractDataOnboardCraneInspectionReport;
import com.gsclimbing.reports.extract.ExtractDataPrre;
import com.gsclimbing.reports.extract.ExtractDefectsInspection;
import com.gsclimbing.reports.populater.DefectsInspectionPopulater;
import com.gsclimbing.reports.populater.ExaminationTransformerPopulater;
import com.gsclimbing.reports.populater.MeasurementsMwSwitchgearPopulater;
import com.gsclimbing.reports.populater.Medidas690V400VPopulater;
import com.gsclimbing.reports.populater.Medidas6KvPopulater;
import com.gsclimbing.reports.populater.OnboardCraneInspectionReportElevatorPopulater;
import com.gsclimbing.reports.populater.PerformanceReportRepairElevatorPopulater;

import lombok.Data;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Data
@RestController
@RequestMapping(path = "/api/reports")
public class ReportsController {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private TurbineService turbineService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private FileService fileService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserService userService;
	@Autowired
	private AlterationService alterationService;
	@Autowired
	private ExtractDefectsInspection extractDefectsInspection;
	@Autowired
	private DefectsInspectionPopulater defectsInspectionPopulater;
	@Autowired
	private ExtractDataExaminationTransformer extractDataExaminationTransformer;
	@Autowired
	private ExaminationTransformerPopulater examinationTransformerPopulater;
	@Autowired
	private ExtractDataMeasurementsMwSwitchgear extractDataMeasurementsMwSwitchgear;
	@Autowired
	private ExtractDataOnboardCraneInspectionReport extractDataOnboardCraneInspectionReport;
	
	@Autowired
	private Medidas6KvPopulater medidas6KvPopulater;
	@Autowired
	private Medidas690V400VPopulater medidas690V400VPopulater;
	@Autowired
	private OnboardCraneInspectionReportElevatorPopulater onboardCraneInspectionReportElevatorPopulater;
	@Autowired
	private PerformanceReportRepairElevatorPopulater performanceReportRepairElevatorPopulater;
	@Autowired
	private ExtractDataPrre extractDataPrre;
	
	@Autowired
	private MeasurementsMwSwitchgearPopulater measurementsMwSwitchgearPopulater;

	private Report report;
	
	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("project") String projectId, @RequestParam("turbineId") Integer turbineId, @RequestParam("typeReport") Integer typeReport) {
		String message = "";
		String validateString = null;
		Report report = null;
		instaceSelection(typeReport);
		SendEmail runnable = null;
		try {
			report = readPdf(file, projectId, turbineId, typeReport, null, "UPLOAD");
			Turbine turbine = turbineService.getTurbine(turbineId);
			turbine.getListReports().add(report);
			if (ObjectUtils.isEmpty(validateString)) {
				String username = reportService.getCurrentLoggedUser();
				User user = userService.getUser(username);
				Project project = projectService.getProjectById(Integer.parseInt(projectId));
				String subject = "User " + user.getUsername() + " inserted a new Defects Inspection Report on project " + project.getName();
				byte[] bytes = null;
				bytes = generatePDF(typeReport, getReport());
				runnable = new SendEmail(subject, "Defects Inspection Report.pdf", bytes);
				Thread t = new Thread(runnable);
				t.start();
			} else {
				throw new Exception("Exception message");
			}
			return ResponseEntity.status(HttpStatus.OK).body(turbine);
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!!!\n" + validateString;
			if (report != null) {
				String uuid = report.getUuid();
				List<FileData> listFileData = fileService.readFile(uuid);
				listFileData.stream().forEach(fileData -> {
					fileService.deleteFile(fileData.getFileId());
					FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
				});
				reportService.deleteReport(report.getReportId());
			}
			return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	public void instaceSelection(Integer typeReport) {
		ReportEnum reportEnum = ReportEnum.values()[typeReport];
		switch (reportEnum) {
		case DIR:
			setReport(new DefectsInspectionReport());
			break;
		case ET:
			setReport(new ExaminationTransformer());
			break;
		case MMSSC:
			setReport(new MeasurementsMwSwitchgear());
			break;
		case M690V400V:
			setReport(new Medidas690V400V());
			break;
		case M6KV:
			setReport(new Medidas6Kv());
			break;
		case OCIR:
			setReport(new OnboardCraneInspectionReport());
			break;
		case PRRE:
			setReport(new PerformanceReportRepairElevator());
			break;
		case SIR:
			setReport(new PerformanceReportRepairElevator());
			break;
		default:
			break;
		}
	}
	
	public Report readPdf(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operation) throws IOException {
		ReportEnum reportEnum = ReportEnum.values()[typeReport];
		switch (reportEnum) {
		case DIR:
			return extractDefectsInspection.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case ET:
			return extractDataExaminationTransformer.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case MMSSC:
			return extractDataMeasurementsMwSwitchgear.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case M690V400V:
			return extractDataMeasurementsMwSwitchgear.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case M6KV:
			return extractDataMeasurementsMwSwitchgear.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case OCIR:
			return extractDataOnboardCraneInspectionReport.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case PRRE:
			return extractDataPrre.readPDF(file, projectId, turbineId, typeReport, idReport, operation);

		}
		return null;
	}
	
	public byte[] generatePDF(Integer typeReport, Report report)  {
		ReportEnum reportEnum = ReportEnum.values()[typeReport];
		switch (reportEnum) {
		case DIR:
			return defectsInspectionPopulater.generatePDF(report);
		case ET:
			return examinationTransformerPopulater.generatePDF(report);
		case MMSSC:
			return measurementsMwSwitchgearPopulater.generatePDF(report);
		case M690V400V:
			return medidas690V400VPopulater.generatePDF(report);
		case M6KV:
			return medidas6KvPopulater.generatePDF(report);
		case OCIR:
			return onboardCraneInspectionReportElevatorPopulater.generatePDF(report);
		case PRRE:
			return performanceReportRepairElevatorPopulater.generatePDF(report);
		}
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file, @RequestParam("idReport") Integer idReport, @RequestParam("typeReport") Integer typeReport) {
		String message = "";
		String validateString = null;
		Report report = null;
		instaceSelection(typeReport);
		try {
			report = readPdf(file, null, null, typeReport, idReport, "UPDATE");
			validateString = validateReport(report);
			return null;
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!!!\n" + validateString;
			if (report != null) {
				String uuid = report.getUuid();
				List<FileData> listFileData = fileService.readFile(uuid);
				listFileData.stream().forEach(fileData -> {
					fileService.deleteFile(fileData.getFileId());
					FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
				});
				reportService.deleteReport(report.getReportId());
			}
			return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete-report/{id}")
	public Integer deleteReport(@PathVariable Integer id) {
		Integer turbineId = reportService.readReport(id).getTurbinaId();
		reportService.deleteReport(id);
		return turbineId;
	}

	@RequestMapping("/turbine-report/{turbineId}")
	public Report readReportByTurbine(final @PathVariable Integer turbineId) {
		Turbine turbine = turbineService.getTurbine(turbineId);
		return turbine.getListReports().isEmpty() ? null : turbine.getListReports().get(0);
	}

	@RequestMapping("/report/{id}")
	public ReportDto readReport(@PathVariable Integer id) {
		return reportService.readReport(id).mapper();
	}

	@RequestMapping("/permission2edit/{id}")
	public Report permission2edit(@PathVariable Integer id) {
		Report report = reportService.readReport(id);
		if (report != null) {
			report.setPermission2Edit("true");
			reportService.updateReport(report);
			SendEmail runnable = null;
			String username = reportService.getCurrentLoggedUser();
			User user = userService.getUser(username);
			Project project = projectService.getProjectById(report.getProjectoId());
			String subject = "User " + user.getUsername() + " asked permission to edit a Defects Inspection Report on project " + project.getName();
			byte[] bytes = null;
			List<FileData> list = fileService.readFile(report.getUuid());
			list.stream().filter(filex -> filex.getMimeType().equals("application/pdf")).findAny();
			bytes = defectsInspectionPopulater.generatePDF(report);
			//TODO
			//bytes = generatePDF(typeReport, getReport());
			runnable = new SendEmail(subject, "Defects Inspection Report.pdf", bytes);
			Thread t = new Thread(runnable);
			t.start();
		}
		return report;
	}

	@RequestMapping("/permission2edit_granted/{id}")
	public Report permission2edit_granted(@PathVariable Integer id) {
		Report report = reportService.readReport(id);
		if (report != null) {
			report.setLocked("false");
			report.setPermission2Edit("false");
			reportService.updateReport(report);
		}
		return report;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/historic/{id}")
	public List<Historic> getHistoricReport(@PathVariable Integer id) {
		List<Historic> listHistoric = new ArrayList<Historic>();
		List<HistoricReport> listHistoricRecord = reportService.readReport(id).getListHistoric();
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

	private String validateReport(Report report) {
		List<String> lista = reportService.chkIfAllFieldsNull(report);
		StringBuilder string = new StringBuilder();
		if (report.getSite() == null || report.getSite().isEmpty()) {
			string.append(System.lineSeparator() + "Field Site empty");
		}
		if (report.getWtgNumber() == null || report.getWtgNumber().isEmpty()) {
			string.append(System.lineSeparator() + "Field WTG Number empty");
		}
		if (report.getWtgType() == null || report.getWtgType().isEmpty()) {
			string.append(System.lineSeparator() + "Field WTG Type empty");
		}
		if (report.getYearConstruction() == null || report.getYearConstruction().isEmpty()) {
			string.append(System.lineSeparator() + "Field Year of Construction empty");
		}
		for (FileData fileData : report.getListaFileData()) {
			if (fileData.getDescription() == null || fileData.getDescription().isEmpty()) {
				string.append(System.lineSeparator() + "Field " + fileData.getNameField() + " empty");
			}
			if (!fileData.isInsertedOnFtpServer()) {
				string.append(System.lineSeparator() + "Image from " + fileData.getNameField() + " empty");
			}
		}
		return string.toString();
	}

}
