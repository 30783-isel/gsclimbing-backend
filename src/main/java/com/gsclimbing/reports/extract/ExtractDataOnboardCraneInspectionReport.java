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

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.OnboardCraneInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.OnboardCraneInspectionReport;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.ExaminationTransformerService;
import com.gsclimbing.database.service.OnboardCraneInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataOnboardCraneInspectionReport {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	private OnboardCraneInspectionReport onboardCraneInspectionReport;
	List<String> listPhotoNames = new ArrayList<String>();
	
	@Autowired
	private OnboardCraneInspectionReportService onboardCraneInspectionReportService;
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

	public OnboardCraneInspectionReport readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		OnboardCraneInspectionReport oldOnboardCraneInspectionReport = null;
		OnboardCraneInspectionReport onboardCraneInspectionReportReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldOnboardCraneInspectionReport = onboardCraneInspectionReportService.readOnboardCraneInspectionReport(idReport);
			if (oldOnboardCraneInspectionReport != null) {
				try {
					setOnboardCraneInspectionReport( (OnboardCraneInspectionReport) oldOnboardCraneInspectionReport.clone() );
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getOnboardCraneInspectionReport().setModifiedDate(LocalDateTime.now());
				getOnboardCraneInspectionReport().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.OCIR.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = onboardCraneInspectionReportService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getOnboardCraneInspectionReport());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setOnboardCraneInspectionReport(new OnboardCraneInspectionReport());;
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getOnboardCraneInspectionReport().setUuid(uuid);
			getOnboardCraneInspectionReport().setCreateDate(LocalDateTime.now());
			getOnboardCraneInspectionReport().setModifiedDate(LocalDateTime.now());
			
			Turbine turbine = turbineService.getTurbine(turbineId);
			getOnboardCraneInspectionReport().setTurbine(turbine);
			getOnboardCraneInspectionReport().setProjectoId(turbine.getProject().getIdProject());
			getOnboardCraneInspectionReport().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getOnboardCraneInspectionReport());
		}

		getOnboardCraneInspectionReport().setLocked("true");
		getOnboardCraneInspectionReport().setPermission2Edit("false");
		setOnboardCraneInspectionReport(getOnboardCraneInspectionReport());
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
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldOnboardCraneInspectionReport, getOnboardCraneInspectionReport(), historicReport);
			historicReport.setListAlternation(listaAlternation);
			getOnboardCraneInspectionReport().getListHistoric().add(historicReport);
			onboardCraneInspectionReportReturned = onboardCraneInspectionReportService.updateOnboardCraneInspectionReport(getOnboardCraneInspectionReport());
		} else {
			onboardCraneInspectionReportReturned = onboardCraneInspectionReportService.createOnboardCraneInspectionReport(getOnboardCraneInspectionReport());
		}
		return onboardCraneInspectionReportReturned;
	}

	private boolean populateAndCopy(PDDocument document, Integer typeReport) throws IOException {
		getListPhotoNames().clear();
		boolean imageInsertion = false;
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		List<PDField> fields = acroForm.getFields();
		for (PDField field : fields) {
			if (field instanceof PDTextField) {
				String valueField = ((PDTextField) field).getValue();
				String nameField = field.getFullyQualifiedName();
				System.out.println(nameField + " - " + valueField);
				if (nameField.equals("typeReport")) {
					if(typeReport == Integer.valueOf(valueField)) {
						getOnboardCraneInspectionReport().setTypeReport(Integer.valueOf(valueField));
					}else {
						return false;
					}
				}
				if (nameField.equals("site"))
					getOnboardCraneInspectionReport().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getOnboardCraneInspectionReport().setWtgNumber(valueField);
				if (nameField.equals("wtgType"))
					getOnboardCraneInspectionReport().setWtgType(valueField);
				if (nameField.equals("manufacturerOnboardCrane"))
					getOnboardCraneInspectionReport().setManufacturerOnboardCrane(valueField);
				if (nameField.equals("type"))
					getOnboardCraneInspectionReport().setType(valueField);
				if (nameField.equals("yearBuild"))
					getOnboardCraneInspectionReport().setYearBuild(valueField);
				if (nameField.equals("serialNumber"))
					getOnboardCraneInspectionReport().setSerialNumber(valueField);
				if (nameField.equals("typePlateTestBadge"))
					getOnboardCraneInspectionReport().setTypePlateTestBadge(valueField);
				if (nameField.equals("inspectors"))
					getOnboardCraneInspectionReport().setInspectors(valueField);
				if (nameField.equals("date"))
					getOnboardCraneInspectionReport().setDate(valueField);
				if (nameField.equals("resultInspection"))
					getOnboardCraneInspectionReport().setResultInspection(valueField);
				if (nameField.equals("repairRequired"))
					getOnboardCraneInspectionReport().setRepairRequired(valueField);
				if (nameField.equals("nextInspection"))
					getOnboardCraneInspectionReport().setNextInspection(valueField);
				if (nameField.equals("readingOperatingBusTxt"))
					getOnboardCraneInspectionReport().setReadingOperatingBusTxt(valueField);
				if (nameField.equals("InterruptVoltageSupplyTxt"))
					getOnboardCraneInspectionReport().setInterruptVoltageSupplyTxt(valueField);
				if (nameField.equals("circuitDiagramPositionTxt"))
					getOnboardCraneInspectionReport().setCircuitDiagramPositionTxt(valueField);
				if (nameField.equals("warningSignsTxt"))
					getOnboardCraneInspectionReport().setWarningSignsTxt(valueField);
				if (nameField.equals("cablesSignsTxt"))
					getOnboardCraneInspectionReport().setCablesSignsTxt(valueField);
				if (nameField.equals("screwedCableGlandsTxt"))
					getOnboardCraneInspectionReport().setScrewedCableGlandsTxt(valueField);
				if (nameField.equals("openSwitchCabinetCoverTxt"))
					getOnboardCraneInspectionReport().setOpenSwitchCabinetCoverTxt(valueField);
				if (nameField.equals("checkOperatingUnitTxt"))
					getOnboardCraneInspectionReport().setCheckOperatingUnitTxt(valueField);
				if (nameField.equals("checkLimitSwitchesTxt"))
					getOnboardCraneInspectionReport().setCheckLimitSwitchesTxt(valueField);
				if (nameField.equals("setVoltageSupplyTxt"))
					getOnboardCraneInspectionReport().setSetVoltageSupplyTxt(valueField);
				if (nameField.equals("checkMotorBrakeTxt"))
					getOnboardCraneInspectionReport().setCheckMotorBrakeTxt(valueField);
				if (nameField.equals("checkRopeMechanicalDamageTxt"))
					getOnboardCraneInspectionReport().setCheckRopeMechanicalDamageTxt(valueField);
				if (nameField.equals("checkLoadHookMechanicalTxt"))
					getOnboardCraneInspectionReport().setCheckLoadHookMechanicalTxt(valueField);
				if (nameField.equals("carryVisualInspectionTxt"))
					getOnboardCraneInspectionReport().setCarryVisualInspectionTxt(valueField);
				if (nameField.equals("usefeelerGaugeTxt"))
					getOnboardCraneInspectionReport().setUsefeelerGaugeTxt(valueField);
				if (nameField.equals("checkLimitSwitchRockersTxt"))
					getOnboardCraneInspectionReport().setCheckLimitSwitchRockersTxt(valueField);
				if (nameField.equals("checkCraneBridgeTxt"))
					getOnboardCraneInspectionReport().setCheckCraneBridgeTxt(valueField);
				if (nameField.equals("checkCrabTxt"))
					getOnboardCraneInspectionReport().setCheckCrabTxt(valueField);
				if (nameField.equals("checkDeflectionRollersTxt"))
					getOnboardCraneInspectionReport().setCheckDeflectionRollersTxt(valueField);
				if (nameField.equals("checkScrewJointsTxt"))
					getOnboardCraneInspectionReport().setCheckScrewJointsTxt(valueField);
				if (nameField.equals("checkAllRollersBridgeTxt"))
					getOnboardCraneInspectionReport().setCheckAllRollersBridgeTxt(valueField);
				if (nameField.equals("checkAllStopBuffersTxt"))
					getOnboardCraneInspectionReport().setCheckAllStopBuffersTxt(valueField);
				if (nameField.equals("checkAllComponentsTxt"))
					getOnboardCraneInspectionReport().setCheckAllComponentsTxt(valueField);
				if (nameField.equals("ancorPointSafetyEquipmentTxt"))
					getOnboardCraneInspectionReport().setAncorPointSafetyEquipmentTxt(valueField);
				if (nameField.equals("loadTestTxt"))
					getOnboardCraneInspectionReport().setLoadTestTxt(valueField);
				if (nameField.equals("notes"))
					getOnboardCraneInspectionReport().setNotes(valueField);
				if (nameField.contains("description") && imageInsertion) {
					setNameField(nameField);
					setDescription(valueField);
				}
			} else if (field instanceof PDCheckBox) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();
				System.out.println(nameField + " - " + valueField);
				
				if (nameField.equals("readingOperatingBusChk"))
					getOnboardCraneInspectionReport().setReadingOperatingBusChk(valueField == "Yes" ? true : false);
				if (nameField.equals("InterruptVoltageSupplyChk"))
					getOnboardCraneInspectionReport().setInterruptVoltageSupplyChk(valueField == "Yes" ? true : false);
				if (nameField.equals("circuitDiagramPositionChk"))
					getOnboardCraneInspectionReport().setCircuitDiagramPositionChk(valueField == "Yes" ? true : false);
				if (nameField.equals("warningSignsChk"))
					getOnboardCraneInspectionReport().setWarningSignsChk(valueField == "Yes" ? true : false);
				if (nameField.equals("cablesSignsChk"))
					getOnboardCraneInspectionReport().setCablesSignsChk(valueField == "Yes" ? true : false);
				if (nameField.equals("screwedCableGlandsChk"))
					getOnboardCraneInspectionReport().setScrewedCableGlandsChk(valueField == "Yes" ? true : false);
				if (nameField.equals("openSwitchCabinetCoverChk"))
					getOnboardCraneInspectionReport().setOpenSwitchCabinetCoverChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkOperatingUnitChk"))
					getOnboardCraneInspectionReport().setCheckOperatingUnitChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkLimitSwitchesChk"))
					getOnboardCraneInspectionReport().setCheckLimitSwitchesChk(valueField == "Yes" ? true : false);
				if (nameField.equals("setVoltageSupplyChk"))
					getOnboardCraneInspectionReport().setSetVoltageSupplyChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkMotorBrakeChk"))
					getOnboardCraneInspectionReport().setCheckMotorBrakeChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkRopeMechanicalDamageChk"))
					getOnboardCraneInspectionReport().setCheckRopeMechanicalDamageChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkLoadHookMechanicalChk"))
					getOnboardCraneInspectionReport().setCheckLoadHookMechanicalChk(valueField == "Yes" ? true : false);
				if (nameField.equals("carryVisualInspectionChk"))
					getOnboardCraneInspectionReport().setCarryVisualInspectionChk(valueField == "Yes" ? true : false);
				if (nameField.equals("usefeelerGaugeChk"))
					getOnboardCraneInspectionReport().setUsefeelerGaugeChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkLimitSwitchRockersChk"))
					getOnboardCraneInspectionReport().setCheckLimitSwitchRockersChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkCraneBridgeChk"))
					getOnboardCraneInspectionReport().setCheckCraneBridgeChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkCrabChk"))
					getOnboardCraneInspectionReport().setCheckCrabChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkDeflectionRollersChk"))
					getOnboardCraneInspectionReport().setCheckDeflectionRollersChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkScrewJointsChk"))
					getOnboardCraneInspectionReport().setCheckScrewJointsChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkAllRollersBridgeChk"))
					getOnboardCraneInspectionReport().setCheckAllRollersBridgeChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkAllStopBuffersChk"))
					getOnboardCraneInspectionReport().setCheckAllStopBuffersChk(valueField == "Yes" ? true : false);
				if (nameField.equals("checkAllComponentsChk"))
					getOnboardCraneInspectionReport().setCheckAllComponentsChk(valueField == "Yes" ? true : false);
				if (nameField.equals("ancorPointSafetyEquipmentChk"))
					getOnboardCraneInspectionReport().setAncorPointSafetyEquipmentChk(valueField == "Yes" ? true : false);
				if (nameField.equals("loadTestChk"))
					getOnboardCraneInspectionReport().setLoadTestChk(valueField == "Yes" ? true : false);
				if (nameField.equals("insertImagesChk")) {
					getOnboardCraneInspectionReport().setInsertImagesChk(valueField);
					if ("Yes".equals(valueField)) {
						imageInsertion = true;
					} else if ("Off".equals(valueField)) {
						imageInsertion = false;
					}
				}
			} else if (field instanceof PDPushButton) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDPushButton) field).getValue();
				System.out.println(nameField + " - " + valueField);
				
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
							onboardCraneInspectionReport.addImgOnListImages(fileData);
							extractAnnotationImages(pDimage, nameField, fileData);		
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	public void extractAnnotationImages(PDImage image, String nameFile, FileData fileData) throws IOException {
		List<FileData> listFileData = fileService.readFile(getOnboardCraneInspectionReport().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getOnboardCraneInspectionReport().getUuid());
			fileData.setCreateDate(getOnboardCraneInspectionReport().getCreateDate());
			fileData.setModifiedDate(getOnboardCraneInspectionReport().getModifiedDate());
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
			fileData.setReport(getOnboardCraneInspectionReport());
			getOnboardCraneInspectionReport().getListaFileData().add(fileData);
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

	public long fileSize(File file) {
		long bytes = file.length();
		long kilobytes = (bytes / 1024);
		return kilobytes;
	}
}
