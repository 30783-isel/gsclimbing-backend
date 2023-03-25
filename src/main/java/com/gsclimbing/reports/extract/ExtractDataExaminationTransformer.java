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
import com.gsclimbing.database.entity.ExaminationTransformer;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.ExaminationTransformerService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataExaminationTransformer {
	
	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	List<String> listPhotoNames = new ArrayList<String>();
	private ExaminationTransformer examinationTransformer;
	 
	@Autowired
	private UserRepository userService;
	@Autowired
	private TurbineService turbineService;
	@Autowired
	private AlterationService alterationService;
	@Autowired
	private HistoricReportService historicReportService;
	@Autowired
	private ExaminationTransformerService examinationTransformerService;
	@Autowired
	private com.gsclimbing.database.service.FileService fileService;

	public ExaminationTransformer readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		ExaminationTransformer oldExaminationTransformer = null;
		ExaminationTransformer examinationTransformerReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldExaminationTransformer = examinationTransformerService.readExaminationTransformer(idReport);
			if (oldExaminationTransformer != null) {
				try {
					setExaminationTransformer((ExaminationTransformer) oldExaminationTransformer.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getExaminationTransformer().setModifiedDate(LocalDateTime.now());
				getExaminationTransformer().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.ET.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = examinationTransformerService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getExaminationTransformer());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setExaminationTransformer(new ExaminationTransformer());
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getExaminationTransformer().setUuid(uuid);
			getExaminationTransformer().setCreateDate(LocalDateTime.now());
			getExaminationTransformer().setModifiedDate(LocalDateTime.now());
			
			Turbine turbine = turbineService.getTurbine(turbineId);
			getExaminationTransformer().setTurbine(turbine);
			getExaminationTransformer().setProjectoId(turbine.getProject().getIdProject());
			getExaminationTransformer().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getExaminationTransformer());
		}

		getExaminationTransformer().setLocked("true");
		getExaminationTransformer().setPermission2Edit("false");
		setExaminationTransformer(getExaminationTransformer());
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
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldExaminationTransformer, examinationTransformer, historicReport);
			historicReport.setListAlternation(listaAlternation);
			getExaminationTransformer().getListHistoric().add(historicReport);
			examinationTransformerReturned = examinationTransformerService.updateExaminationTransformer(getExaminationTransformer());
		} else {
			examinationTransformerReturned = examinationTransformerService.createExaminationTransformer(getExaminationTransformer());
		}
		return examinationTransformerReturned;
	}
	
	private boolean populateAndCopy(PDDocument document, Integer typeReport) throws IOException {
		getListPhotoNames().clear();
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		List<PDField> fields = acroForm.getFields();
		boolean imageInsertion = false;
		for (PDField field : fields) {
			if (field instanceof PDTextField) {
				String valueField = ((PDTextField) field).getValue();
				String nameField = field.getFullyQualifiedName();
				if (nameField.equals("typeReport")) {
					if(typeReport == Integer.valueOf(valueField)) {
						getExaminationTransformer().setTypeReport(Integer.valueOf(valueField));
					}else {
						return false;
					}
				}
				if (nameField.equals("site"))
					getExaminationTransformer().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getExaminationTransformer().setWtgNumber(valueField);
				if (nameField.equals("wtgType"))
					getExaminationTransformer().setWtgType(valueField);
				if (nameField.equals("yearConstruction"))
					getExaminationTransformer().setYearConstruction(valueField);
				if (nameField.equals("dateOfMeasurement"))
					getExaminationTransformer().setDateOfMeasurement(valueField);
				if (nameField.equals("manufacturer"))
					getExaminationTransformer().setManufacturer(valueField);
				if (nameField.equals("type"))
					getExaminationTransformer().setType(valueField);
				if (nameField.equals("equipamentSerialNumber"))
					getExaminationTransformer().setEquipamentSerialNumber(valueField);
				if (nameField.equals("equipamentType1"))
					getExaminationTransformer().setEquipamentType1(valueField);
				if (nameField.equals("serialNumber1"))
					getExaminationTransformer().setSerialNumber1(valueField);
				if (nameField.equals("calibrationDate1"))
					getExaminationTransformer().setCalibrationDate1(valueField);
				if (nameField.equals("nextCalibrationDate1"))
					getExaminationTransformer().setNextCalibrationDate1(valueField);
				if (nameField.equals("terminals1_1"))
					getExaminationTransformer().setTerminals1_1(valueField);
				if (nameField.equals("terminals1_2"))
					getExaminationTransformer().setTerminals1_2(valueField);
				if (nameField.equals("terminals1_3"))
					getExaminationTransformer().setTerminals1_3(valueField);
				if (nameField.equals("terminals2_1"))
					getExaminationTransformer().setTerminals2_1(valueField);
				if (nameField.equals("terminals2_2"))
					getExaminationTransformer().setTerminals2_2(valueField);
				if (nameField.equals("terminals2_3"))
					getExaminationTransformer().setTerminals2_3(valueField);
				if (nameField.equals("terminals3_1"))
					getExaminationTransformer().setTerminals3_1(valueField);
				if (nameField.equals("terminals3_2"))
					getExaminationTransformer().setTerminals3_2(valueField);
				if (nameField.equals("terminals3_3"))
					getExaminationTransformer().setTerminals3_3(valueField);
				if (nameField.equals("tolerancia1"))
					getExaminationTransformer().setTolerancia1(valueField);
				if (nameField.equals("tolerancia2"))
					getExaminationTransformer().setTolerancia2(valueField);
				if (nameField.equals("tolerancia3"))
					getExaminationTransformer().setTolerancia3(valueField);
				if (nameField.equals("tolerancia4"))
					getExaminationTransformer().setTolerancia4(valueField);
				if (nameField.equals("tolerancia5"))
					getExaminationTransformer().setTolerancia5(valueField);
				if (nameField.equals("tolerancia6"))
					getExaminationTransformer().setTolerancia6(valueField);
				if (nameField.equals("equipamentType2"))
					getExaminationTransformer().setEquipamentType2(valueField);
				if (nameField.equals("serialNumber2"))
					getExaminationTransformer().setSerialNumber2(valueField);
				if (nameField.equals("calibrationDate2"))
					getExaminationTransformer().setCalibrationDate2(valueField);
				if (nameField.equals("nextCalibrationDate2"))
					getExaminationTransformer().setNextCalibrationDate2(valueField);
				if (nameField.equals("voltage1"))
					getExaminationTransformer().setVoltage1(valueField);
				if (nameField.equals("voltage2"))
					getExaminationTransformer().setVoltage2(valueField);
				if (nameField.equals("voltage3"))
					getExaminationTransformer().setVoltage3(valueField);
				if (nameField.equals("voltage4"))
					getExaminationTransformer().setVoltage4(valueField);
				if (nameField.equals("voltage5"))
					getExaminationTransformer().setVoltage5(valueField);
				if (nameField.equals("voltage6"))
					getExaminationTransformer().setVoltage6(valueField);
				if (nameField.equals("resistencia1"))
					getExaminationTransformer().setResistencia1(valueField);
				if (nameField.equals("resistencia2"))
					getExaminationTransformer().setResistencia2(valueField);
				if (nameField.equals("resistencia3"))
					getExaminationTransformer().setResistencia3(valueField);
				if (nameField.equals("resistencia4"))
					getExaminationTransformer().setResistencia4(valueField);
				if (nameField.equals("resistencia5"))
					getExaminationTransformer().setResistencia5(valueField);
				if (nameField.equals("resistencia6"))
					getExaminationTransformer().setResistencia6(valueField);
				if (nameField.equals("medida1"))
					getExaminationTransformer().setMedida1(valueField);
				if (nameField.equals("medida2"))
					getExaminationTransformer().setMedida2(valueField);
				if (nameField.equals("medida3"))
					getExaminationTransformer().setMedida3(valueField);
				if (nameField.equals("medida4"))
					getExaminationTransformer().setMedida4(valueField);
				if (nameField.equals("medida5"))
					getExaminationTransformer().setMedida5(valueField);
				if (nameField.equals("medida6"))
					getExaminationTransformer().setMedida6(valueField);
				if (nameField.equals("equipamentType3"))
					getExaminationTransformer().setEquipamentType3(valueField);
				if (nameField.equals("serialNumber3"))
					getExaminationTransformer().setSerialNumber3(valueField);
				if (nameField.equals("calibrationDate3"))
					getExaminationTransformer().setCalibrationDate3(valueField);
				if (nameField.equals("nextCalibrationDate3"))
					getExaminationTransformer().setNextCalibrationDate3(valueField);
				if (nameField.equals("voltage"))
					getExaminationTransformer().setVoltage(valueField);
				if (nameField.equals("corrent1"))
					getExaminationTransformer().setCorrent1(valueField);
				if (nameField.equals("corrent2"))
					getExaminationTransformer().setCorrent2(valueField);
				if (nameField.equals("equipamentType4"))
					getExaminationTransformer().setEquipamentType4(valueField);
				if (nameField.equals("serialNumber4"))
					getExaminationTransformer().setSerialNumber4(valueField);
				if (nameField.equals("calibrationDate4"))
					getExaminationTransformer().setCalibrationDate4(valueField);
				if (nameField.equals("nextCalibrationDate4"))
					getExaminationTransformer().setNextCalibrationDate4(valueField);
				if (nameField.equals("insulationResistance"))
					getExaminationTransformer().setInsulationResistance(valueField);
				if (nameField.equals("ratioTest"))
					getExaminationTransformer().setRatioTest(valueField);
				if (nameField.equals("conclusion"))
					getExaminationTransformer().setConclusion(valueField);
				if (nameField.equals("performedBy"))
					getExaminationTransformer().setPerformedBy(valueField);
				if (nameField.equals("date"))
					getExaminationTransformer().setDate(valueField);
							
				if (nameField.equals("additionalField1Label"))
					getExaminationTransformer().setAdditionalField1Label(valueField);
				if (nameField.equals("additionalField1Text"))
					getExaminationTransformer().setAdditionalField1Text(valueField);
				if (nameField.equals("additionalField2Label"))
					getExaminationTransformer().setAdditionalField2Label(valueField);
				if (nameField.equals("additionalField2Text"))
					getExaminationTransformer().setAdditionalField2Text(valueField);
				if (nameField.equals("additionalField3Label"))
					getExaminationTransformer().setAdditionalField3Label(valueField);
				if (nameField.equals("additionalField3Text"))
					getExaminationTransformer().setAdditionalField3Text(valueField);
				if (nameField.equals("additionalField4Label"))
					getExaminationTransformer().setAdditionalField4Label(valueField);
				if (nameField.equals("additionalField4Text"))
					getExaminationTransformer().setAdditionalField4Text(valueField);
				if (nameField.equals("additionalField5Label"))
					getExaminationTransformer().setAdditionalField5Label(valueField);
				if (nameField.equals("additionalField5Text"))
					getExaminationTransformer().setAdditionalField5Text(valueField);
				if (nameField.equals("additionalField6Label"))
					getExaminationTransformer().setAdditionalField6Label(valueField);
				if (nameField.equals("additionalField6Text"))
					getExaminationTransformer().setAdditionalField6Text(valueField);
				if (nameField.equals("additionalField7Label"))
					getExaminationTransformer().setAdditionalField7Label(valueField);
				if (nameField.equals("additionalField7Text"))
					getExaminationTransformer().setAdditionalField7Text(valueField);
				
				if (nameField.contains("description") && imageInsertion) {
					setNameField(nameField);
					setDescription(valueField);
				}
			} else if (field instanceof PDCheckBox) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();
				if (nameField.equals("insertImagesChk")) {
					getExaminationTransformer().setInsertImagesChk(valueField);
					if ("Yes".equals(valueField)) {
						imageInsertion = true;
					} else if ("Off".equals(valueField)) {
						imageInsertion = false;
					}
				}
			} else if (field instanceof PDRadioButton) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDRadioButton) field).getValue();
				
				if (nameField.equals("visualInspectionTransformer"))
					getExaminationTransformer().setVisualInspectionTransformer(valueField);
				if (nameField.equals("ratioIdentified"))
					getExaminationTransformer().setRatioIdentified(valueField);
				
			} else if (field instanceof PDPushButton) {
				if (imageInsertion) {
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
							examinationTransformer.addImgOnListImages(fileData);
							extractAnnotationImages(pDimage, nameField, fileData);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				}
			}
		}
		return true;
	}
	
	public void extractAnnotationImages(PDImage image, String nameFile, FileData fileData) throws IOException {
		List<FileData> listFileData = fileService.readFile(getExaminationTransformer().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getExaminationTransformer().getUuid());
			fileData.setCreateDate(getExaminationTransformer().getCreateDate());
			fileData.setModifiedDate(getExaminationTransformer().getModifiedDate());
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
			fileData.setReport(getExaminationTransformer());
			getExaminationTransformer().getListaFileData().add(fileData);
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
			alteration.setOldPicByte(null);
			alteration.setNewPicByte(null);
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

	public ExaminationTransformer getExaminationTransformer() {
		return examinationTransformer;
	}

	public void setExaminationTransformer(ExaminationTransformer examinationTransformer) {
		this.examinationTransformer = examinationTransformer;
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
