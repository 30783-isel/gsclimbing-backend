package com.gsclimbing.reports.populater;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDevice;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class StatutoryInspectionReportElevatorPopulater {

	@Autowired
	private UserRepository userService;

	@Autowired
	private FileService fileService;

	private PDDocument _pdfDocument;

	public byte[] generatePDF(Report report) {
		byte[] bytes = null;
		try {
			bytes = populateAndCopy((StatutoryInspectionReport)report);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private byte[] populateAndCopy(StatutoryInspectionReport report) throws IOException {

		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/statutoryInspectionReport/", "Statutory Inspection Report.pdf");

		_pdfDocument = PDDocument.load(inputStream);

		_pdfDocument.getNumberOfPages();

		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("wtgType", report.getWtgType());

		setField("inspectors", report.getInspectors());
		setField("date", report.getDate());
		setField("repairRequired", report.getRepairRequired());
		setField("nextInspection", report.getNextInspection());

		setField("site", report.getSite());

		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("wtgType", report.getWtgType());

		setField("reportNumber", report.getReportNumber());
		setField("client", report.getClient());
		setField("clientContact", report.getClientContact());
		setField("windPark", report.getWindPark());
		setField("siteAddress", report.getSiteAddress());

		setField("wtgType", report.getWtgType());
		setField("wtgNumber", report.getWtgNumber());
		setField("site", report.getSite());
		setField("yearOfConstruction", report.getYearConstruction());
		
		setField("serviceCabinManufacturer", report.getServiceCabinManufacturer());
		setField("serviceCabinType", report.getServiceCabinType());
		setField("serviceCabinSerialNumber", report.getServiceCabinSerialNumber());
		setField("serviceCabinInspectionPassedWithoutDefects", report.getServiceCabinInspectionPassedWithoutDefects());
		setField("serviceCabinInspectionPassedWithSmallDefects", report.getServiceCabinInspectionPassedWithSmallDefects());
		setField("serviceCabinInspectionNotPassed", report.getServiceCabinInspectionNotPassed());

		setField("ladderTowerManufacturer", report.getLadderTowerManufacturer());
		setField("ladderTowerType", report.getLadderTowerType());
		setField("ladderTowerSerialNumber", report.getLadderTowerSerialNumber());
		setField("ladderTowerInspectionPassedWithoutDefects", report.getLadderTowerInspectionPassedWithoutDefects());
		setField("ladderTowerInspectionPassedWithSmallDefects", report.getLadderTowerInspectionPassedWithSmallDefects());
		setField("ladderTowerInspectionNotPassed", report.getLadderTowerInspectionNotPassed());

		setField("failArrestSystemTowerManufacturer", report.getFailArrestSystemTowerManufacturer());
		setField("failArrestSystemTowerType", report.getFailArrestSystemTowerType());
		setField("failArrestSystemTowerSerialNumber", report.getFailArrestSystemTowerSerialNumber());
		setField("failArrestSystemToweInspectionPassedWithoutDefects", report.getFailArrestSystemTowerInspectionPassedWithoutDefects());
		setField("failArrestSystemTowerInspectionPassedWithSmallDefects", report.getFailArrestSystemTowerInspectionPassedWithSmallDefects());
		setField("failArrestSystemTowerInspectionNotPassed", report.getFailArrestSystemTowerInspectionNotPassed());

		setField("internalCraneManufacturer", report.getInternalCraneManufacturer());
		setField("internalCraneType", report.getInternalCraneType());
		setField("internalCraneSerialNumber", report.getInternalCraneSerialNumber());
		setField("internalCraneInspectionPassedWithoutDefects", report.getInternalCraneInspectionPassedWithoutDefects());
		setField("internalCraneInspectionPassedWithSmallDefects", report.getInternalCraneInspectionPassedWithSmallDefects());
		setField("internalCraneInspectionNotPassed", report.getInternalCraneInspectionNotPassed());

		setField("descenderDeviceManufacturer", report.getDescenderDeviceManufacturer());
		setField("descenderDeviceType", report.getDescenderDeviceType());
		setField("descenderDeviceSerialNumber", report.getDescenderDeviceSerialNumber());
		setField("descenderDeviceInspectionPassedWithoutDefects", report.getDescenderDeviceInspectionPassedWithoutDefects());
		setField("descenderDeviceInspectionPassedWithSmallDefects", report.getDescenderDeviceInspectionPassedWithSmallDefects());
		setField("descenderDeviceInspectionNotPassed", report.getDescenderDeviceInspectionNotPassed());

		setField("anchorPointsManufacturer", report.getAnchorPointsManufacturer());
		setField("anchorPointsType", report.getAnchorPointsType());
		setField("anchorPointsSerialNumber", report.getAnchorPointsSerialNumber());
		setField("anchorPointsInspectionPassedWithoutDefects", report.getAnchorPointsInspectionPassedWithoutDefects());
		setField("anchorPointsInspectionPassedWithSmallDefects", report.getAnchorPointsInspectionPassedWithSmallDefects());
		setField("anchorPointsInspectionNotPassed", report.getAnchorPointsInspectionNotPassed());

		setField("inspectors", report.getInspectors());
		setField("date", report.getDate());
		setField("resultOfInspection", report.getResultOfInspection());
		setField("repairRequired", report.getRepairRequired());
		setField("nextInspection", report.getNextInspection());

		setField("siteDate", report.getSiteDate());
		setField("responsibleTechnician", report.getResponsibleTechnician());

		// 2.1 Inspection report service cabin
		// ------------------------------------------------------------

		StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin = report.getStatutoryInspectionReportServiceCabin();

		setField("inspectionReportServiceCabinManufacturer", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinManufacturer());
		setField("inspectionReportServiceCabinType", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinType());
		setField("inspectionReportServiceCabinSerialNumber", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinSerialNumber());
		setField("inspectionReportServiceCabinSerialNumberHoist", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinSerialNumberHoist());
		setField("inspectionReportServiceCabinYearBuild", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinYearBuild());
		setField("inspectionReportServiceCabin5yearInspectionRequired", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin5yearInspectionRequired());
		setField("inspectionReportServiceCabinHourMeterReading", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinHourMeterReading());

		setField("inspectionReportServiceCabinInspectors", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinInspectors());
		setField("inspectionReportServiceCabinDate", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinDate());
		setField("inspectionReportServiceCabinResultOfInspection", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinResultOfInspection());
		setField("inspectionReportServiceCabinRepairRequired", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinRepairRequired());
		setField("inspectionReportServiceCabinNextInspection", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinNextInspection());
		
		setField("inspectionReportServiceCabin1Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin1Txt());
		setField("inspectionReportServiceCabin2Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin2Txt());
		setField("inspectionReportServiceCabin3Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin3Txt());
		setField("inspectionReportServiceCabin4Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin4Txt());
		setField("inspectionReportServiceCabin5Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin5Txt());
		setField("inspectionReportServiceCabin6Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin6Txt());
		setField("inspectionReportServiceCabin7Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin7Txt());
		setField("inspectionReportServiceCabin8Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin8Txt());
		setField("inspectionReportServiceCabin9Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin9Txt());
		setField("inspectionReportServiceCabin10Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin10Txt());
		setField("inspectionReportServiceCabin11Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin11Txt());
		setField("inspectionReportServiceCabin12Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin12Txt());
		setField("inspectionReportServiceCabin13Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin13Txt());
		setField("inspectionReportServiceCabin14Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin14Txt());
		setField("inspectionReportServiceCabin15Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin15Txt());
		setField("inspectionReportServiceCabin16Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin16Txt());
		setField("inspectionReportServiceCabin17Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin17Txt());
		setField("inspectionReportServiceCabin18Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin18Txt());
		setField("inspectionReportServiceCabin19Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin19Txt());
		setField("inspectionReportServiceCabin20Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin20Txt());
		setField("inspectionReportServiceCabin21Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin21Txt());
		setField("inspectionReportServiceCabin22Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin22Txt());
		setField("inspectionReportServiceCabin23Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin23Txt());
		setField("inspectionReportServiceCabin24Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin24Txt());
		setField("inspectionReportServiceCabin25Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin25Txt());
		setField("inspectionReportServiceCabin26Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin26Txt());
		setField("inspectionReportServiceCabin27Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin27Txt());
		setField("inspectionReportServiceCabin28Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin28Txt());
		setField("inspectionReportServiceCabin29Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin29Txt());
		setField("inspectionReportServiceCabin30Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin30Txt());
		setField("inspectionReportServiceCabin31Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin31Txt());
		setField("inspectionReportServiceCabin32Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin32Txt());
		setField("inspectionReportServiceCabin33Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin33Txt());
		setField("inspectionReportServiceCabin34Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin34Txt());
		setField("inspectionReportServiceCabin35Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin35Txt());
		setField("inspectionReportServiceCabin36Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin36Txt());
		setField("inspectionReportServiceCabin37Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin37Txt());
		setField("inspectionReportServiceCabin38Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin38Txt());
		setField("inspectionReportServiceCabin39Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin39Txt());
		setField("inspectionReportServiceCabin40Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin40Txt());
		setField("inspectionReportServiceCabin41Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin41Txt());
		setField("inspectionReportServiceCabin42Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin42Txt());
		setField("inspectionReportServiceCabin43Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin43Txt());
		setField("inspectionReportServiceCabin44Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin44Txt());
		setField("inspectionReportServiceCabin45Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin45Txt());
		setField("inspectionReportServiceCabin46Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin46Txt());
		setField("inspectionReportServiceCabin47Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin47Txt());
		setField("inspectionReportServiceCabin48Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin48Txt());
		setField("inspectionReportServiceCabin49Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin49Txt());
		setField("inspectionReportServiceCabin50Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin50Txt());
		setField("inspectionReportServiceCabin51Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin51Txt());
		setField("inspectionReportServiceCabin52Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin52Txt());
		setField("inspectionReportServiceCabin53Txt", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabin53Txt());
		setField("inspectionReportServiceCabinNotes", statutoryInspectionReportServiceCabin.getInspectionReportServiceCabinNotes());

		// 2.2. Inspection report internal crane
		// ----------------------------------------------------------------------------------------------

		StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane = report.getStatutoryInspectionReportInternalCrane();

		setField("internalCraneManufacturer", statutoryInspectionReportInternalCrane.getInternalCraneManufacturer());
		setField("internalCraneType", statutoryInspectionReportInternalCrane.getInternalCraneType());
		setField("internalCraneYearBuild", statutoryInspectionReportInternalCrane.getInternalCraneYearBuild());
		setField("internalCraneSerialNumber", statutoryInspectionReportInternalCrane.getInternalCraneSerialNumber());

		setField("internalCraneTypePlateTestBadge", statutoryInspectionReportInternalCrane.getInternalCraneTypePlateTestBadge());

		setField("internalCraneInspectors", statutoryInspectionReportInternalCrane.getInternalCraneInspectors());
		setField("internalCraneDate", statutoryInspectionReportInternalCrane.getInternalCraneDate());
		setField("internalCraneResultInspection", statutoryInspectionReportInternalCrane.getInternalCraneResultInspection());
		setField("internalCraneRepairRequired", statutoryInspectionReportInternalCrane.getInternalCraneRepairRequired());
		setField("internalCraneNextInspection", statutoryInspectionReportInternalCrane.getInternalCraneNextInspection());
		setField("internalCrane1Txt", statutoryInspectionReportInternalCrane.getInternalCrane1Txt());
		setField("internalCrane2Txt", statutoryInspectionReportInternalCrane.getInternalCrane2Txt());
		setField("internalCrane3Txt", statutoryInspectionReportInternalCrane.getInternalCrane3Txt());
		setField("internalCrane4Txt", statutoryInspectionReportInternalCrane.getInternalCrane4Txt());
		setField("internalCrane5Txt", statutoryInspectionReportInternalCrane.getInternalCrane5Txt());
		setField("internalCrane6Txt", statutoryInspectionReportInternalCrane.getInternalCrane6Txt());
		setField("internalCrane7Txt", statutoryInspectionReportInternalCrane.getInternalCrane7Txt());
		setField("internalCrane8Txt", statutoryInspectionReportInternalCrane.getInternalCrane8Txt());
		setField("internalCrane9Txt", statutoryInspectionReportInternalCrane.getInternalCrane9Txt());
		setField("internalCrane10Txt", statutoryInspectionReportInternalCrane.getInternalCrane10Txt());
		setField("internalCrane11Txt", statutoryInspectionReportInternalCrane.getInternalCrane11Txt());
		setField("internalCrane12Txt", statutoryInspectionReportInternalCrane.getInternalCrane12Txt());
		setField("internalCrane13Txt", statutoryInspectionReportInternalCrane.getInternalCrane13Txt());
		setField("internalCrane14Txt", statutoryInspectionReportInternalCrane.getInternalCrane14Txt());
		setField("internalCrane15Txt", statutoryInspectionReportInternalCrane.getInternalCrane15Txt());
		setField("internalCrane16Txt", statutoryInspectionReportInternalCrane.getInternalCrane16Txt());
		setField("internalCrane17Txt", statutoryInspectionReportInternalCrane.getInternalCrane17Txt());
		setField("internalCrane18Txt", statutoryInspectionReportInternalCrane.getInternalCrane18Txt());
		setField("internalCrane19Txt", statutoryInspectionReportInternalCrane.getInternalCrane19Txt());
		setField("internalCrane20Txt", statutoryInspectionReportInternalCrane.getInternalCrane20Txt());
		setField("internalCrane21Txt", statutoryInspectionReportInternalCrane.getInternalCrane21Txt());
		setField("internalCrane22Txt", statutoryInspectionReportInternalCrane.getInternalCrane22Txt());
		setField("internalCrane23Txt", statutoryInspectionReportInternalCrane.getInternalCrane23Txt());
		setField("internalCrane24Txt", statutoryInspectionReportInternalCrane.getInternalCrane24Txt());
		setField("internalCraneNotes", statutoryInspectionReportInternalCrane.getInternalCraneNotes());

		// 2.3 private String Inspection report ladder
		// ---------------------------------------------------------------);

		StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder = report.getStatutoryInspectionReportInspectionReportLadder();
		setField("inspectionReportLadderManufacturer", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderManufacturer());
		setField("inspectionReportLadderType", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderType());
		setField("inspectionReportLadderSerialNumber", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderSerialNumber());
		setField("inspectionReportLadderManufacturerFallArrestSystem", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderManufacturerFallArrestSystem());
		setField("inspectionReportLadderType2", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderType2());
		setField("inspectionReportLadderTypePlateTestBadge", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderTypePlateTestBadge());

		setField("inspectionReportLadderSerialNumber2", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderSerialNumber2());
		
		setField("inspectionReportLadderInspectors", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderInspectors());
		setField("inspectionReportLadderDate", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderDate());
		setField("inspectionReportLadderResultInspection", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderResultInspection());
		setField("inspectionReportLadderRepairRequired", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderRepairRequired());
		setField("inspectionReportLadderNextInspection", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderNextInspection());

		setField("inspectionReportLadder1Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder1Txt());
		setField("inspectionReportLadder2Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder2Txt());
		setField("inspectionReportLadder3Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder3Txt());
		setField("inspectionReportLadder4Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder4Txt());
		setField("inspectionReportLadder5Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder5Txt());
		setField("inspectionReportLadder6Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder6Txt());
		setField("inspectionReportLadder7Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder7Txt());
		setField("inspectionReportLadder8Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder8Txt());
		setField("inspectionReportLadder9Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder9Txt());
		setField("inspectionReportLadder10Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder10Txt());
		setField("inspectionReportLadder11Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder11Txt());
		setField("inspectionReportLadder12Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder12Txt());
		setField("inspectionReportLadder13Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder13Txt());
		setField("inspectionReportLadder14Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder14Txt());
		setField("inspectionReportLadder15Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder15Txt());
		setField("inspectionReportLadder16Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder16Txt());
		setField("inspectionReportLadder17Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder17Txt());
		setField("inspectionReportLadder18Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder18Txt());
		setField("inspectionReportLadder19Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder19Txt());
		setField("inspectionReportLadder20Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder20Txt());
		setField("inspectionReportLadder21Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder21Txt());
		setField("inspectionReportLadder22Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder22Txt());
		setField("inspectionReportLadder23Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder23Txt());
		setField("inspectionReportLadder24Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder24Txt());
		setField("inspectionReportLadder25Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder25Txt());
		setField("inspectionReportLadder26Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder26Txt());
		setField("inspectionReportLadder27Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder27Txt());
		setField("inspectionReportLadder28Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder28Txt());
		setField("inspectionReportLadder29Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder29Txt());
		setField("inspectionReportLadder30Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder30Txt());
		setField("inspectionReportLadder31Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder31Txt());
		setField("inspectionReportLadder32Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder32Txt());
		setField("inspectionReportLadder33Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder33Txt());
		setField("inspectionReportLadder34Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder34Txt());
		setField("inspectionReportLadder35Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder35Txt());
		setField("inspectionReportLadder36Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder36Txt());
		setField("inspectionReportLadder37Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder37Txt());
		setField("inspectionReportLadder38Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder38Txt());
		setField("inspectionReportLadder39Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder39Txt());
		setField("inspectionReportLadder40Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder40Txt());
		setField("inspectionReportLadder41Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder41Txt());
		setField("inspectionReportLadder42Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder42Txt());
		setField("inspectionReportLadder43Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder43Txt());
		setField("inspectionReportLadder44Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder44Txt());
		setField("inspectionReportLadder45Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder45Txt());
		setField("inspectionReportLadder46Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder46Txt());
		setField("inspectionReportLadder47Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder47Txt());
		setField("inspectionReportLadder48Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder48Txt());
		setField("inspectionReportLadder49Txt", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadder49Txt());

		setField("inspectionReportLadderNotes", statutoryInspectionReportInspectionReportLadder.getInspectionReportLadderNotes());

		// 2.4 Inspection Anchor Points
		// -----------------------------------------------------------

		StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints = report.getStatutoryInspectionReportInspectionAnchorPoints();

		setField("inspectionAnchorPointsManufacturer", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsManufacturer());
		setField("inspectionAnchorPointsType", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsType());
		setField("inspectionAnchorPointsTypePlateTestBadge", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsTypePlateTestBadge());

		setField("inspectionAnchorPointsInspectors", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsInspectors());
		setField("inspectionAnchorPointsDate", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsDate());
		setField("inspectionAnchorPointsResultOfInspection", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsResultOfInspection());
		setField("inspectionAnchorPointsRepairRequired", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsRepairRequired());
		setField("inspectionAnchorPointsNextInspection", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsNextInspection());

		setField("inspectionAnchorPoints1Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints1Txt());
		setField("inspectionAnchorPoints2Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints2Txt());
		setField("inspectionAnchorPoints3Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints3Txt());
		setField("inspectionAnchorPoints4Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints4Txt());
		setField("inspectionAnchorPoints5Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints5Txt());
		setField("inspectionAnchorPoints6Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints6Txt());
		setField("inspectionAnchorPoints7Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints7Txt());
		setField("inspectionAnchorPoints8Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints8Txt());
		setField("inspectionAnchorPoints9Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints9Txt());
		setField("inspectionAnchorPoints10Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints10Txt());
		setField("inspectionAnchorPoints11Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints11Txt());
		setField("inspectionAnchorPoints12Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints12Txt());
		setField("inspectionAnchorPoints13Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints13Txt());
		setField("inspectionAnchorPoints14Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints14Txt());
		setField("inspectionAnchorPoints15Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints15Txt());
		setField("inspectionAnchorPoints16Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints16Txt());
		setField("inspectionAnchorPoints17Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints17Txt());
		setField("inspectionAnchorPoints18Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints18Txt());
		setField("inspectionAnchorPoints19Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints19Txt());
		setField("inspectionAnchorPoints20Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints20Txt());
		setField("inspectionAnchorPoints21Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints21Txt());
		setField("inspectionAnchorPoints22Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints22Txt());
		setField("inspectionAnchorPoints23Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints23Txt());
		setField("inspectionAnchorPoints24Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints24Txt());
		setField("inspectionAnchorPoints25Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints25Txt());
		setField("inspectionAnchorPoints26Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints26Txt());
		setField("inspectionAnchorPoints27Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints27Txt());
		setField("inspectionAnchorPoints28Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints28Txt());
		setField("inspectionAnchorPoints29Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints29Txt());
		setField("inspectionAnchorPoints30Txt", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPoints30Txt());

		setField("inspectionAnchorPointsNotes", statutoryInspectionReportInspectionAnchorPoints.getInspectionAnchorPointsNotes());

		// 2.5 Inspection descender device
		// ------------------------------------------",
		// report.getInspectionReportServiceCabin1Txt());

		StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice = report.getStatutoryInspectionReportInspectionDescenderDevice();

		setField("inspectionDescenderDeviceManufacturer", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceManufacturer());
		setField("inspectionDescenderDeviceType", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceType());
		setField("inspectionDescenderDeviceYearBuild", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceYearBuild());
		setField("inspectionDescenderDeviceSerialNumber", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceSerialNumber());
		setField("inspectionDescenderDeviceTypePlateTestBadge", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceTypePlateTestBadge());

		setField("inspectionDescenderDeviceInspectors", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceInspectors());
		setField("inspectionDescenderDeviceDate", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceDate());
		setField("inspectionDescenderDeviceResultOfInspection", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceResultOfInspection());
		setField("inspectionDescenderDeviceRepairRequired", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceRepairRequired());
		setField("inspectionDescenderDeviceNextInspection", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceNextInspection());

		setField("inspectionDescenderDevice1Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice1Txt());
		setField("inspectionDescenderDevice2Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice2Txt());
		setField("inspectionDescenderDevice3Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice3Txt());
		setField("inspectionDescenderDevice4Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice4Txt());
		setField("inspectionDescenderDevice5Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice5Txt());
		setField("inspectionDescenderDevice6Txt", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDevice6Txt());
		setField("inspectionDescenderDeviceNotes", statutoryInspectionReportInspectionDescenderDevice.getInspectionDescenderDeviceNotes());

		setField("inspectionReportServiceCabin1Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin1Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin2Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin2Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin3Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin3Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin4Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin4Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin5Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin5Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin6Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin6Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin7Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin7Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin8Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin8Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin9Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin9Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin10Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin10Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin11Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin11Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin12Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin12Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin13Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin13Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin14Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin14Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin15Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin15Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin16Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin16Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin17Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin17Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin18Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin18Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin19Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin19Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin20Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin20Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin21Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin21Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin22Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin22Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin23Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin23Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin24Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin24Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin25Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin25Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin26Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin26Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin27Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin27Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin28Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin28Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin29Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin29Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin30Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin30Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin31Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin31Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin32Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin32Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin33Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin33Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin34Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin34Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin35Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin35Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin36Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin36Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin37Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin37Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin38Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin38Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin39Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin39Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin40Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin40Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin41Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin41Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin42Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin42Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin43Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin43Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin44Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin44Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin45Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin45Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin46Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin46Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin47Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin47Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin48Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin48Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin49Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin49Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin50Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin50Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin51Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin51Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin52Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin52Chk() ? "Yes" : "Off");
		setField("inspectionReportServiceCabin53Chk", statutoryInspectionReportServiceCabin.isInspectionReportServiceCabin53Chk() ? "Yes" : "Off");

		// 2.2 Internal Crane
		// -----------------------------------------------------------;

		setField("internalCrane1Chk", statutoryInspectionReportInternalCrane.isInternalCrane1Chk() ? "Yes" : "Off");
		setField("internalCrane2Chk", statutoryInspectionReportInternalCrane.isInternalCrane2Chk() ? "Yes" : "Off");
		setField("internalCrane3Chk", statutoryInspectionReportInternalCrane.isInternalCrane3Chk() ? "Yes" : "Off");
		setField("internalCrane4Chk", statutoryInspectionReportInternalCrane.isInternalCrane4Chk() ? "Yes" : "Off");
		setField("internalCrane5Chk", statutoryInspectionReportInternalCrane.isInternalCrane5Chk() ? "Yes" : "Off");
		setField("internalCrane6Chk", statutoryInspectionReportInternalCrane.isInternalCrane6Chk() ? "Yes" : "Off");
		setField("internalCrane7Chk", statutoryInspectionReportInternalCrane.isInternalCrane7Chk() ? "Yes" : "Off");
		setField("internalCrane8Chk", statutoryInspectionReportInternalCrane.isInternalCrane8Chk() ? "Yes" : "Off");
		setField("internalCrane9Chk", statutoryInspectionReportInternalCrane.isInternalCrane9Chk() ? "Yes" : "Off");
		setField("internalCrane10Chk", statutoryInspectionReportInternalCrane.isInternalCrane10Chk() ? "Yes" : "Off");
		setField("internalCrane11Chk", statutoryInspectionReportInternalCrane.isInternalCrane11Chk() ? "Yes" : "Off");
		setField("internalCrane12Chk", statutoryInspectionReportInternalCrane.isInternalCrane12Chk() ? "Yes" : "Off");
		setField("internalCrane13Chk", statutoryInspectionReportInternalCrane.isInternalCrane13Chk() ? "Yes" : "Off");
		setField("internalCrane14Chk", statutoryInspectionReportInternalCrane.isInternalCrane14Chk() ? "Yes" : "Off");
		setField("internalCrane15Chk", statutoryInspectionReportInternalCrane.isInternalCrane15Chk() ? "Yes" : "Off");
		setField("internalCrane16Chk", statutoryInspectionReportInternalCrane.isInternalCrane16Chk() ? "Yes" : "Off");
		setField("internalCrane17Chk", statutoryInspectionReportInternalCrane.isInternalCrane17Chk() ? "Yes" : "Off");
		setField("internalCrane18Chk", statutoryInspectionReportInternalCrane.isInternalCrane18Chk() ? "Yes" : "Off");
		setField("internalCrane19Chk", statutoryInspectionReportInternalCrane.isInternalCrane19Chk() ? "Yes" : "Off");
		setField("internalCrane20Chk", statutoryInspectionReportInternalCrane.isInternalCrane20Chk() ? "Yes" : "Off");
		setField("internalCrane21Chk", statutoryInspectionReportInternalCrane.isInternalCrane21Chk() ? "Yes" : "Off");
		setField("internalCrane22Chk", statutoryInspectionReportInternalCrane.isInternalCrane22Chk() ? "Yes" : "Off");
		setField("internalCrane23Chk", statutoryInspectionReportInternalCrane.isInternalCrane23Chk() ? "Yes" : "Off");
		setField("internalCrane24Chk", statutoryInspectionReportInternalCrane.isInternalCrane24Chk() ? "Yes" : "Off");

		// 2.3 Inspection Report Ladder
		// -----------------------------------------------------------;

		setField("inspectionReportLadder1Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder1Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder2Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder2Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder3Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder3Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder4Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder4Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder5Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder5Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder6Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder6Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder7Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder7Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder8Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder8Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder9Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder9Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder10Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder10Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder11Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder11Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder12Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder12Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder13Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder13Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder14Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder14Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder15Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder15Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder16Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder16Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder17Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder17Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder18Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder18Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder19Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder19Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder20Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder20Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder21Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder21Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder22Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder22Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder23Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder23Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder24Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder24Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder25Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder25Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder26Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder26Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder27Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder27Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder28Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder28Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder29Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder29Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder30Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder30Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder31Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder31Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder32Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder32Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder33Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder33Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder34Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder34Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder35Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder35Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder36Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder36Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder37Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder37Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder38Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder38Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder39Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder39Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder40Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder40Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder41Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder41Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder42Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder42Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder43Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder43Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder44Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder44Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder45Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder45Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder46Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder46Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder47Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder47Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder48Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder48Chk() ? "Yes" : "Off");
		setField("inspectionReportLadder49Chk", statutoryInspectionReportInspectionReportLadder.isInspectionReportLadder49Chk() ? "Yes" : "Off");

		// 2.4 Inspection Anchor Points
		// -----------------------------------------------------------

		setField("inspectionAnchorPoints1Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints1Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints2Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints2Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints3Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints3Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints4Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints4Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints5Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints5Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints6Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints6Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints7Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints7Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints8Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints8Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints9Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints9Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints10Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints10Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints11Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints11Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints12Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints12Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints13Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints13Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints14Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints14Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints15Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints15Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints16Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints16Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints17Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints17Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints18Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints18Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints19Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints19Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints20Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints20Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints21Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints21Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints22Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints22Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints23Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints23Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints24Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints24Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints25Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints25Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints26Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints26Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints27Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints27Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints28Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints28Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints29Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints29Chk() ? "Yes" : "Off");
		setField("inspectionAnchorPoints30Chk", statutoryInspectionReportInspectionAnchorPoints.isInspectionAnchorPoints30Chk() ? "Yes" : "Off");

		// 2.5 Inspection descender device ------------------------------------------"

		setField("inspectionDescenderDevice1Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice1Chk() ? "Yes" : "Off");
		setField("inspectionDescenderDevice2Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice2Chk() ? "Yes" : "Off");
		setField("inspectionDescenderDevice3Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice3Chk() ? "Yes" : "Off");
		setField("inspectionDescenderDevice4Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice4Chk() ? "Yes" : "Off");
		setField("inspectionDescenderDevice5Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice5Chk() ? "Yes" : "Off");
		setField("inspectionDescenderDevice6Chk", statutoryInspectionReportInspectionDescenderDevice.isInspectionDescenderDevice6Chk() ? "Yes" : "Off");

		List<FileData> listFileData = null;
		byte[] bytes = null;

		if (report != null) {
			List<FileData> list = fileService.readFile(report.getUuid());
			listFileData = list.stream().filter(file -> file.getMimeType().equals("JPG")).collect(Collectors.toList());
		}

		for (FileData fileData : listFileData) {

			if (fileData != null)
				bytes = FTPDownloadFiles.downloadFile2FTPServer(fileData.getHash());

			InputStream is = new ByteArrayInputStream(bytes);
			BufferedImage bufferedImage = ImageIO.read(is);

			setImageField((String) fileData.getName(), bufferedImage);

			setField(fileData.getNameField(), fileData.getDescription());

		}

		byte[] data = null;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		_pdfDocument.save(byteArrayOutputStream);
		_pdfDocument.close();
		InputStream inputStream_ = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

		data = IOUtils.toByteArray(inputStream_);

		return data;
	}

	public void setField(String name, String value) throws IOException {
		PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
		PDAcroForm acroForm = docCatalog.getAcroForm();
		PDField field = acroForm.getField(name);
		if (field != null) {
			field.setValue(value);
		} else {
			System.err.println("No field found with name:" + name);
		}
	}

	public void setImageField(String name, BufferedImage bufferedImage) throws IOException {
		PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
		PDAcroForm acroForm = docCatalog.getAcroForm();
		PDField field = acroForm.getField(name);
		if (field != null) {
			if (field instanceof PDPushButton) {
				insrtImage((PDPushButton) field, bufferedImage);
			}
		} else {
			System.err.println("No field found with name:" + name);
		}
	}

	private void insrtImage(PDPushButton pdPushButton, BufferedImage bImage) throws IOException {

		List<PDAnnotationWidget> widgets = pdPushButton.getWidgets();
		if (widgets != null && widgets.size() > 0) {
			PDAnnotationWidget annotationWidget = widgets.get(0);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "jpg", baos);
			byte[] bytes = baos.toByteArray();

			PDImageXObject pdImageXObject = PDImageXObject.createFromByteArray(_pdfDocument, bytes, "Statutory Inspection Report");
			float imageScaleRatio = (float) pdImageXObject.getHeight() / (float) pdImageXObject.getWidth();

			PDRectangle buttonPosition = getFieldArea(pdPushButton);
			float height = buttonPosition.getHeight();
			float width = height * imageScaleRatio;
			float x = buttonPosition.getLowerLeftX();
			float y = buttonPosition.getLowerLeftY();

			float ratio = 0;
			float imageWidth = 0;
			float imageHeigth = 0;

			if (pdImageXObject.getWidth() > pdImageXObject.getHeight()) {
				ratio = calcV(pdImageXObject.getWidth(), pdImageXObject.getHeight());
				float bigger = buttonPosition.getWidth();
				imageWidth = bigger;
				imageHeigth = (float) (bigger / ratio);
				Math.round(imageHeigth);
			}

			if (pdImageXObject.getWidth() < pdImageXObject.getHeight()) {
				ratio = calcV(pdImageXObject.getHeight(), pdImageXObject.getWidth());
				float bigger = buttonPosition.getHeight();
				imageHeigth = bigger;
				imageWidth = (float) (bigger / ratio);
				Math.round(imageWidth);
			}

			PDAppearanceStream pdAppearanceStream = new PDAppearanceStream(_pdfDocument);
			pdAppearanceStream.setResources(new PDResources());
			try (PDPageContentStream pdPageContentStream = new PDPageContentStream(_pdfDocument, pdAppearanceStream)) {
				pdPageContentStream.drawImage(pdImageXObject, x, y, imageWidth, imageHeigth);
			}
			pdAppearanceStream.setBBox(new PDRectangle(x, y, buttonPosition.getWidth(), buttonPosition.getHeight()));

			PDAppearanceDictionary pdAppearanceDictionary = annotationWidget.getAppearance();
			if (pdAppearanceDictionary == null) {
				pdAppearanceDictionary = new PDAppearanceDictionary();
				annotationWidget.setAppearance(pdAppearanceDictionary);
			}

			pdAppearanceDictionary.setNormalAppearance(pdAppearanceStream);

		}

	}

	private PDRectangle getFieldArea(PDField field) {
		COSDictionary fieldDict = field.getCOSObject();
		COSArray fieldAreaArray = (COSArray) fieldDict.getDictionaryObject(COSName.RECT);
		return new PDRectangle(fieldAreaArray);
	}

	float calcV(float s, float t) {
		float v = s / t;
		return v;
	}

}