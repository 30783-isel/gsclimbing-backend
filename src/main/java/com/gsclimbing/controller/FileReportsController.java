package com.gsclimbing.controller;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.*;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.reports.populater.*;
import com.gsclimbing.zip.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Transactional
@RestController
@RequestMapping(path = "/api/reports/files")
public class FileReportsController {
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TurbineService turbineService;
	
	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;
	@Autowired
	private DefectsInspectionPopulater defectsInspectionPopulater;
	@Autowired
	private ExaminationTransformerService examinationTransformerService;
	@Autowired
	private ExaminationTransformerPopulater examinationTransformerPopulater;
	@Autowired
	private Medidas6KvService medidas6KvService;
	@Autowired
	private Medidas6KvPopulater medidas6KvPopulater;
	@Autowired
	private Medidas690V400VService medidas690V400VService;
	@Autowired
	private Medidas690V400VPopulater medidas690V400VPopulater;
	@Autowired
	private MeasurementsMwSwitchgearService measurementsMwSwitchgearService;
	@Autowired
	private MeasurementsMwSwitchgearPopulater measurementsMwSwitchgearPopulater;
	@Autowired
	private OnboardCraneInspectionReportService onboardCraneInspectionReportService;
	@Autowired
	private OnboardCraneInspectionReportElevatorPopulater onboardCraneInspectionReportElevatorPopulater;
	@Autowired
	private PerformanceReportRepairElevatorService performanceReportRepairElevatorService;
	@Autowired
	private PerformanceReportRepairElevatorPopulater performanceReportRepairElevatorPopulater;
	@Autowired
	private StatutoryInspectionReportReportService statutoryInspectionReportReportService;
	@Autowired
	private StatutoryInspectionReportElevatorPopulater statutoryInspectionReportElevatorPopulater;
	
	@Autowired
	private FileService fileService;
	

	
	@RequestMapping("/download_pdf/{typeReport}/{id}")
	public ResponseEntity<byte[]> getFileByReportId(final @PathVariable Integer typeReport, final @PathVariable Integer id) {
		byte[] bytes = null;
		String fileName = null;
		bytes = donwloadPdf(typeReport, id,fileName);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(bytes);
	}
	
	public byte[]  donwloadPdf(Integer typeReport, Integer id, String fileName) {
		ReportEnum reportEnum = ReportEnum.values()[typeReport];
		switch (reportEnum) {
		case DIR:
			return defectsInspectionPopulater.generatePDF(defectsInspectionReportService.readDefectsInspectionReport(id));
		case ET:
			return examinationTransformerPopulater.generatePDF(examinationTransformerService.readExaminationTransformer(id));
		case MMSSC:
			return measurementsMwSwitchgearPopulater.generatePDF(measurementsMwSwitchgearService.readMeasurementsMwSwitchgear(id));
		case M6KV:
			return medidas6KvPopulater.generatePDF(medidas6KvService.readMedidas6Kv(id));
		case M690V400V:
			return medidas690V400VPopulater.generatePDF(medidas690V400VService.readMedidas690V400V(id));
		case OCIR:
			return onboardCraneInspectionReportElevatorPopulater.generatePDF(onboardCraneInspectionReportService.readOnboardCraneInspectionReport(id));
		case PRRE:
			return performanceReportRepairElevatorPopulater.generatePDF(performanceReportRepairElevatorService.readPerformanceReportRepairElevator(id));
		case SIR:
			return statutoryInspectionReportElevatorPopulater.generatePDF(statutoryInspectionReportReportService.readStatutoryInspectionReport(id));
		}
		return null;
	}
	
	@RequestMapping("/download_zip/{projectId}")
	public ResponseEntity<byte[]> getZipFile(@PathVariable int projectId) throws IOException {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectService.getProject(projectId).getName() + ".zip" + "\"").body(getReportsOnZip(projectId));
		//return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectService.getProject(projectId).getName() + ".zip" + "\"").body(getCleanReportsOnZip(projectId));
	}
	private byte[] getReportsOnZip(int projectId) throws IOException {
		Path tmpDirOrig = Files.createTempDirectory(null);
		for (Turbine turbine : turbineService.getTurbinesByProject(projectService.getProject(projectId))) {
			Path path = Files.createTempDirectory(tmpDirOrig, turbine.getName() + " - ");
			turbine.getListReports().stream().forEach(report -> {
				byte[] byteArrayPDF = donwloadPdf(report.getTypeReport(), report.getReportId(), ReportEnum.values()[report.getTypeReport()].name() );
				File file = new File(path.toString(), ReportEnum.values()[report.getTypeReport()].getLabel() + ".pdf");
				try {
					OutputStream outStream = new FileOutputStream(file);
					outStream.write(byteArrayPDF);
				} catch (IOException e) {

					e.printStackTrace();
				}
			});
		}
		return FileUtils.readFileToByteArray(new File(ZipUtils.ZipDirectory(tmpDirOrig.toString(), projectService.getProject(projectId).getName())));
	}

	@RequestMapping("/download_clean_zip/{projectId}")
	public ResponseEntity<byte[]> getCleanZipFile(@PathVariable int projectId) throws IOException {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectService.getProject(projectId).getName() + ".zip" + "\"").body(getCleanReportsOnZip(projectId));
	}
	private byte[] getCleanReportsOnZip(int projectId) throws IOException {
		boolean defectInspectionReportBool = false;
		byte[] bytesDefectInspectionReport = null;
		File defectInspectionReportTemp = null;

		boolean onboardCraneInspectionReportBool = false;
		byte[] bytesOnboardCraneInspectionReport = null;
		File onboardCraneInspectionReportTemp = null;

		boolean performanceReportRepairElevatorBool = false;
		byte[] bytesPerformanceReportRepairElevator = null;
		File performanceReportRepairElevatorTemp = null;
		
		boolean statutoryInspectionReportBool = false;
		byte[] bytesStatutoryInspectionReport = null;
		File statutoryInspectionReportTemp = null;

		boolean measurementsMVSwitchgearStatorCabinetBool = false;
		byte[] bytesMeasurementsMVSwitchgearStatorCabinet = null;
		File measurementsMVSwitchgearStatorCabinetTemp = null;
		
		boolean examinationTransformerBool = false;
		byte[] bytesExaminationTransformer = null;
		File measurementsExaminationTransformerTemp = null;
				
		boolean measurementsMV6KvBool = false;
		byte[] bytesMeasurementsMV6Kv = null;
		File measurementsMV6KvTemp = null;
		
		boolean measurements690V400VBool = false;
		byte[] bytesMeasurements690V400V = null;
		File measurements690V400VTemp = null;
		
		byte[] bytes = null;

		Project project = projectService.getProject(projectId);

		List<Turbine> listTurbines = turbineService.getTurbinesByProject(project);

		for (Turbine turbine : listTurbines) {

			if (turbine.isDefectsInspectionReport())
				defectInspectionReportBool = true;

			if (turbine.isOnboardCraneInspectionReport())
				onboardCraneInspectionReportBool = true;

			if (turbine.isPerformanceReportRepairElevator())
				performanceReportRepairElevatorBool = true;
			
			if (turbine.isStatutoryInspectionReport())
				statutoryInspectionReportBool = true;

			if (turbine.isMeasurementsMwSwitchgear())
				measurementsMVSwitchgearStatorCabinetBool = true;
			
			if (turbine.isExaminationTransformer())
				examinationTransformerBool = true;
			
			if (turbine.isMeasurements6KV())
				measurementsMV6KvBool = true;
			
			if (turbine.isMeasurements690V400V())
				measurements690V400VBool = true;
		}
		Path tmpDirOrig = Files.createTempDirectory(null);
		if (defectInspectionReportBool) {
			bytesDefectInspectionReport = populateAndCopy("/defectInspectionReport/", "/Defect Inspection Report.pdf", project.getSite(), project.getNumber(), project.getType());
			defectInspectionReportTemp = new File(tmpDirOrig.toString(), "/Defect Inspection Report.pdf");
			OutputStream outStream = new FileOutputStream(defectInspectionReportTemp);
			outStream.write(bytesDefectInspectionReport);
		}

		if (onboardCraneInspectionReportBool) {
			String folder = "/onboardCraneInspectionReport/";
			String filenameOnboardCraneInspectionReport = "Onboard crane Inspection Report.pdf";
			bytesOnboardCraneInspectionReport = populateAndCopy(folder, filenameOnboardCraneInspectionReport, project.getSite(), project.getNumber(), project.getType());
			onboardCraneInspectionReportTemp = new File(tmpDirOrig.toString(), filenameOnboardCraneInspectionReport);
			OutputStream outStream = new FileOutputStream(onboardCraneInspectionReportTemp);
			outStream.write(bytesOnboardCraneInspectionReport);
		}

		if (performanceReportRepairElevatorBool) {
			String folder = "/performanceReportRepairElevator/";
			String filenamePerformanceReportRepairElevator = "Performance Report Repair Elevator.pdf";
			bytesPerformanceReportRepairElevator = populateAndCopy(folder, filenamePerformanceReportRepairElevator, project.getSite(), project.getNumber(), project.getType());
			performanceReportRepairElevatorTemp = new File(tmpDirOrig.toString(), filenamePerformanceReportRepairElevator);
			OutputStream outStream = new FileOutputStream(performanceReportRepairElevatorTemp);
			outStream.write(bytesPerformanceReportRepairElevator);
		}
		
		
		if (measurementsMVSwitchgearStatorCabinetBool) {
			String folder = "/measurementsMVSwitchgearStatorCabinet/";
			String filenameMeasurementsMVSwitchgearStatorCabinet = "Measurements of MV Switchgear and Stator Cabinet.pdf";
			bytesMeasurementsMVSwitchgearStatorCabinet = populateAndCopy(folder, filenameMeasurementsMVSwitchgearStatorCabinet, project.getSite(), project.getNumber(), project.getType());
			measurementsMVSwitchgearStatorCabinetTemp = new File(tmpDirOrig.toString(), filenameMeasurementsMVSwitchgearStatorCabinet);
			OutputStream outStream = new FileOutputStream(measurementsMVSwitchgearStatorCabinetTemp);
			outStream.write(bytesMeasurementsMVSwitchgearStatorCabinet);
		}
		
		
		if (examinationTransformerBool) {
			String folder = "/examinationTransformer/";
			String filenameExaminationTransformer = "Examination Transformer.pdf";
			bytesExaminationTransformer = populateAndCopy(folder, filenameExaminationTransformer, project.getSite(), project.getNumber(), project.getType());
			measurementsExaminationTransformerTemp = new File(tmpDirOrig.toString(), filenameExaminationTransformer);
			OutputStream outStream = new FileOutputStream(measurementsExaminationTransformerTemp);
			outStream.write(bytesExaminationTransformer);
		}
		
		if (measurementsMV6KvBool) {
			String folder = "/medidas6Kv/";
			String filenameMeasurementsMV6Kv = "Medidas 6Kv.pdf";
			bytesMeasurementsMV6Kv = populateAndCopy(folder, filenameMeasurementsMV6Kv, project.getSite(), project.getNumber(), project.getType());
			measurementsMV6KvTemp = new File(tmpDirOrig.toString(), filenameMeasurementsMV6Kv);
			OutputStream outStream = new FileOutputStream(measurementsMV6KvTemp);
			outStream.write(bytesMeasurementsMV6Kv);
		}
				
		if (measurements690V400VBool) {
			String folder = "/medidas690V400V/";
			String filenameMeasurements690V400V = "Medidas 690V400V.pdf";
			bytesMeasurements690V400V = populateAndCopy(folder, filenameMeasurements690V400V, project.getSite(), project.getNumber(), project.getType());
			measurements690V400VTemp = new File(tmpDirOrig.toString(), filenameMeasurements690V400V);
			OutputStream outStream = new FileOutputStream(measurements690V400VTemp);
			outStream.write(bytesMeasurements690V400V);
		}

		if (statutoryInspectionReportBool) {
			String folder = "/statutoryInspectionReport/";
			String filenameStatutoryInspectionReport = "Statutory Inspection Report.pdf";
			bytesStatutoryInspectionReport = populateAndCopy(folder, filenameStatutoryInspectionReport, project.getSite(), project.getNumber(), project.getType());
			statutoryInspectionReportTemp = new File(tmpDirOrig.toString(), filenameStatutoryInspectionReport);
			OutputStream outStream = new FileOutputStream(statutoryInspectionReportTemp);
			outStream.write(bytesStatutoryInspectionReport);
		}
		return FileUtils.readFileToByteArray(new File(ZipUtils.ZipDirectory(tmpDirOrig.toString(), project.getName())));
	}
	
	private byte[] populateAndCopy(String folder, String filename, String site, String number, String type) throws IOException {
		
		PDDocument pdfDocument = null;
		
		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile( folder, filename);

		pdfDocument = PDDocument.load(inputStream);

		pdfDocument.getNumberOfPages();

		setField(pdfDocument, "site", site);
		setField(pdfDocument, "wtgNumber", number);
		setField(pdfDocument, "wtgType", type);

		byte[] data = null;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		pdfDocument.save(byteArrayOutputStream);
		pdfDocument.close();
		InputStream inputStream_ = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

		data = IOUtils.toByteArray(inputStream_);

		return data;
	}
	
	public void setField(PDDocument pdfDocument, String name, String value) throws IOException {
		PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
		PDAcroForm acroForm = docCatalog.getAcroForm();
		PDField field = acroForm.getField(name);
		if (field != null) {
			field.setValue(value);
		} else {
			System.err.println("No field found with name:" + name);
		}
	}
}
