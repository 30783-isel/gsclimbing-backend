package com.gsclimbing.reports.extract;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionAnchorPoints;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionDescenderDevice;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInspectionReportLadder;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportInternalCrane;
import com.gsclimbing.database.entity.statutory_inspection_report.StatutoryInspectionReportServiceCabin;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.ExaminationTransformerService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.StatutoryInspectionReportReportService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataStatutoryInspectionReport {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	private StatutoryInspectionReport statutoryInspectionReport;
	List<String> listPhotoNames = new ArrayList<String>();
	
	@Autowired
	private StatutoryInspectionReportReportService statutoryInspectionReportService;
		
	@Autowired
	private TurbineService turbineService;
	@Autowired
	private FileService fileService;
	@Autowired
	private UserRepository userService;
	@Autowired
	private HistoricReportService historicReportService;
	@Autowired
	private AlterationService alterationService;

	public StatutoryInspectionReport readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		StatutoryInspectionReport oldStatutoryInspectionReport = null;
		StatutoryInspectionReport statutoryInspectionReportReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldStatutoryInspectionReport = statutoryInspectionReportService.readStatutoryInspectionReport(idReport);
			if (oldStatutoryInspectionReport != null) {
				
				setStatutoryInspectionReport( (StatutoryInspectionReport) oldStatutoryInspectionReport.clone() );
				
				getStatutoryInspectionReport().setModifiedDate(LocalDateTime.now());
				getStatutoryInspectionReport().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.SIR.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = statutoryInspectionReportService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getStatutoryInspectionReport());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setStatutoryInspectionReport(new StatutoryInspectionReport());;
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getStatutoryInspectionReport().setUuid(uuid);
			getStatutoryInspectionReport().setCreateDate(LocalDateTime.now());
			getStatutoryInspectionReport().setModifiedDate(LocalDateTime.now());
			
			Turbine turbine = turbineService.getTurbine(turbineId);
			getStatutoryInspectionReport().setTurbine(turbine);
			getStatutoryInspectionReport().setProjectoId(turbine.getProject().getIdProject());
			getStatutoryInspectionReport().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getStatutoryInspectionReport());
		}

		getStatutoryInspectionReport().setLocked("true");
		getStatutoryInspectionReport().setPermission2Edit("false");
		setStatutoryInspectionReport(getStatutoryInspectionReport());
		File convfile = null;
		try {
			convfile = multipartToFile(file, file.getOriginalFilename());
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		try (PDDocument document = PDDocument.load(convfile)) {
			if(!populateAndCopy(document, typeReport)) {
				return null;
			}
		}
		if ("UPDATE".equals(operacao)) {
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldStatutoryInspectionReport, getStatutoryInspectionReport(), historicReport);
			historicReport.setListAlternation(listaAlternation);
			getStatutoryInspectionReport().getListHistoric().add(historicReport);
			statutoryInspectionReportReturned = statutoryInspectionReportService.updateStatutoryInspectionReport(getStatutoryInspectionReport());
		} else {
			statutoryInspectionReportReturned = statutoryInspectionReportService.createStatutoryInspectionReport(getStatutoryInspectionReport());
		}
		return statutoryInspectionReportReturned;
	}

	private boolean populateAndCopy(PDDocument document, Integer typeReport) throws IOException {
		
		StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin = getStatutoryInspectionReport().getStatutoryInspectionReportServiceCabin() == null ? new StatutoryInspectionReportServiceCabin() : getStatutoryInspectionReport().getStatutoryInspectionReportServiceCabin();
		StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane = getStatutoryInspectionReport().getStatutoryInspectionReportInternalCrane() == null ? new StatutoryInspectionReportInternalCrane() : getStatutoryInspectionReport().getStatutoryInspectionReportInternalCrane();
		StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder = getStatutoryInspectionReport().getStatutoryInspectionReportInspectionReportLadder() == null ? new StatutoryInspectionReportInspectionReportLadder() : getStatutoryInspectionReport().getStatutoryInspectionReportInspectionReportLadder();
		StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints = getStatutoryInspectionReport().getStatutoryInspectionReportInspectionAnchorPoints() == null ? new StatutoryInspectionReportInspectionAnchorPoints() : getStatutoryInspectionReport().getStatutoryInspectionReportInspectionAnchorPoints();
		StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice = getStatutoryInspectionReport().getStatutoryInspectionReportInspectionDescenderDevice() == null ? new StatutoryInspectionReportInspectionDescenderDevice() : getStatutoryInspectionReport().getStatutoryInspectionReportInspectionDescenderDevice();
		
		getListPhotoNames().clear();
		boolean imageInsertion = false;
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		List<PDField> fields = acroForm.getFields();
		for (PDField field : fields) {
			if (field instanceof PDTextField) {
				String valueField = ((PDTextField) field).getValue();
				String nameField = field.getFullyQualifiedName();
				if (nameField.equals("typeReport")) {
					if(typeReport == Integer.valueOf(valueField)) {
						getStatutoryInspectionReport().setTypeReport(Integer.valueOf(valueField));
					}else {
						return false;
					}
				}
				
				
				
				
				if (nameField.equals("additionalField1Label"))
					getStatutoryInspectionReport().setAdditionalField1Label(valueField);
				if (nameField.equals("additionalField1Text"))
					getStatutoryInspectionReport().setAdditionalField1Text(valueField);
				if (nameField.equals("additionalField2Label"))
					getStatutoryInspectionReport().setAdditionalField2Label(valueField);
				if (nameField.equals("additionalField2Text"))
					getStatutoryInspectionReport().setAdditionalField2Text(valueField);
				if (nameField.equals("additionalField3Label"))
					getStatutoryInspectionReport().setAdditionalField3Label(valueField);
				if (nameField.equals("additionalField3Text"))
					getStatutoryInspectionReport().setAdditionalField3Text(valueField);
				if (nameField.equals("additionalField4Label"))
					getStatutoryInspectionReport().setAdditionalField4Label(valueField);
				if (nameField.equals("additionalField4Text"))
					getStatutoryInspectionReport().setAdditionalField4Text(valueField);
				if (nameField.equals("additionalField5Label"))
					getStatutoryInspectionReport().setAdditionalField5Label(valueField);
				if (nameField.equals("additionalField5Text"))
					getStatutoryInspectionReport().setAdditionalField5Text(valueField);
				if (nameField.equals("additionalField6Label"))
					getStatutoryInspectionReport().setAdditionalField6Label(valueField);
				if (nameField.equals("additionalField6Text"))
					getStatutoryInspectionReport().setAdditionalField6Text(valueField);
				if (nameField.equals("additionalField7Label"))
					getStatutoryInspectionReport().setAdditionalField7Label(valueField);
				if (nameField.equals("additionalField7Text"))
					getStatutoryInspectionReport().setAdditionalField7Text(valueField);
				
				
				if (nameField.equals("site"))
					getStatutoryInspectionReport().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getStatutoryInspectionReport().setWtgNumber(valueField);
				if (nameField.equals("wtgType"))
					getStatutoryInspectionReport().setWtgType(valueField);

				if (nameField.equals("reportNumber"))
					getStatutoryInspectionReport().setReportNumber(valueField);
				if (nameField.equals("client"))
					getStatutoryInspectionReport().setClient(valueField);
				if (nameField.equals("clientContact"))
					getStatutoryInspectionReport().setClientContact(valueField);
				if (nameField.equals("windPark"))
					getStatutoryInspectionReport().setWindPark(valueField);
				if (nameField.equals("siteAddress"))
					getStatutoryInspectionReport().setSiteAddress(valueField);

				if (nameField.equals("wtgType"))
					getStatutoryInspectionReport().setWtgType(valueField);
				if (nameField.equals("wtgNumber"))
					getStatutoryInspectionReport().setWtgNumber(valueField);
				if (nameField.equals("site"))
					getStatutoryInspectionReport().setSite(valueField);
				if (nameField.equals("yearOfConstruction"))
					getStatutoryInspectionReport().setYearConstruction(valueField);

				if (nameField.equals("serviceCabinManufacturer"))
					getStatutoryInspectionReport().setServiceCabinManufacturer(valueField);
				if (nameField.equals("serviceCabinType"))
					getStatutoryInspectionReport().setServiceCabinType(valueField);
				if (nameField.equals("serviceCabinSerialNumber"))
					getStatutoryInspectionReport().setServiceCabinSerialNumber(valueField);
				if (nameField.equals("serviceCabinInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setServiceCabinInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("serviceCabinInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setServiceCabinInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("serviceCabinInspectionNotPassed"))
					getStatutoryInspectionReport().setServiceCabinInspectionNotPassed(valueField);

				if (nameField.equals("ladderTowerManufacturer"))
					getStatutoryInspectionReport().setLadderTowerManufacturer(valueField);
				if (nameField.equals("ladderTowerType"))
					getStatutoryInspectionReport().setLadderTowerType(valueField);
				if (nameField.equals("ladderTowerSerialNumber"))
					getStatutoryInspectionReport().setLadderTowerSerialNumber(valueField);
				if (nameField.equals("ladderTowerInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setLadderTowerInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("ladderTowerInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setLadderTowerInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("ladderTowerInspectionNotPassed"))
					getStatutoryInspectionReport().setLadderTowerInspectionNotPassed(valueField);

			    

				if (nameField.equals("failArrestSystemTowerManufacturer"))
					getStatutoryInspectionReport().setFailArrestSystemTowerManufacturer(valueField);
				if (nameField.equals("failArrestSystemTowerType"))
					getStatutoryInspectionReport().setFailArrestSystemTowerType(valueField);
				if (nameField.equals("failArrestSystemTowerSerialNumber"))
					getStatutoryInspectionReport().setFailArrestSystemTowerSerialNumber(valueField);
				if (nameField.equals("failArrestSystemToweInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setFailArrestSystemTowerInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("failArrestSystemTowerInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setFailArrestSystemTowerInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("failArrestSystemTowerInspectionNotPassed"))
					getStatutoryInspectionReport().setFailArrestSystemTowerInspectionNotPassed(valueField);

				if (nameField.equals("internalCraneManufacturer"))
					getStatutoryInspectionReport().setInternalCraneManufacturer(valueField);
				if (nameField.equals("internalCraneType"))
					getStatutoryInspectionReport().setInternalCraneType(valueField);
				if (nameField.equals("internalCraneSerialNumber"))
					getStatutoryInspectionReport().setInternalCraneSerialNumber(valueField);
				if (nameField.equals("internalCraneInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setInternalCraneInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("internalCraneInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setInternalCraneInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("internalCraneInspectionNotPassed"))
					getStatutoryInspectionReport().setInternalCraneInspectionNotPassed(valueField);

				if (nameField.equals("descenderDeviceManufacturer"))
					getStatutoryInspectionReport().setDescenderDeviceManufacturer(valueField);
				if (nameField.equals("descenderDeviceType"))
					getStatutoryInspectionReport().setDescenderDeviceType(valueField);
				if (nameField.equals("descenderDeviceSerialNumber"))
					getStatutoryInspectionReport().setDescenderDeviceSerialNumber(valueField);
				if (nameField.equals("descenderDeviceInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setDescenderDeviceInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("descenderDeviceInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setDescenderDeviceInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("descenderDeviceInspectionNotPassed"))
					getStatutoryInspectionReport().setDescenderDeviceInspectionNotPassed(valueField);

				if (nameField.equals("anchorPointsManufacturer"))
					getStatutoryInspectionReport().setAnchorPointsManufacturer(valueField);
				if (nameField.equals("anchorPointsType"))
					getStatutoryInspectionReport().setAnchorPointsType(valueField);
				if (nameField.equals("anchorPointsSerialNumber"))
					getStatutoryInspectionReport().setAnchorPointsSerialNumber(valueField);
				if (nameField.equals("anchorPointsInspectionPassedWithoutDefects"))
					getStatutoryInspectionReport().setAnchorPointsInspectionPassedWithoutDefects(valueField);
				if (nameField.equals("anchorPointsInspectionPassedWithSmallDefects"))
					getStatutoryInspectionReport().setAnchorPointsInspectionPassedWithSmallDefects(valueField);
				if (nameField.equals("anchorPointsInspectionNotPassed"))
					getStatutoryInspectionReport().setAnchorPointsInspectionNotPassed(valueField);

				if (nameField.equals("inspectors"))
					getStatutoryInspectionReport().setInspectors(valueField);
				if (nameField.equals("date"))
					getStatutoryInspectionReport().setDate(valueField);
				if (nameField.equals("resultOfInspection"))
					getStatutoryInspectionReport().setResultOfInspection(valueField);
				if (nameField.equals("repairRequired"))
					getStatutoryInspectionReport().setRepairRequired(valueField);
				if (nameField.equals("nextInspection"))
					getStatutoryInspectionReport().setNextInspection(valueField);

				if (nameField.equals("siteDate"))
					getStatutoryInspectionReport().setSiteDate(valueField);
				if (nameField.equals("responsibleTechnician"))
					getStatutoryInspectionReport().setResponsibleTechnician(valueField);
				
				// 2.1 Inspection report service cabin
				// ------------------------------------------------------------

				
				if (nameField.equals("inspectionReportServiceCabinManufacturer"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinManufacturer(valueField);
				if (nameField.equals("inspectionReportServiceCabinType"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinType(valueField);
				if (nameField.equals("inspectionReportServiceCabinSerialNumber"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinSerialNumber(valueField);
				if (nameField.equals("inspectionReportServiceCabinSerialNumberHoist"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinSerialNumberHoist(valueField);
				if (nameField.equals("inspectionReportServiceCabinYearBuild"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinYearBuild(valueField);
				if (nameField.equals("inspectionReportServiceCabin5yearInspectionRequired"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin5yearInspectionRequired(valueField);
				if (nameField.equals("inspectionReportServiceCabinHourMeterReading"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinHourMeterReading(valueField);

				if (nameField.equals("inspectionReportServiceCabinInspectors"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinInspectors(valueField);
				if (nameField.equals("inspectionReportServiceCabinDate"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinDate(valueField);
				if (nameField.equals("inspectionReportServiceCabinResultOfInspection"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinResultOfInspection(valueField);
				if (nameField.equals("inspectionReportServiceCabinRepairRequired"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinRepairRequired(valueField);
				if (nameField.equals("inspectionReportServiceCabinNextInspection"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinNextInspection(valueField);
				if (nameField.equals("inspectionReportServiceCabinNextInspectionSticker"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinNextInspectionSticker(valueField);
				if (nameField.equals("inspectionReportServiceCabin1Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin1Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin2Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin2Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin3Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin3Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin4Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin4Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin5Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin5Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin6Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin6Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin7Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin7Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin8Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin8Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin9Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin9Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin10Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin10Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin11Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin11Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin12Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin12Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin13Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin13Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin14Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin14Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin15Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin15Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin16Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin16Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin17Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin17Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin18Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin18Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin19Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin19Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin20Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin20Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin21Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin21Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin22Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin22Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin23Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin23Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin24Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin24Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin25Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin25Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin26Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin26Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin27Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin27Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin28Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin28Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin29Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin29Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin30Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin30Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin31Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin31Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin32Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin32Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin33Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin33Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin34Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin34Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin35Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin35Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin36Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin36Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin37Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin37Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin38Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin38Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin39Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin39Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin40Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin40Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin41Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin41Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin42Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin42Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin43Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin43Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin44Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin44Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin45Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin45Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin46Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin46Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin47Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin47Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin48Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin48Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin49Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin49Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin50Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin50Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin51Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin51Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin52Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin52Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin53Txt"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin53Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabinNotes"))
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabinNotes(valueField);

				// 2.2. Inspection report internal crane
				// ----------------------------------------------------------------------------------------------
					
				if (nameField.equals("internalCraneManufacturerPage"))
					statutoryInspectionReportInternalCrane.setInternalCraneManufacturerPage(valueField);
				if (nameField.equals("internalCraneTypePage"))
					statutoryInspectionReportInternalCrane.setInternalCraneTypePage(valueField);
				if (nameField.equals("internalCraneYearBuild"))
					statutoryInspectionReportInternalCrane.setInternalCraneYearBuild(valueField);
				if (nameField.equals("internalCraneSerialNumberPage"))
					statutoryInspectionReportInternalCrane.setInternalCraneSerialNumberPage(valueField);
				
				
				if (nameField.equals("internalCraneTypePlateTestBadge"))
					statutoryInspectionReportInternalCrane.setInternalCraneTypePlateTestBadge(valueField);

				if (nameField.equals("internalCraneInspectors"))
					statutoryInspectionReportInternalCrane.setInternalCraneInspectors(valueField);
				if (nameField.equals("internalCraneDate"))
					statutoryInspectionReportInternalCrane.setInternalCraneDate(valueField);
				if (nameField.equals("internalCraneResultInspection"))
					statutoryInspectionReportInternalCrane.setInternalCraneResultInspection(valueField);
				if (nameField.equals("internalCraneRepairRequired"))
					statutoryInspectionReportInternalCrane.setInternalCraneRepairRequired(valueField);
				if (nameField.equals("internalCraneNextInspection"))
					statutoryInspectionReportInternalCrane.setInternalCraneNextInspection(valueField);

				if (nameField.equals("internalCrane1Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane1Txt(valueField);
				if (nameField.equals("internalCrane2Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane2Txt(valueField);
				if (nameField.equals("internalCrane3Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane3Txt(valueField);
				if (nameField.equals("internalCrane4Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane4Txt(valueField);
				if (nameField.equals("internalCrane5Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane5Txt(valueField);
				if (nameField.equals("internalCrane6Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane6Txt(valueField);
				if (nameField.equals("internalCrane7Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane7Txt(valueField);
				if (nameField.equals("internalCrane8Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane8Txt(valueField);
				if (nameField.equals("internalCrane9Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane9Txt(valueField);
				if (nameField.equals("internalCrane10Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane10Txt(valueField);
				if (nameField.equals("internalCrane11Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane11Txt(valueField);
				if (nameField.equals("internalCrane12Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane12Txt(valueField);
				if (nameField.equals("internalCrane13Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane13Txt(valueField);
				if (nameField.equals("internalCrane14Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane14Txt(valueField);
				if (nameField.equals("internalCrane15Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane15Txt(valueField);
				if (nameField.equals("internalCrane16Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane16Txt(valueField);
				if (nameField.equals("internalCrane17Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane17Txt(valueField);
				if (nameField.equals("internalCrane18Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane18Txt(valueField);
				if (nameField.equals("internalCrane19Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane19Txt(valueField);
				if (nameField.equals("internalCrane20Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane20Txt(valueField);
				if (nameField.equals("internalCrane21Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane21Txt(valueField);
				if (nameField.equals("internalCrane22Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane22Txt(valueField);
				if (nameField.equals("internalCrane23Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane23Txt(valueField);
				if (nameField.equals("internalCrane24Txt"))
					statutoryInspectionReportInternalCrane.setInternalCrane24Txt(valueField);

				if (nameField.equals("internalCraneNotes"))
					statutoryInspectionReportInternalCrane.setInternalCraneNotes(valueField);

				// 2.3 private String Inspection report ladder
				// ---------------------------------------------------------------;
				
				if (nameField.equals("inspectionReportLadderManufacturer"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderManufacturer(valueField);
				if (nameField.equals("inspectionReportLadderType"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderType(valueField);
				if (nameField.equals("inspectionReportLadderSerialNumber"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderSerialNumber(valueField);
				if (nameField.equals("inspectionReportLadderManufacturerFallArrestSystem"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderManufacturerFallArrestSystem(valueField);
				if (nameField.equals("inspectionReportLadderType2"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderType2(valueField);
				if (nameField.equals("inspectionReportLadderSerialNumber2"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderSerialNumber2(valueField);
				if (nameField.equals("inspectionReportLadderTypePlateTestBadge"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionReportLadderInspectors"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderInspectors(valueField);
				if (nameField.equals("inspectionReportLadderDate"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderDate(valueField);
				if (nameField.equals("inspectionReportLadderResultInspection"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderResultInspection(valueField);
				if (nameField.equals("inspectionReportLadderRepairRequired"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderRepairRequired(valueField);
				if (nameField.equals("inspectionReportLadderNextInspection"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderNextInspection(valueField);

				if (nameField.equals("inspectionReportLadder1Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder1Txt(valueField);
				if (nameField.equals("inspectionReportLadder2Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder2Txt(valueField);
				if (nameField.equals("inspectionReportLadder3Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder3Txt(valueField);
				if (nameField.equals("inspectionReportLadder4Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder4Txt(valueField);
				if (nameField.equals("inspectionReportLadder5Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder5Txt(valueField);
				if (nameField.equals("inspectionReportLadder6Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder6Txt(valueField);
				if (nameField.equals("inspectionReportLadder7Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder7Txt(valueField);
				if (nameField.equals("inspectionReportLadder8Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder8Txt(valueField);
				if (nameField.equals("inspectionReportLadder9Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder9Txt(valueField);
				if (nameField.equals("inspectionReportLadder10Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder10Txt(valueField);
				if (nameField.equals("inspectionReportLadder11Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder11Txt(valueField);
				if (nameField.equals("inspectionReportLadder12Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder12Txt(valueField);
				if (nameField.equals("inspectionReportLadder13Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder13Txt(valueField);
				if (nameField.equals("inspectionReportLadder14Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder14Txt(valueField);
				if (nameField.equals("inspectionReportLadder15Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder15Txt(valueField);
				if (nameField.equals("inspectionReportLadder16Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder16Txt(valueField);
				if (nameField.equals("inspectionReportLadder17Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder17Txt(valueField);
				if (nameField.equals("inspectionReportLadder18Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder18Txt(valueField);
				if (nameField.equals("inspectionReportLadder19Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder19Txt(valueField);
				if (nameField.equals("inspectionReportLadder20Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder20Txt(valueField);
				if (nameField.equals("inspectionReportLadder21Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder21Txt(valueField);
				if (nameField.equals("inspectionReportLadder22Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder22Txt(valueField);
				if (nameField.equals("inspectionReportLadder23Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder23Txt(valueField);
				if (nameField.equals("inspectionReportLadder24Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder24Txt(valueField);
				if (nameField.equals("inspectionReportLadder25Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder25Txt(valueField);
				if (nameField.equals("inspectionReportLadder26Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder26Txt(valueField);
				if (nameField.equals("inspectionReportLadder27Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder27Txt(valueField);
				if (nameField.equals("inspectionReportLadder28Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder28Txt(valueField);
				if (nameField.equals("inspectionReportLadder29Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder29Txt(valueField);
				if (nameField.equals("inspectionReportLadder30Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder30Txt(valueField);
				if (nameField.equals("inspectionReportLadder31Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder31Txt(valueField);
				if (nameField.equals("inspectionReportLadder32Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder32Txt(valueField);
				if (nameField.equals("inspectionReportLadder33Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder33Txt(valueField);
				if (nameField.equals("inspectionReportLadder34Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder34Txt(valueField);
				if (nameField.equals("inspectionReportLadder35Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder35Txt(valueField);
				if (nameField.equals("inspectionReportLadder36Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder36Txt(valueField);
				if (nameField.equals("inspectionReportLadder37Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder37Txt(valueField);
				if (nameField.equals("inspectionReportLadder38Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder38Txt(valueField);
				if (nameField.equals("inspectionReportLadder39Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder39Txt(valueField);
				if (nameField.equals("inspectionReportLadder40Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder40Txt(valueField);
				if (nameField.equals("inspectionReportLadder41Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder41Txt(valueField);
				if (nameField.equals("inspectionReportLadder42Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder42Txt(valueField);
				if (nameField.equals("inspectionReportLadder43Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder43Txt(valueField);
				if (nameField.equals("inspectionReportLadder44Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder44Txt(valueField);
				if (nameField.equals("inspectionReportLadder45Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder45Txt(valueField);
				if (nameField.equals("inspectionReportLadder46Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder46Txt(valueField);
				if (nameField.equals("inspectionReportLadder47Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder47Txt(valueField);
				if (nameField.equals("inspectionReportLadder48Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder48Txt(valueField);
				if (nameField.equals("inspectionReportLadder49Txt"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder49Txt(valueField);

				if (nameField.equals("inspectionReportLadderNotes"))
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadderNotes(valueField);

				// 2.4 Inspection Anchor Points
				// -----------------------------------------------------------
				
				if (nameField.equals("inspectionAnchorPointsManufacturer"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsManufacturer(valueField);
				if (nameField.equals("inspectionAnchorPointsType"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsType(valueField);
				if (nameField.equals("inspectionAnchorPointsTypePlateTestBadge"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionAnchorPointsInspectors"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsInspectors(valueField);
				if (nameField.equals("inspectionAnchorPointsDate"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsDate(valueField);
				if (nameField.equals("inspectionAnchorPointsResultOfInspection"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsResultOfInspection(valueField);
				if (nameField.equals("inspectionAnchorPointsRepairRequired"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsRepairRequired(valueField);
				if (nameField.equals("inspectionAnchorPointsNextInspection"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsNextInspection(valueField);

				if (nameField.equals("inspectionAnchorPoints1Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints1Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints2Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints2Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints3Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints3Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints4Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints4Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints5Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints5Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints6Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints6Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints7Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints7Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints8Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints8Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints9Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints9Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints10Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints10Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints11Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints11Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints12Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints12Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints13Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints13Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints14Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints14Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints15Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints15Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints16Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints16Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints17Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints17Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints18Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints18Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints19Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints19Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints20Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints20Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints21Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints21Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints22Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints22Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints23Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints23Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints24Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints24Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints25Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints25Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints26Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints26Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints27Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints27Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints28Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints28Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints29Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints29Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints30Txt"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints30Txt(valueField);

				if (nameField.equals("inspectionAnchorPointsNotes"))
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPointsNotes(valueField);

				// 2.5 Inspection descender device
			
				if (nameField.equals("inspectionDescenderDeviceManufacturer"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceManufacturer(valueField);
				if (nameField.equals("inspectionDescenderDeviceType"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceType(valueField);
				if (nameField.equals("inspectionDescenderDeviceYearBuild"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceYearBuild(valueField);
				if (nameField.equals("inspectionDescenderDeviceSerialNumber"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceSerialNumber(valueField);
				if (nameField.equals("inspectionDescenderDeviceTypePlateTestBadge"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionDescenderDeviceInspectors"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceInspectors(valueField);
				if (nameField.equals("inspectionDescenderDeviceDate"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceDate(valueField);
				if (nameField.equals("inspectionDescenderDeviceResultOfInspection"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceResultOfInspection(valueField);
				if (nameField.equals("inspectionDescenderDeviceRepairRequired"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceRepairRequired(valueField);
				if (nameField.equals("inspectionDescenderDeviceNextInspection"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceNextInspection(valueField);

				if (nameField.equals("inspectionDescenderDevice1Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice1Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice2Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice2Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice3Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice3Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice4Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice4Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice5Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice5Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice6Txt"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice6Txt(valueField);

				if (nameField.equals("inspectionDescenderDeviceNotes"))
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDeviceNotes(valueField);

				if (nameField.contains("description") && imageInsertion) {
					setNameField(nameField);
					setDescription(valueField);
				}
			} else if (field instanceof PDCheckBox) {

				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();

				if (nameField.equals("insertImagesChk")) {
					getStatutoryInspectionReport().setInsertImagesChk(valueField);
					if ("Yes".equals(valueField)) {
						imageInsertion = true;
					} else if ("Off".equals(valueField)) {
						imageInsertion = false;
					}
				}	
			} else if (field instanceof PDRadioButton) {

				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDRadioButton) field).getValue();
				
				// 2.1 inspection Report Service Cabin
				// ---------------------------------------------------------------;

				if (nameField.equals("inspectionReportServiceCabin1Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin1Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin2Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin2Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin3Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin3Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin4Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin4Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin5Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin5Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin6Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin6Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin7Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin7Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin8Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin8Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin9Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin9Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin10Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin10Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin11Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin11Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin12Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin12Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin13Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin13Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin14Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin14Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin15Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin15Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin16Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin16Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin17Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin17Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin18Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin18Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin19Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin19Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin20Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin20Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin21Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin21Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin22Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin22Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin23Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin23Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin24Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin24Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin25Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin25Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin26Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin26Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin27Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin27Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin28Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin28Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin29Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin29Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin30Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin30Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin31Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin31Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin32Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin32Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin33Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin33Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin34Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin34Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin35Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin35Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin36Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin36Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin37Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin37Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin38Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin38Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin39Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin39Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin40Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin40Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin41Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin41Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin42Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin42Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin43Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin43Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin44Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin44Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin45Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin45Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin46Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin46Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin47Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin47Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin48Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin48Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin49Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin49Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin50Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin50Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin51Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin51Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin52Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin52Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportServiceCabin53Chk")) {
					statutoryInspectionReportServiceCabin.setInspectionReportServiceCabin53Chk(valueField.equals("true") ? true : false);
				}
				
				
				

				// 2.2 Internal Crane
				// -----------------------------------------------------------;

				if (nameField.equals("internalCrane1Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane1Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane2Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane2Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane3Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane3Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane4Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane4Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane5Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane5Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane6Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane6Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane7Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane7Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane8Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane8Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane9Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane9Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane10Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane10Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane11Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane11Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane12Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane12Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane13Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane13Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane14Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane14Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane15Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane15Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane16Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane16Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane17Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane17Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane18Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane18Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane19Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane19Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane20Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane20Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane21Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane21Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane22Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane22Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane23Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane23Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("internalCrane24Chk")) {
					statutoryInspectionReportInternalCrane.setInternalCrane24Chk(valueField.equals("true") ? true : false);
				}
					
				// 2.3 Inspection Report Ladder
				// -----------------------------------------------------------;

				if (nameField.equals("inspectionReportLadder1Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder1Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder2Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder2Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder3Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder3Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder4Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder4Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder5Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder5Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder6Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder6Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder7Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder7Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder8Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder8Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder9Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder9Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder10Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder10Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder11Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder11Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder12Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder12Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder13Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder13Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder14Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder14Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder15Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder15Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder16Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder16Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder17Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder17Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder18Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder18Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder19Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder19Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder20Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder20Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder21Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder21Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder22Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder22Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder23Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder23Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder24Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder24Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder25Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder25Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder26Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder26Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder27Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder27Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder28Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder28Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder29Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder29Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder30Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder30Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder31Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder31Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder32Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder32Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder33Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder33Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder34Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder34Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder35Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder35Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder36Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder36Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder37Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder37Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder38Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder38Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder39Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder39Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder40Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder40Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder41Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder41Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder42Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder42Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder43Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder43Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder44Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder44Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder45Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder45Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder46Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder46Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder47Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder47Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder48Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder48Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionReportLadder49Chk")) {
					statutoryInspectionReportInspectionReportLadder.setInspectionReportLadder49Chk(valueField.equals("true") ? true : false);
				}
				
				
				// 2.4 Inspection Anchor Points
				// -----------------------------------------------------------

				if (nameField.equals("inspectionAnchorPoints1Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints1Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints2Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints2Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints3Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints3Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints4Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints4Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints5Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints5Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints6Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints6Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints7Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints7Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints8Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints8Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints9Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints9Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints10Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints10Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints11Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints11Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints12Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints12Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints13Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints13Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints14Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints14Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints15Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints15Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints16Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints16Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints17Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints17Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints18Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints18Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints19Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints19Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints20Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints20Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints21Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints21Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints22Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints22Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints23Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints23Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints24Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints24Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints25Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints25Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints26Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints26Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints27Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints27Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints28Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints28Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints29Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints29Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionAnchorPoints30Chk")) {
					statutoryInspectionReportInspectionAnchorPoints.setInspectionAnchorPoints30Chk(valueField.equals("true") ? true : false);
				}
					
				
				// 2.5 Inspection descender device ------------------------------------------"

				if (nameField.equals("inspectionDescenderDevice1Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice1Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionDescenderDevice2Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice2Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionDescenderDevice3Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice3Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionDescenderDevice4Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice4Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionDescenderDevice5Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice5Chk(valueField.equals("true") ? true : false);
				}
				if (nameField.equals("inspectionDescenderDevice6Chk")) {
					statutoryInspectionReportInspectionDescenderDevice.setInspectionDescenderDevice6Chk(valueField.equals("true") ? true : false);
				}
			} else if (field instanceof PDPushButton) {

				String nameField = field.getFullyQualifiedName();

				for (final PDAnnotationWidget widget : field.getWidgets()) {

					WidgetImageChecker checker = new WidgetImageChecker(widget);
					try {
						if (checker.hasImages()) {
							PDImage pDimage = checker.getpDimage();

							setPhoto(nameField);
							FileData fileData = new FileData();
							fileData.setImageChange(0);
							fileData.setNameField(getNameField());
							fileData.setDescription(getDescription());
							statutoryInspectionReport.addImgOnListImages(fileData);
							extractAnnotationImages(pDimage, nameField, fileData);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		getStatutoryInspectionReport().setStatutoryInspectionReportServiceCabin(statutoryInspectionReportServiceCabin);
		getStatutoryInspectionReport().setStatutoryInspectionReportInternalCrane(statutoryInspectionReportInternalCrane);
		getStatutoryInspectionReport().setStatutoryInspectionReportInspectionReportLadder(statutoryInspectionReportInspectionReportLadder);
		getStatutoryInspectionReport().setStatutoryInspectionReportInspectionAnchorPoints(statutoryInspectionReportInspectionAnchorPoints);
		getStatutoryInspectionReport().setStatutoryInspectionReportInspectionDescenderDevice(statutoryInspectionReportInspectionDescenderDevice);
		
		statutoryInspectionReportServiceCabin.setStatutoryInspectionReport(getStatutoryInspectionReport());
		statutoryInspectionReportInternalCrane.setStatutoryInspectionReport(getStatutoryInspectionReport());
		statutoryInspectionReportInspectionReportLadder.setStatutoryInspectionReport(getStatutoryInspectionReport());
		statutoryInspectionReportInspectionAnchorPoints.setStatutoryInspectionReport(getStatutoryInspectionReport());
		statutoryInspectionReportInspectionDescenderDevice.setStatutoryInspectionReport(getStatutoryInspectionReport());
		
		return true;
	}

	public void extractAnnotationImages(PDImage image, String nameFile, FileData fileData) throws IOException {
		List<FileData> listFileData = fileService.readFile(getStatutoryInspectionReport().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getStatutoryInspectionReport().getUuid());
			fileData.setCreateDate(getStatutoryInspectionReport().getCreateDate());
			fileData.setModifiedDate(getStatutoryInspectionReport().getModifiedDate());
			fileData.setName(nameFile);
			fileData.setMimeType("JPG");
			try {
				fileData.setHash();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			File file = File.createTempFile(fileData.getHash(), null);
			ImageIO.write(image.getImage(), "jpg", file);
			fileData.setSize(fileSize(file));
			boolean inserted = FTPUploadFile.uploadFile2FTPServer(file, fileData.getHash());
			fileData.setInsertedOnFtpServer(inserted);
			getListPhotoNames().add(nameFile);
			fileData.setReport(getStatutoryInspectionReport());
			getStatutoryInspectionReport().getListaFileData().add(fileData);
		} else {
			uploadImage(image, fileDataFiltered.get().getHash(), String.valueOf(getIdHistoric()), fileDataFiltered.get().getName());
		}
	}

	private void uploadImage(PDImage image, String hash, String idHistoric, String imageFieldName) throws IOException {

		File file = File.createTempFile(imageFieldName, null);
		ImageIO.write(image.getImage(), "jpg", file);

		FileData fileData = fileService.readFileByHash(hash);
		fileData.addImageChange();

		if (fileSize(file) != fileData.getSize()) {
			Alteration alteration = new Alteration();
			alteration.setField(new Report().mapeamento().get(imageFieldName));
			alteration.setFieldOld(null);
			alteration.setFieldNew(null);
			alteration.setHash(hash);
			alteration.setImage(true);
			alteration.setImageChange(fileData.getImageChange());
			alteration.setLocalDateTime(LocalDateTime.now());
			alteration.setHistoricReport(historicReport);
			if (historicReport != null) {
				historicReport.getListAlternation().add(alteration);
				historicReport.addNumAlterations();
				historicReportService.saveHistoricReport(historicReport);
			}
			FTPUploadFile.replaceFile2FTPServer(file, hash, fileData.getImageChange());
		}
	}

	static class WidgetImageChecker extends PDFGraphicsStreamEngine {

		private PDImage pDimage;

		WidgetImageChecker(PDAnnotationWidget widget) {
			super(widget.getPage());
			this.widget = widget;
		}

		boolean hasImages() throws IOException {
			count = 0;
			PDAppearanceStream normalAppearance = widget.getNormalAppearanceStream();
			processChildStream(normalAppearance, widget.getPage());
			return count != 0;
		}

		@Override
		public void drawImage(PDImage pdImage) throws IOException {
			count++;
			this.pDimage = pdImage;
		}

		@Override
		public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
		}

		@Override
		public void clip(int windingRule) throws IOException {
		}

		@Override
		public void moveTo(float x, float y) throws IOException {
		}

		@Override
		public void lineTo(float x, float y) throws IOException {
		}

		@Override
		public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
		}

		@Override
		public Point2D getCurrentPoint() throws IOException {
			return null;
		}

		@Override
		public void closePath() throws IOException {
		}

		@Override
		public void endPath() throws IOException {
		}

		@Override
		public void strokePath() throws IOException {
		}

		@Override
		public void fillPath(int windingRule) throws IOException {
		}

		@Override
		public void fillAndStrokePath(int windingRule) throws IOException {
		}

		@Override
		public void shadingFill(COSName shadingName) throws IOException {
		}

		final PDAnnotationWidget widget;
		int count = 0;

		public PDImage getpDimage() {
			return pDimage;
		}

		public void setpDimage(PDImage pDimage) {
			this.pDimage = pDimage;
		}

	}

	public File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {

		File convFile = File.createTempFile(fileName, null);
		multipart.transferTo(convFile);
		return convFile;
	}

	public long fileSize(File file) {
		long bytes = file.length();
		long kilobytes = (bytes / 1024);
		return kilobytes;
	}
}
