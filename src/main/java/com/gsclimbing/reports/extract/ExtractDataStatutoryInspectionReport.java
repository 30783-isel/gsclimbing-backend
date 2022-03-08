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
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
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
	private StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder;
	@Autowired
	private StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane;
	@Autowired
	private StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin;
	@Autowired
	private StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice;
	@Autowired
	private StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints;
	
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
				try {
					setStatutoryInspectionReport( (StatutoryInspectionReport) oldStatutoryInspectionReport.clone() );
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getStatutoryInspectionReport().setModifiedDate(LocalDateTime.now());
				getStatutoryInspectionReport().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(1);
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
			getStatutoryInspectionReport().setTypeReport(typeReport);
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
			populateAndCopy(document);
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

	void populateAndCopy(PDDocument document) throws IOException {

		getListPhotoNames().clear();

		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

		List<PDField> fields = acroForm.getFields();

		for (PDField field : fields) {

			if (field instanceof PDTextField) {

				String valueField = ((PDTextField) field).getValue();
				String nameField = field.getFullyQualifiedName();

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
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinManufacturer(valueField);
				if (nameField.equals("inspectionReportServiceCabinType"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinType(valueField);
				if (nameField.equals("inspectionReportServiceCabinSerialNumber"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinSerialNumber(valueField);
				if (nameField.equals("inspectionReportServiceCabinSerialNumberHoist"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinSerialNumberHoist(valueField);
				if (nameField.equals("inspectionReportServiceCabinYearBuild"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinYearBuild(valueField);
				if (nameField.equals("inspectionReportServiceCabin5yearInspectionRequired"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin5yearInspectionRequired(valueField);
				if (nameField.equals("inspectionReportServiceCabinHourMeterReading"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinHourMeterReading(valueField);

				if (nameField.equals("inspectionReportServiceCabinInspectors"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinInspectors(valueField);
				if (nameField.equals("inspectionReportServiceCabinDate"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinDate(valueField);
				if (nameField.equals("inspectionReportServiceCabinResultOfInspection"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinResultOfInspection(valueField);
				if (nameField.equals("inspectionReportServiceCabinRepairRequired"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinRepairRequired(valueField);
				if (nameField.equals("inspectionReportServiceCabinNextInspection"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinNextInspection(valueField);
				if (nameField.equals("inspectionStickerReportServiceCabinNextInspection"))
					getStatutoryInspectionReportServiceCabin().setInspectionStickerReportServiceCabinNextInspection(valueField);
				if (nameField.equals("inspectionReportServiceCabin1Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin1Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin2Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin2Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin3Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin3Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin4Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin4Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin5Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin5Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin6Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin6Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin7Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin7Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin8Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin8Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin9Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin9Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin10Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin10Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin11Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin11Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin12Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin12Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin13Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin13Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin14Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin14Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin15Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin15Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin16Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin16Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin17Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin17Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin18Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin18Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin19Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin19Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin20Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin20Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin21Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin21Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin22Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin22Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin23Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin23Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin24Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin24Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin25Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin25Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin26Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin26Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin27Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin27Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin28Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin28Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin29Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin29Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin30Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin30Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin31Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin31Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin32Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin32Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin33Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin33Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin34Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin34Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin35Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin35Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin36Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin36Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin37Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin37Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin38Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin38Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin39Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin39Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin40Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin40Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin41Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin41Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin42Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin42Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin43Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin43Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin44Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin44Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin45Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin45Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin46Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin46Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin47Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin47Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin48Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin48Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin49Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin49Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin50Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin50Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin51Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin51Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin52Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin52Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin53Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin53Txt(valueField);
				if (nameField.equals("inspectionReportServiceCabin54Txt"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin54Txt(valueField);

					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabinNotes(valueField);

				// 2.2. Inspection report internal crane
				// ----------------------------------------------------------------------------------------------

				if (nameField.equals("internalCraneManufacturer"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneManufacturer_(valueField);
				if (nameField.equals("internalCraneType_"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneType_(valueField);
				if (nameField.equals("internalCraneYearBuild"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneYearBuild(valueField);
				if (nameField.equals("internalCraneSerialNumber_"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneSerialNumber_(valueField);
				
				
				if (nameField.equals("internalCraneTypePlateTestBadge"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneTypePlateTestBadge(valueField);

				if (nameField.equals("internalCraneInspectors"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneInspectors(valueField);
				if (nameField.equals("internalCraneDate"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneDate(valueField);
				if (nameField.equals("internalCraneResultInspection"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneResultInspection(valueField);
				if (nameField.equals("internalCraneRepairRequired"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneRepairRequired(valueField);
				if (nameField.equals("internalCraneNextInspection"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneNextInspection(valueField);

				if (nameField.equals("internalCrane1Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane1Txt(valueField);
				if (nameField.equals("internalCrane2Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane2Txt(valueField);
				if (nameField.equals("internalCrane3Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane3Txt(valueField);
				if (nameField.equals("internalCrane4Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane4Txt(valueField);
				if (nameField.equals("internalCrane5Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane5Txt(valueField);
				if (nameField.equals("internalCrane6Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane6Txt(valueField);
				if (nameField.equals("internalCrane7Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane7Txt(valueField);
				if (nameField.equals("internalCrane8Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane8Txt(valueField);
				if (nameField.equals("internalCrane9Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane9Txt(valueField);
				if (nameField.equals("internalCrane10Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane10Txt(valueField);
				if (nameField.equals("internalCrane11Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane11Txt(valueField);
				if (nameField.equals("internalCrane12Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane12Txt(valueField);
				if (nameField.equals("internalCrane13Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane13Txt(valueField);
				if (nameField.equals("internalCrane14Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane14Txt(valueField);
				if (nameField.equals("internalCrane15Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane15Txt(valueField);
				if (nameField.equals("internalCrane16Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane16Txt(valueField);
				if (nameField.equals("internalCrane17Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane17Txt(valueField);
				if (nameField.equals("internalCrane18Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane18Txt(valueField);
				if (nameField.equals("internalCrane19Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane19Txt(valueField);
				if (nameField.equals("internalCrane20Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane20Txt(valueField);
				if (nameField.equals("internalCrane21Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane21Txt(valueField);
				if (nameField.equals("internalCrane22Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane22Txt(valueField);
				if (nameField.equals("internalCrane23Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane23Txt(valueField);
				if (nameField.equals("internalCrane24Txt"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane24Txt(valueField);

				if (nameField.equals("internalCraneNotes"))
					getStatutoryInspectionReportInternalCrane().setInternalCraneNotes(valueField);

				// 2.3 private String Inspection report ladder
				// ---------------------------------------------------------------;

				if (nameField.equals("inspectionReportLadderManufacturer"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderManufacturer(valueField);
				if (nameField.equals("inspectionReportLadderType"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderType(valueField);
				if (nameField.equals("inspectionReportLadderSerialNumber"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderSerialNumber(valueField);
				if (nameField.equals("inspectionReportLadderManufacturerFallArrestSystem"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderManufacturerFallArrestSystem(valueField);
				if (nameField.equals("inspectionReportLadderType2"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderType2(valueField);
				if (nameField.equals("inspectionReportLadderSerialNumber2"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderSerialNumber2(valueField);
				if (nameField.equals("inspectionReportLadderTypePlateTestBadge"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionReportLadderInspectors"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderInspectors(valueField);
				if (nameField.equals("inspectionReportLadderDate"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderDate(valueField);
				if (nameField.equals("inspectionReportLadderResultInspection"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderResultInspection(valueField);
				if (nameField.equals("inspectionReportLadderRepairRequired"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderRepairRequired(valueField);
				if (nameField.equals("inspectionReportLadderNextInspection"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderNextInspection(valueField);

				if (nameField.equals("inspectionReportLadder1Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder1Txt(valueField);
				if (nameField.equals("inspectionReportLadder2Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder2Txt(valueField);
				if (nameField.equals("inspectionReportLadder3Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder3Txt(valueField);
				if (nameField.equals("inspectionReportLadder4Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder4Txt(valueField);
				if (nameField.equals("inspectionReportLadder5Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder5Txt(valueField);
				if (nameField.equals("inspectionReportLadder6Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder6Txt(valueField);
				if (nameField.equals("inspectionReportLadder7Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder7Txt(valueField);
				if (nameField.equals("inspectionReportLadder8Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder8Txt(valueField);
				if (nameField.equals("inspectionReportLadder9Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder9Txt(valueField);
				if (nameField.equals("inspectionReportLadder10Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder10Txt(valueField);
				if (nameField.equals("inspectionReportLadder11Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder11Txt(valueField);
				if (nameField.equals("inspectionReportLadder12Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder12Txt(valueField);
				if (nameField.equals("inspectionReportLadder13Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder13Txt(valueField);
				if (nameField.equals("inspectionReportLadder14Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder14Txt(valueField);
				if (nameField.equals("inspectionReportLadder15Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder15Txt(valueField);
				if (nameField.equals("inspectionReportLadder16Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder16Txt(valueField);
				if (nameField.equals("inspectionReportLadder17Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder17Txt(valueField);
				if (nameField.equals("inspectionReportLadder18Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder18Txt(valueField);
				if (nameField.equals("inspectionReportLadder19Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder19Txt(valueField);
				if (nameField.equals("inspectionReportLadder20Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder20Txt(valueField);
				if (nameField.equals("inspectionReportLadder21Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder21Txt(valueField);
				if (nameField.equals("inspectionReportLadder22Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder22Txt(valueField);
				if (nameField.equals("inspectionReportLadder23Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder23Txt(valueField);
				if (nameField.equals("inspectionReportLadder24Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder24Txt(valueField);
				if (nameField.equals("inspectionReportLadder25Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder25Txt(valueField);
				if (nameField.equals("inspectionReportLadder26Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder26Txt(valueField);
				if (nameField.equals("inspectionReportLadder27Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder27Txt(valueField);
				if (nameField.equals("inspectionReportLadder28Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder28Txt(valueField);
				if (nameField.equals("inspectionReportLadder29Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder29Txt(valueField);
				if (nameField.equals("inspectionReportLadder30Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder30Txt(valueField);
				if (nameField.equals("inspectionReportLadder31Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder31Txt(valueField);
				if (nameField.equals("inspectionReportLadder32Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder32Txt(valueField);
				if (nameField.equals("inspectionReportLadder33Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder33Txt(valueField);
				if (nameField.equals("inspectionReportLadder34Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder34Txt(valueField);
				if (nameField.equals("inspectionReportLadder35Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder35Txt(valueField);
				if (nameField.equals("inspectionReportLadder36Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder36Txt(valueField);
				if (nameField.equals("inspectionReportLadder37Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder37Txt(valueField);
				if (nameField.equals("inspectionReportLadder38Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder38Txt(valueField);
				if (nameField.equals("inspectionReportLadder39Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder39Txt(valueField);
				if (nameField.equals("inspectionReportLadder40Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder40Txt(valueField);
				if (nameField.equals("inspectionReportLadder41Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder41Txt(valueField);
				if (nameField.equals("inspectionReportLadder42Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder42Txt(valueField);
				if (nameField.equals("inspectionReportLadder43Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder43Txt(valueField);
				if (nameField.equals("inspectionReportLadder44Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder44Txt(valueField);
				if (nameField.equals("inspectionReportLadder45Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder45Txt(valueField);
				if (nameField.equals("inspectionReportLadder46Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder46Txt(valueField);
				if (nameField.equals("inspectionReportLadder47Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder47Txt(valueField);
				if (nameField.equals("inspectionReportLadder48Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder48Txt(valueField);
				if (nameField.equals("inspectionReportLadder49Txt"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder49Txt(valueField);

				if (nameField.equals("inspectionReportLadderNotes"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadderNotes(valueField);

				// 2.4 Inspection Anchor Points
				// -----------------------------------------------------------

				if (nameField.equals("inspectionAnchorPointsManufacturer"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsManufacturer(valueField);
				if (nameField.equals("inspectionAnchorPointsType"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsType(valueField);
				if (nameField.equals("inspectionAnchorPointsTypePlateTestBadge"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionAnchorPointsInspectors"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsInspectors(valueField);
				if (nameField.equals("inspectionAnchorPointsDate"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsDate(valueField);
				if (nameField.equals("inspectionAnchorPointsResultOfInspection"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsResultOfInspection(valueField);
				if (nameField.equals("inspectionAnchorPointsRepairRequired"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsRepairRequired(valueField);
				if (nameField.equals("inspectionAnchorPointsNextInspection"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsNextInspection(valueField);

				if (nameField.equals("inspectionAnchorPoints1Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints1Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints2Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints2Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints3Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints3Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints4Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints4Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints5Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints5Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints6Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints6Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints7Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints7Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints8Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints8Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints9Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints9Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints10Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints10Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints11Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints11Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints12Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints12Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints13Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints13Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints14Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints14Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints15Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints15Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints16Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints16Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints17Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints17Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints18Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints18Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints19Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints19Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints20Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints20Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints21Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints21Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints22Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints22Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints23Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints23Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints24Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints24Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints25Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints25Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints26Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints26Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints27Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints27Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints28Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints28Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints29Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints29Txt(valueField);
				if (nameField.equals("inspectionAnchorPoints30Txt"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints30Txt(valueField);

				if (nameField.equals("inspectionAnchorPointsNotes"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPointsNotes(valueField);

				// 2.5 Inspection descender device
				// ------------------------------------------"))getStatutoryInspectionReport().setInspectionReportServiceCabin1Txt(valueField);

				if (nameField.equals("inspectionDescenderDeviceManufacturer"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceManufacturer(valueField);
				if (nameField.equals("inspectionDescenderDeviceType"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceType(valueField);
				if (nameField.equals("inspectionDescenderDeviceYearBuild"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceYearBuild(valueField);
				if (nameField.equals("inspectionDescenderDeviceSerialNumber"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceSerialNumber(valueField);
				if (nameField.equals("inspectionDescenderDeviceTypePlateTestBadge"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceTypePlateTestBadge(valueField);

				if (nameField.equals("inspectionDescenderDeviceInspectors"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceInspectors(valueField);
				if (nameField.equals("inspectionDescenderDeviceDate"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceDate(valueField);
				if (nameField.equals("inspectionDescenderDeviceResultOfInspection"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceResultOfInspection(valueField);
				if (nameField.equals("inspectionDescenderDeviceRepairRequired"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceRepairRequired(valueField);
				if (nameField.equals("inspectionDescenderDeviceNextInspection"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceNextInspection(valueField);

				if (nameField.equals("inspectionDescenderDevice1Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice1Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice2Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice2Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice3Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice3Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice4Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice4Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice5Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice5Txt(valueField);
				if (nameField.equals("inspectionDescenderDevice6Txt"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice6Txt(valueField);

				if (nameField.equals("inspectionDescenderDeviceNotes"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDeviceNotes(valueField);

			} else if (field instanceof PDCheckBox) {

				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();

				// 2.1 inspection Report Service Cabin
				// ---------------------------------------------------------------;

				if (nameField.equals("inspectionReportServiceCabin1Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin1Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin2Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin2Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin3Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin3Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin4Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin4Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin5Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin5Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin6Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin6Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin7Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin7Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin8Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin8Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin9Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin9Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin10Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin10Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin11Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin11Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin12Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin12Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin13Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin13Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin14Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin14Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin15Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin15Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin16Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin16Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin17Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin17Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin18Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin18Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin19Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin19Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin20Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin20Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin21Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin21Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin22Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin22Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin23Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin23Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin24Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin24Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin25Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin25Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin26Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin26Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin27Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin27Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin28Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin28Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin29Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin29Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin30Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin30Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin31Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin31Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin32Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin32Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin33Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin33Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin34Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin34Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin35Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin35Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin36Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin36Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin37Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin37Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin38Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin38Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin39Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin39Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin40Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin40Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin41Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin41Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin42Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin42Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin43Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin43Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin44Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin44Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin45Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin45Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin46Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin46Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin47Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin47Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin48Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin48Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin49Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin49Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin50Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin50Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin51Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin51Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin52Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin52Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin53Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin53Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportServiceCabin54Chk"))
					getStatutoryInspectionReportServiceCabin().setInspectionReportServiceCabin54Chk(valueField =="Yes" ? true : false);

				// 2.2 Internal Crane
				// -----------------------------------------------------------;

				if (nameField.equals("internalCrane1Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane1Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane2Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane2Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane3Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane3Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane4Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane4Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane5Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane5Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane6Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane6Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane7Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane7Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane8Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane8Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane9Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane9Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane10Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane10Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane11Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane11Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane12Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane12Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane13Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane13Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane14Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane14Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane15Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane15Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane16Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane16Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane17Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane17Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane18Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane18Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane19Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane19Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane20Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane20Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane21Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane21Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane22Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane22Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane23Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane23Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("internalCrane24Chk"))
					getStatutoryInspectionReportInternalCrane().setInternalCrane24Chk(valueField =="Yes" ? true : false);

				// 2.3 Inspection Report Ladder
				// -----------------------------------------------------------;

				if (nameField.equals("inspectionReportLadder1Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder1Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder2Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder2Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder3Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder3Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder4Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder4Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder5Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder5Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder6Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder6Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder7Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder7Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder8Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder8Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder9Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder9Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder10Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder10Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder11Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder11Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder12Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder12Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder13Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder13Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder14Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder14Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder15Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder15Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder16Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder16Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder17Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder17Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder18Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder18Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder19Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder19Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder20Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder20Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder21Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder21Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder22Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder22Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder23Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder23Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder24Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder24Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder25Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder25Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder26Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder26Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder27Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder27Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder28Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder28Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder29Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder29Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder30Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder30Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder31Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder31Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder32Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder32Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder33Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder33Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder34Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder34Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder35Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder35Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder36Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder36Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder37Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder37Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder38Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder38Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder39Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder39Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder40Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder40Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder41Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder41Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder42Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder42Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder43Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder43Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder44Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder44Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder45Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder45Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder46Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder46Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder47Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder47Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder48Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder48Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionReportLadder49Chk"))
					getStatutoryInspectionReportInspectionReportLadder().setInspectionReportLadder49Chk(valueField =="Yes" ? true : false);

				// 2.4 Inspection Anchor Points
				// -----------------------------------------------------------

				if (nameField.equals("inspectionAnchorPoints1Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints1Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints2Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints2Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints3Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints3Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints4Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints4Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints5Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints5Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints6Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints6Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints7Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints7Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints8Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints8Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints9Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints9Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints10Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints10Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints11Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints11Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints12Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints12Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints13Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints13Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints14Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints14Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints15Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints15Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints16Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints16Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints17Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints17Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints18Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints18Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints19Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints19Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints20Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints20Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints21Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints21Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints22Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints22Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints23Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints23Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints24Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints24Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints25Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints25Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints26Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints26Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints27Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints27Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints28Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints28Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints29Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints29Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionAnchorPoints30Chk"))
					getStatutoryInspectionReportInspectionAnchorPoints().setInspectionAnchorPoints30Chk(valueField =="Yes" ? true : false);

				// 2.5 Inspection descender device ------------------------------------------"

				if (nameField.equals("inspectionDescenderDevice1Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice1Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionDescenderDevice2Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice2Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionDescenderDevice3Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice3Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionDescenderDevice4Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice4Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionDescenderDevice5Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice5Chk(valueField =="Yes" ? true : false);
				if (nameField.equals("inspectionDescenderDevice6Chk"))
					getStatutoryInspectionReportInspectionDescenderDevice().setInspectionDescenderDevice6Chk(valueField =="Yes" ? true : false);

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
							statutoryInspectionReport.addOneMorePicture();
							extractAnnotationImages(pDimage, nameField, fileData);

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					;
				}

			}
		}

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
			alteration.setField(imageFieldName);
			alteration.setFieldOld(null);
			alteration.setFieldNew(null);
			alteration.setImage(true);
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

	public StatutoryInspectionReport getStatutoryInspectionReport() {
		return statutoryInspectionReport;
	}

	public void setStatutoryInspectionReport(StatutoryInspectionReport statutoryInspectionReport) {
		this.statutoryInspectionReport = statutoryInspectionReport;
	}

	public StatutoryInspectionReportInspectionAnchorPoints getStatutoryInspectionReportInspectionAnchorPoints() {
		return statutoryInspectionReportInspectionAnchorPoints;
	}

	public void setStatutoryInspectionReportInspectionAnchorPoints(StatutoryInspectionReportInspectionAnchorPoints statutoryInspectionReportInspectionAnchorPoints) {
		this.statutoryInspectionReportInspectionAnchorPoints = statutoryInspectionReportInspectionAnchorPoints;
	}

	public StatutoryInspectionReportInspectionDescenderDevice getStatutoryInspectionReportInspectionDescenderDevice() {
		return statutoryInspectionReportInspectionDescenderDevice;
	}

	public void setStatutoryInspectionReportInspectionDescenderDevice(StatutoryInspectionReportInspectionDescenderDevice statutoryInspectionReportInspectionDescenderDevice) {
		this.statutoryInspectionReportInspectionDescenderDevice = statutoryInspectionReportInspectionDescenderDevice;
	}

	public StatutoryInspectionReportInspectionReportLadder getStatutoryInspectionReportInspectionReportLadder() {
		return statutoryInspectionReportInspectionReportLadder;
	}

	public void setStatutoryInspectionReportInspectionReportLadder(StatutoryInspectionReportInspectionReportLadder statutoryInspectionReportInspectionReportLadder) {
		this.statutoryInspectionReportInspectionReportLadder = statutoryInspectionReportInspectionReportLadder;
	}

	public StatutoryInspectionReportInternalCrane getStatutoryInspectionReportInternalCrane() {
		return statutoryInspectionReportInternalCrane;
	}

	public void setStatutoryInspectionReportInternalCrane(StatutoryInspectionReportInternalCrane statutoryInspectionReportInternalCrane) {
		this.statutoryInspectionReportInternalCrane = statutoryInspectionReportInternalCrane;
	}

	public StatutoryInspectionReportServiceCabin getStatutoryInspectionReportServiceCabin() {
		return statutoryInspectionReportServiceCabin;
	}

	public void setStatutoryInspectionReportServiceCabin(StatutoryInspectionReportServiceCabin statutoryInspectionReportServiceCabin) {
		this.statutoryInspectionReportServiceCabin = statutoryInspectionReportServiceCabin;
	}

	public String getUuidStr() {
		return uuidStr;
	}

	public void setUuidStr(String uuidStr) {
		this.uuidStr = uuidStr;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public List<String> getListPhotoNames() {
		return listPhotoNames;
	}

	public void setListPhotoNames(List<String> listPhotoNames) {
		this.listPhotoNames = listPhotoNames;
	}

	public long fileSize(File file) {
		long bytes = file.length();
		long kilobytes = (bytes / 1024);
		return kilobytes;
	}
}
