package com.gsclimbing.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.ExaminationTransformer;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.ExaminationTransformerService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.reports.populater.DefectsInspectionPopulater;
import com.gsclimbing.reports.populater.ExaminationTransformerPopulater;
import com.gsclimbing.zip.ZipUtils;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })

@RestController
@RequestMapping(path = "/api/files")
public class FileController {
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
			fileName = "Defect Inspection Report.pdf";
			DefectsInspectionReport defectsInspectionReport = defectsInspectionReportService.readDefectsInspectionReport(id);
			return defectsInspectionPopulater.generatePDF(defectsInspectionReport);
		case ET:
			fileName = "Examination Transformer.pdf";
			ExaminationTransformer examinationTransformer = examinationTransformerService.readExaminationTransformer(id);
			return examinationTransformerPopulater.generatePDF(examinationTransformer);
		}
		return null;
	}
	
	@RequestMapping("/download_zip/{projectId}")
	public ResponseEntity<byte[]> getZipFile(@PathVariable int projectId) throws IOException {

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
			String folder = "/defectInspectionReport/";
			String filenameDefectInspectionReport = "/Defect Inspection Report.pdf";
			
			bytesDefectInspectionReport = populateAndCopy(folder, filenameDefectInspectionReport, project.getSite(), project.getNumber(), project.getType());
			//bytesDefectInspectionReport = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameDefectInspectionReport, "10", "defectInspectionReport/");
			defectInspectionReportTemp = new File(tmpDirOrig.toString(), filenameDefectInspectionReport);
			OutputStream outStream = new FileOutputStream(defectInspectionReportTemp);
			outStream.write(bytesDefectInspectionReport);
		}

		if (onboardCraneInspectionReportBool) {
			String folder = "/onboardCraneInspectionReport/";
			String filenameOnboardCraneInspectionReport = "Onboard crane Inspection Report.pdf";
			bytesOnboardCraneInspectionReport = populateAndCopy(folder, filenameOnboardCraneInspectionReport, project.getSite(), project.getNumber(), project.getType());
			//bytesOnboardCraneInspectionReport = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameOnboardCraneInspectionReport, "4", "onboardCraneInspectionReport/");
			onboardCraneInspectionReportTemp = new File(tmpDirOrig.toString(), filenameOnboardCraneInspectionReport);
			OutputStream outStream = new FileOutputStream(onboardCraneInspectionReportTemp);
			outStream.write(bytesOnboardCraneInspectionReport);
		}

		if (performanceReportRepairElevatorBool) {
			String folder = "/performanceReportRepairElevator/";
			String filenamePerformanceReportRepairElevator = "Performance Report Repair Elevator.pdf";
			bytesPerformanceReportRepairElevator = populateAndCopy(folder, filenamePerformanceReportRepairElevator, project.getSite(), project.getNumber(), project.getType());
			//bytesPerformanceReportRepairElevator = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenamePerformanceReportRepairElevator, "4", "performanceReportRepairElevator/");
			performanceReportRepairElevatorTemp = new File(tmpDirOrig.toString(), filenamePerformanceReportRepairElevator);
			OutputStream outStream = new FileOutputStream(performanceReportRepairElevatorTemp);
			outStream.write(bytesPerformanceReportRepairElevator);
		}
		
		if (statutoryInspectionReportBool) {
			String folder = "/statutoryInspectionReport/";
			String filenameStatutoryInspectionReport = "Statutory Inspection Report.pdf";
			bytesStatutoryInspectionReport = populateAndCopy(folder, filenameStatutoryInspectionReport, project.getSite(), project.getNumber(), project.getType());
			//bytesStatutoryInspectionReport = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameStatutoryInspectionReport, "8", "statutoryInspectionReport/");
			statutoryInspectionReportTemp = new File(tmpDirOrig.toString(), filenameStatutoryInspectionReport);
			OutputStream outStream = new FileOutputStream(statutoryInspectionReportTemp);
			outStream.write(bytesStatutoryInspectionReport);
		}
		
		if (measurementsMVSwitchgearStatorCabinetBool) {
			String folder = "/measurementsMVSwitchgearStatorCabinet/";
			String filenameMeasurementsMVSwitchgearStatorCabinet = "Measurements of MV Switchgear and Stator Cabinet.pdf";
			bytesMeasurementsMVSwitchgearStatorCabinet = populateAndCopy(folder, filenameMeasurementsMVSwitchgearStatorCabinet, project.getSite(), project.getNumber(), project.getType());
			//bytesMeasurementsMVSwitchgearStatorCabinet = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameMeasurementsMVSwitchgearStatorCabinet, "0", "measurementsMVSwitchgearStatorCabinet/");
			measurementsMVSwitchgearStatorCabinetTemp = new File(tmpDirOrig.toString(), filenameMeasurementsMVSwitchgearStatorCabinet);
			OutputStream outStream = new FileOutputStream(measurementsMVSwitchgearStatorCabinetTemp);
			outStream.write(bytesMeasurementsMVSwitchgearStatorCabinet);
		}
		
		
		if (examinationTransformerBool) {
			String folder = "/ExaminationTransformer/";
			String filenameExaminationTransformer = "Examination Transformer.pdf";
			bytesExaminationTransformer = populateAndCopy(folder, filenameExaminationTransformer, project.getSite(), project.getNumber(), project.getType());
			//bytesExaminationTransformer = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameExaminationTransformer, "0", "ExaminationTransformer/");
			measurementsExaminationTransformerTemp = new File(tmpDirOrig.toString(), filenameExaminationTransformer);
			OutputStream outStream = new FileOutputStream(measurementsExaminationTransformerTemp);
			outStream.write(bytesExaminationTransformer);
		}
		
		if (measurementsMV6KvBool) {
			String folder = "/medidas6Kv/";
			String filenameMeasurementsMV6Kv = "Medidas 6Kv.pdf";
			bytesMeasurementsMV6Kv = populateAndCopy(folder, filenameMeasurementsMV6Kv, project.getSite(), project.getNumber(), project.getType());
			//bytesMeasurementsMV6Kv = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameMeasurementsMV6Kv, "0", "medidas6Kv/");
			measurementsMV6KvTemp = new File(tmpDirOrig.toString(), filenameMeasurementsMV6Kv);
			OutputStream outStream = new FileOutputStream(measurementsMV6KvTemp);
			outStream.write(bytesMeasurementsMV6Kv);
		}
				
		if (measurements690V400VBool) {
			String folder = "/medidas690V400V/";
			String filenameMeasurements690V400V = "Medidas 690V400V.pdf";
			bytesMeasurements690V400V = populateAndCopy(folder, filenameMeasurements690V400V, project.getSite(), project.getNumber(), project.getType());
			//bytesMeasurements690V400V = FTPDownloadFiles.downloadPdfReportFromFTPServer(filenameMeasurements690V400V, "0", "medidas690V400V/");
			measurements690V400VTemp = new File(tmpDirOrig.toString(), filenameMeasurements690V400V);
			OutputStream outStream = new FileOutputStream(measurements690V400VTemp);
			outStream.write(bytesMeasurements690V400V);
		}

		String filename = project.getName() + ".zip";

		String pathZipFile = ZipUtils.ZipDirectory(tmpDirOrig.toString(), project.getName());

		byte[] data = FileUtils.readFileToByteArray(new File(pathZipFile));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(data);
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
