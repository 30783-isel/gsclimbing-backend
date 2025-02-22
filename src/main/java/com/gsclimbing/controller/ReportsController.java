package com.gsclimbing.controller;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.*;
import com.gsclimbing.database.service.*;
import com.gsclimbing.dto.FilterDTO;
import com.gsclimbing.dto.ReportDto;
import com.gsclimbing.email.SendEmail;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.historic.Historic;
import com.gsclimbing.reports.extract.*;
import com.gsclimbing.reports.populater.*;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Data
@RestController
@Transactional
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
	private ExtractDataExaminationTransformer extractDataExaminationTransformer;
	@Autowired
	private ExtractDataMedidas690V400V extractDataMedidas690V400V;
	@Autowired
	private ExtractDataMedidas6Kv extractDataMedidas6Kv;
	@Autowired
	private ExtractDataMeasurementsMwSwitchgear extractDataMeasurementsMwSwitchgear;
	@Autowired
	private ExtractDataOnboardCraneInspectionReport extractDataOnboardCraneInspectionReport;
	@Autowired
	private ExtractDataPrre extractDataPrre;
	@Autowired
	private ExtractDataStatutoryInspectionReport extractDataStatutoryInspectionReport;

	@Autowired
	private DefectsInspectionPopulater defectsInspectionPopulater;
	@Autowired
	private ExaminationTransformerPopulater examinationTransformerPopulater;
	@Autowired
	private Medidas6KvPopulater medidas6KvPopulater;
	@Autowired
	private Medidas690V400VPopulater medidas690V400VPopulater;
	@Autowired
	private MeasurementsMwSwitchgearPopulater measurementsMwSwitchgearPopulater;
	@Autowired
	private OnboardCraneInspectionReportElevatorPopulater onboardCraneInspectionReportElevatorPopulater;
	@Autowired
	private PerformanceReportRepairElevatorPopulater performanceReportRepairElevatorPopulater;
	@Autowired
	private StatutoryInspectionReportElevatorPopulater statutoryInspectionReportElevatorPopulater;

	private Report report;

	@Autowired
	private EntityManager entityManager;

	@GetMapping(value = "/search")
	public ResponseEntity<Report> searchReport(FilterDTO filter) {

		QReport report = QReport.report;
		JPAQuery<QReport> queryGetByTypeReport = new JPAQuery<>(entityManager);
		queryGetByTypeReport.from(report).where(report.typeReport.eq(filter.getTypeReport()));
		List<QReport> lista = queryGetByTypeReport.fetch();

		return new ResponseEntity<Report>(new Report(), HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("project") String projectId, @RequestParam("turbineId") Integer turbineId, @RequestParam("typeReport") Integer typeReport) {
		String message = "";
		List<String> lista = null;
		Report report = null;
		instaceSelection(typeReport);
		SendEmail runnable = null;
		try {
			report = readPdf(file, projectId, turbineId, typeReport, null, "UPLOAD");
			if (report.equals(null)) {
				throw new Exception("Exception message");
			}
			lista = validateReport(report);
			if (ObjectUtils.isEmpty(lista)) {
				String username = reportService.getCurrentLoggedUser();
				User user = userService.getUserByUsername(username);
				Project project = projectService.getProjectById(Integer.parseInt(projectId));
				String subject = "User " + user.getUsername() + " inserted a new " + ReportEnum.values()[typeReport].label + " on project " + project.getName();
				byte[] bytes = null;
				bytes = generatePDF(typeReport, report);
				runnable = new SendEmail("reports@gs-climbing.com", subject, org.apache.commons.lang3.StringUtils.EMPTY, ReportEnum.values()[typeReport].label + ".pdf", bytes);
				Thread t = new Thread(runnable);
				t.start();
			} else {
				throw new Exception("Exception message");
			}
			return ResponseEntity.status(HttpStatus.OK).body(turbineService.getTurbine(turbineId));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!!!\n";
			String emptyFields = "Empty fields:\n" + convertEmptyListToString(lista);
			if(lista != null && !lista.isEmpty()){
				message = message.concat(emptyFields);
			}
			log.error(message, e);
			if (report != null) {
				String uuid = report.getUuid();
				List<FileData> listFileData = fileService.readFile(uuid);
				reportService.deleteReport(report.getReportId());
				listFileData.stream().forEach(fileData -> {
					FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
				});
			}
			return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
		}
	}

	private String convertEmptyListToString(List<String> lista) {
		StringBuilder strBuilder = new StringBuilder();
		lista.stream().forEach(str -> {
			strBuilder.append(str);
			strBuilder.append(System.lineSeparator());
		});

		return strBuilder.toString();
	}

	@PostMapping(value = "/update")
	public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file, @RequestParam("idReport") Integer idReport, @RequestParam("typeReport") Integer typeReport) {
		String message = "";
		String validateString = null;
		Report report = null;
		instaceSelection(typeReport);
		try {
			report = readPdf(file, null, null, typeReport, idReport, "UPDATE");
			if (report.equals(null)) {
				throw new Exception("Exception message");
			}
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			message = "Could not update the file: " + file.getOriginalFilename() + "!!!\n" + validateString;
			log.error(message);
			return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/delete-report/{id}")
	public Integer deleteReport(@PathVariable Integer id) {
		Report report = reportService.readReport(id);
		Integer turbineId = report.getTurbinaId();

//		Turbine turbine = report.getTurbine();
//		switch (ReportEnum.values()[report.getTypeReport()]) {
//		case DIR:
//			turbine.setDefectsInspectionReport(false);
//			break;
//		case ET:
//			turbine.setExaminationTransformer(false);
//			break;
//		case M690V400V:
//			turbine.setMeasurements690V400V(false);
//			break;
//		case M6KV:
//			turbine.setMeasurements6KV(false);
//			break;
//		case MMSSC:
//			turbine.setMeasurementsMwSwitchgear(false);
//			break;
//		case OCIR:
//			turbine.setOnboardCraneInspectionReport(false);
//			break;
//		case PRRE:
//			turbine.setPerformanceReportRepairElevator(false);
//			break;
//		case SIR:
//			turbine.setStatutoryInspectionReport(false);
//			break;
//		}

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
			User user = userService.getUserByUsername(username);
			Project project = projectService.getProjectById(report.getProjectoId());
			String subject = "User " + user.getUsername() + " asked permission to edit a Defects Inspection Report on project " + project.getName();
			byte[] bytes = null;
			List<FileData> list = fileService.readFile(report.getUuid());
			list.stream().filter(filex -> filex.getMimeType().equals("application/pdf")).findAny();
			bytes = generatePDF(report.getTypeReport(), report);
			runnable = new SendEmail("reports@gs-climbing.com", subject, org.apache.commons.lang3.StringUtils.EMPTY, ReportEnum.values()[report.getTypeReport()].label + ".pdf", bytes);
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
			setReport(new StatutoryInspectionReport());
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
			return extractDataMedidas690V400V.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case M6KV:
			return extractDataMedidas6Kv.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case OCIR:
			return extractDataOnboardCraneInspectionReport.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case PRRE:
			return extractDataPrre.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		case SIR:
			return extractDataStatutoryInspectionReport.readPDF(file, projectId, turbineId, typeReport, idReport, operation);
		}
		return null;
	}

	public byte[] generatePDF(Integer typeReport, Report report) {
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
		case SIR:
			return statutoryInspectionReportElevatorPopulater.generatePDF(report);
		}
		return null;
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

	private List<String> validateReport(Report report) {
		List<String> lista = reportService.chkIfAllFieldsNull(report);
		if (report.getListaFileData() != null) {
			for (FileData fileData : report.getListaFileData()) {
				if (fileData.getDescription() == null || fileData.getDescription().isEmpty()) {
					lista.add(System.lineSeparator() + "Description from " + fileData.getNameField() + " empty");
				}
				if (!fileData.isInsertedOnFtpServer()) {
					lista.add(System.lineSeparator() + "Image from " + fileData.getNameField() + " empty");
				}
			}
		}
		if ("Yes".equals(report.getInsertImagesChk())) {
			if ((!(report instanceof StatutoryInspectionReport) && report.getListaFileData().size() < 3) || ((report instanceof StatutoryInspectionReport) && report.getListaFileData().size() < 5)) {
				lista.add(System.lineSeparator() + "You add to insert images at least 3 images.");
			}
		}
		return lista;
	}

}
