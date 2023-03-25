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
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.ExaminationTransformerService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.Medidas6KvService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataMedidas6Kv {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	private Medidas6Kv medidas6Kv;
	List<String> listPhotoNames = new ArrayList<String>();

	@Autowired
	private Medidas6KvService medidas6KvService;
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

	public Medidas6Kv readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		Medidas6Kv oldMedidas6Kv = null;
		Medidas6Kv medidas6KvReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldMedidas6Kv = medidas6KvService.readMedidas6Kv(idReport);
			if (oldMedidas6Kv != null) {
				try {
					setMedidas6Kv((Medidas6Kv) oldMedidas6Kv.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getMedidas6Kv().setModifiedDate(LocalDateTime.now());
				getMedidas6Kv().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.M6KV.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = medidas6KvService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getMedidas6Kv());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setMedidas6Kv(new Medidas6Kv());
			;
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getMedidas6Kv().setUuid(uuid);
			getMedidas6Kv().setCreateDate(LocalDateTime.now());
			getMedidas6Kv().setModifiedDate(LocalDateTime.now());

			Turbine turbine = turbineService.getTurbine(turbineId);
			getMedidas6Kv().setTurbine(turbine);
			getMedidas6Kv().setProjectoId(turbine.getProject().getIdProject());
			getMedidas6Kv().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getMedidas6Kv());
		}

		getMedidas6Kv().setLocked("true");
		getMedidas6Kv().setPermission2Edit("false");
		setMedidas6Kv(getMedidas6Kv());
		File convfile = null;
		try {
			convfile = multipartToFile(file, file.getOriginalFilename());
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		try (PDDocument document = PDDocument.load(convfile)) {
			if (!populateAndCopy(document, typeReport)) {
				return null;
			}
		}
		if ("UPDATE".equals(operacao)) {
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldMedidas6Kv, getMedidas6Kv(), historicReport);
			historicReport.setListAlternation(listaAlternation);
			getMedidas6Kv().getListHistoric().add(historicReport);
			medidas6KvReturned = medidas6KvService.updateMedidas6Kv(getMedidas6Kv());
		} else {
			medidas6KvReturned = medidas6KvService.createMedidas6Kv(getMedidas6Kv());
		}
		return medidas6KvReturned;
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
					if (typeReport == Integer.valueOf(valueField)) {
						getMedidas6Kv().setTypeReport(Integer.valueOf(valueField));
					} else {
						return false;
					}
				}
				
				
				
				
				if (nameField.equals("additionalField1Label"))
					getMedidas6Kv().setAdditionalField1Label(valueField);
				if (nameField.equals("additionalField1Text"))
					getMedidas6Kv().setAdditionalField1Text(valueField);
				if (nameField.equals("additionalField2Label"))
					getMedidas6Kv().setAdditionalField2Label(valueField);
				if (nameField.equals("additionalField2Text"))
					getMedidas6Kv().setAdditionalField2Text(valueField);
				if (nameField.equals("additionalField3Label"))
					getMedidas6Kv().setAdditionalField3Label(valueField);
				if (nameField.equals("additionalField3Text"))
					getMedidas6Kv().setAdditionalField3Text(valueField);
				if (nameField.equals("additionalField4Label"))
					getMedidas6Kv().setAdditionalField4Label(valueField);
				if (nameField.equals("additionalField4Text"))
					getMedidas6Kv().setAdditionalField4Text(valueField);
				if (nameField.equals("additionalField5Label"))
					getMedidas6Kv().setAdditionalField5Label(valueField);
				if (nameField.equals("additionalField5Text"))
					getMedidas6Kv().setAdditionalField5Text(valueField);
				if (nameField.equals("additionalField6Label"))
					getMedidas6Kv().setAdditionalField6Label(valueField);
				if (nameField.equals("additionalField6Text"))
					getMedidas6Kv().setAdditionalField6Text(valueField);
				if (nameField.equals("additionalField7Label"))
					getMedidas6Kv().setAdditionalField7Label(valueField);
				if (nameField.equals("additionalField7Text"))
					getMedidas6Kv().setAdditionalField7Text(valueField);
				
				
				
				if (nameField.equals("dateOfMeasurement"))
					getMedidas6Kv().setDateOfMeasurement(valueField);
				if (nameField.equals("site"))
					getMedidas6Kv().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getMedidas6Kv().setWtgNumber(valueField);

				if (nameField.equals("voltage1"))
					getMedidas6Kv().setVoltage1(valueField);
				if (nameField.equals("length1"))
					getMedidas6Kv().setLength1(valueField);
				if (nameField.equals("visualInspection1"))
					getMedidas6Kv().setVisualInspection1(valueField);

				if (nameField.equals("equipmentType1"))
					getMedidas6Kv().setEquipmentType1(valueField);
				if (nameField.equals("serialNumber1"))
					getMedidas6Kv().setSerialNumber1(valueField);
				if (nameField.equals("calibrationDate1"))
					getMedidas6Kv().setCalibrationDate1(valueField);
				if (nameField.equals("nextCalibrationDate1"))
					getMedidas6Kv().setNextCalibrationDate1(valueField);

				if (nameField.equals("box1_1"))
					getMedidas6Kv().setBox1_1(valueField);
				if (nameField.equals("box1_2"))
					getMedidas6Kv().setBox1_2(valueField);
				if (nameField.equals("box1_3"))
					getMedidas6Kv().setBox1_3(valueField);
				if (nameField.equals("box1_4"))
					getMedidas6Kv().setBox1_4(valueField);
				if (nameField.equals("box1_5"))
					getMedidas6Kv().setBox1_5(valueField);
				if (nameField.equals("box1_6"))
					getMedidas6Kv().setBox1_6(valueField);
				if (nameField.equals("box1_7"))
					getMedidas6Kv().setBox1_7(valueField);
				if (nameField.equals("box1_8"))
					getMedidas6Kv().setBox1_8(valueField);
				if (nameField.equals("box1_9"))
					getMedidas6Kv().setBox1_9(valueField);
				if (nameField.equals("box1_10"))
					getMedidas6Kv().setBox1_10(valueField);
				if (nameField.equals("box1_11"))
					getMedidas6Kv().setBox1_11(valueField);
				if (nameField.equals("box1_12"))
					getMedidas6Kv().setBox1_12(valueField);
				if (nameField.equals("box1_13"))
					getMedidas6Kv().setBox1_13(valueField);
				if (nameField.equals("box1_14"))
					getMedidas6Kv().setBox1_14(valueField);
				if (nameField.equals("box1_15"))
					getMedidas6Kv().setBox1_15(valueField);
				if (nameField.equals("box1_16"))
					getMedidas6Kv().setBox1_16(valueField);
				if (nameField.equals("box1_17"))
					getMedidas6Kv().setBox1_17(valueField);
				if (nameField.equals("box1_18"))
					getMedidas6Kv().setBox1_18(valueField);

				if (nameField.equals("equipmentType2"))
					getMedidas6Kv().setEquipmentType2(valueField);
				if (nameField.equals("serialNumber2"))
					getMedidas6Kv().setSerialNumber2(valueField);
				if (nameField.equals("calibrationDate2"))
					getMedidas6Kv().setCalibrationDate2(valueField);
				if (nameField.equals("nextCalibrationDate2"))
					getMedidas6Kv().setNextCalibrationDate2(valueField);

				if (nameField.equals("box2_1"))
					getMedidas6Kv().setBox2_1(valueField);
				if (nameField.equals("box2_2"))
					getMedidas6Kv().setBox2_2(valueField);
				if (nameField.equals("box2_3"))
					getMedidas6Kv().setBox2_3(valueField);
				if (nameField.equals("box2_4"))
					getMedidas6Kv().setBox2_4(valueField);
				if (nameField.equals("box2_5"))
					getMedidas6Kv().setBox2_5(valueField);
				if (nameField.equals("box2_6"))
					getMedidas6Kv().setBox2_6(valueField);

				if (nameField.equals("box3_1"))
					getMedidas6Kv().setBox3_1(valueField);
				if (nameField.equals("box3_2"))
					getMedidas6Kv().setBox3_2(valueField);

				if (nameField.equals("type2"))
					getMedidas6Kv().setType2(valueField);
				if (nameField.equals("voltage2"))
					getMedidas6Kv().setVoltage2(valueField);
				if (nameField.equals("length2"))
					getMedidas6Kv().setLength2(valueField);
				if (nameField.equals("visualInspection2"))
					getMedidas6Kv().setVisualInspection2(valueField);

				if (nameField.equals("equipmentType3"))
					getMedidas6Kv().setEquipmentType3(valueField);
				if (nameField.equals("serialNumber3"))
					getMedidas6Kv().setSerialNumber3(valueField);
				if (nameField.equals("calibrationDate3"))
					getMedidas6Kv().setCalibrationDate3(valueField);
				if (nameField.equals("nextCalibrationDate3"))
					getMedidas6Kv().setNextCalibrationDate3(valueField);

				if (nameField.equals("box4_1"))
					getMedidas6Kv().setBox4_1(valueField);
				if (nameField.equals("box4_2"))
					getMedidas6Kv().setBox4_2(valueField);
				if (nameField.equals("box4_3"))
					getMedidas6Kv().setBox4_3(valueField);
				if (nameField.equals("box4_4"))
					getMedidas6Kv().setBox4_4(valueField);
				if (nameField.equals("box4_5"))
					getMedidas6Kv().setBox4_5(valueField);
				if (nameField.equals("box4_6"))
					getMedidas6Kv().setBox4_6(valueField);
				if (nameField.equals("box4_7"))
					getMedidas6Kv().setBox4_7(valueField);
				if (nameField.equals("box4_8"))
					getMedidas6Kv().setBox4_8(valueField);
				if (nameField.equals("box4_9"))
					getMedidas6Kv().setBox4_9(valueField);
				if (nameField.equals("box4_10"))
					getMedidas6Kv().setBox4_10(valueField);
				if (nameField.equals("box4_11"))
					getMedidas6Kv().setBox4_11(valueField);
				if (nameField.equals("box4_12"))
					getMedidas6Kv().setBox4_12(valueField);
				if (nameField.equals("box4_13"))
					getMedidas6Kv().setBox4_13(valueField);
				if (nameField.equals("box4_14"))
					getMedidas6Kv().setBox4_14(valueField);
				if (nameField.equals("box4_15"))
					getMedidas6Kv().setBox4_15(valueField);
				if (nameField.equals("box4_16"))
					getMedidas6Kv().setBox4_16(valueField);
				if (nameField.equals("box4_17"))
					getMedidas6Kv().setBox4_17(valueField);
				if (nameField.equals("box4_18"))
					getMedidas6Kv().setBox4_18(valueField);

				if (nameField.equals("equipmentType4"))
					getMedidas6Kv().setEquipmentType4(valueField);
				if (nameField.equals("serialNumber4"))
					getMedidas6Kv().setSerialNumber4(valueField);
				if (nameField.equals("calibrationDate4"))
					getMedidas6Kv().setCalibrationDate4(valueField);
				if (nameField.equals("nextCalibrationDate4"))
					getMedidas6Kv().setNextCalibrationDate4(valueField);

				if (nameField.equals("box5_1"))
					getMedidas6Kv().setBox5_1(valueField);
				if (nameField.equals("box5_2"))
					getMedidas6Kv().setBox5_2(valueField);
				if (nameField.equals("box5_3"))
					getMedidas6Kv().setBox5_3(valueField);
				if (nameField.equals("box5_4"))
					getMedidas6Kv().setBox5_4(valueField);
				if (nameField.equals("box5_5"))
					getMedidas6Kv().setBox5_5(valueField);
				if (nameField.equals("box5_6"))
					getMedidas6Kv().setBox5_6(valueField);

				if (nameField.equals("box6_1"))
					getMedidas6Kv().setBox6_1(valueField);
				if (nameField.equals("box6_2"))
					getMedidas6Kv().setBox6_2(valueField);

				if (nameField.equals("type3"))
					getMedidas6Kv().setType3(valueField);
				if (nameField.equals("voltage3"))
					getMedidas6Kv().setVoltage3(valueField);
				if (nameField.equals("length3"))
					getMedidas6Kv().setLength3(valueField);
				if (nameField.equals("visualInspection3"))
					getMedidas6Kv().setVisualInspection3(valueField);

				if (nameField.equals("equipmentType5"))
					getMedidas6Kv().setEquipmentType5(valueField);
				if (nameField.equals("serialNumber5"))
					getMedidas6Kv().setSerialNumber5(valueField);
				if (nameField.equals("calibrationDate5"))
					getMedidas6Kv().setCalibrationDate5(valueField);
				if (nameField.equals("nextCalibrationDate5"))
					getMedidas6Kv().setNextCalibrationDate5(valueField);

				if (nameField.equals("box7_1"))
					getMedidas6Kv().setBox7_1(valueField);
				if (nameField.equals("box7_2"))
					getMedidas6Kv().setBox7_2(valueField);
				if (nameField.equals("box7_3"))
					getMedidas6Kv().setBox7_3(valueField);
				if (nameField.equals("box7_4"))
					getMedidas6Kv().setBox7_4(valueField);
				if (nameField.equals("box7_5"))
					getMedidas6Kv().setBox7_5(valueField);
				if (nameField.equals("box7_6"))
					getMedidas6Kv().setBox7_6(valueField);
				if (nameField.equals("box7_7"))
					getMedidas6Kv().setBox7_7(valueField);
				if (nameField.equals("box7_8"))
					getMedidas6Kv().setBox7_8(valueField);
				if (nameField.equals("box7_9"))
					getMedidas6Kv().setBox7_9(valueField);
				if (nameField.equals("box7_10"))
					getMedidas6Kv().setBox7_10(valueField);
				if (nameField.equals("box7_11"))
					getMedidas6Kv().setBox7_11(valueField);
				if (nameField.equals("box7_12"))
					getMedidas6Kv().setBox7_12(valueField);
				if (nameField.equals("box7_13"))
					getMedidas6Kv().setBox7_13(valueField);
				if (nameField.equals("box7_14"))
					getMedidas6Kv().setBox7_14(valueField);
				if (nameField.equals("box7_15"))
					getMedidas6Kv().setBox7_15(valueField);
				if (nameField.equals("box7_16"))
					getMedidas6Kv().setBox7_16(valueField);
				if (nameField.equals("box7_17"))
					getMedidas6Kv().setBox7_17(valueField);
				if (nameField.equals("box7_18"))
					getMedidas6Kv().setBox7_18(valueField);

				if (nameField.equals("equipmentType6"))
					getMedidas6Kv().setEquipmentType6(valueField);
				if (nameField.equals("serialNumber6"))
					getMedidas6Kv().setSerialNumber6(valueField);
				if (nameField.equals("calibrationDate6"))
					getMedidas6Kv().setCalibrationDate6(valueField);
				if (nameField.equals("nextCalibrationDate6"))
					getMedidas6Kv().setNextCalibrationDate6(valueField);

				if (nameField.equals("box8_1"))
					getMedidas6Kv().setBox8_1(valueField);
				if (nameField.equals("box8_2"))
					getMedidas6Kv().setBox8_2(valueField);
				if (nameField.equals("box8_3"))
					getMedidas6Kv().setBox8_3(valueField);
				if (nameField.equals("box8_4"))
					getMedidas6Kv().setBox8_4(valueField);
				if (nameField.equals("box8_5"))
					getMedidas6Kv().setBox8_5(valueField);
				if (nameField.equals("box8_6"))
					getMedidas6Kv().setBox8_6(valueField);

				if (nameField.equals("box9_1"))
					getMedidas6Kv().setBox9_1(valueField);
				if (nameField.equals("box9_2"))
					getMedidas6Kv().setBox9_2(valueField);

				if (nameField.equals("conclusion"))
					getMedidas6Kv().setConclusion(valueField);
				if (nameField.equals("performedBy"))
					getMedidas6Kv().setPerformedBy(valueField);
				if (nameField.equals("closedDate"))
					getMedidas6Kv().setClosedDate(valueField);
				if (nameField.contains("description") && imageInsertion) {
					setNameField(nameField);
					setDescription(valueField);
				}
			} else if (field instanceof PDCheckBox) {

				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();

				if (nameField.equals("chk1"))
					getMedidas6Kv().setChk1(valueField == "Yes" ? true : false);
				if (nameField.equals("chk2"))
					getMedidas6Kv().setChk2(valueField == "Yes" ? true : false);
				if (nameField.equals("chk3"))
					getMedidas6Kv().setChk3(valueField == "Yes" ? true : false);
				if (nameField.equals("insertImagesChk")) {
					getMedidas6Kv().setInsertImagesChk(valueField);
					if ("Yes".equals(valueField)) {
						imageInsertion = true;
					} else if ("Off".equals(valueField)) {
						imageInsertion = false;
					}
				}
			} else if (field instanceof PDRadioButton) {

				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDRadioButton) field).getValue();

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
								medidas6Kv.addImgOnListImages(fileData);
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
		List<FileData> listFileData = fileService.readFile(getMedidas6Kv().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getMedidas6Kv().getUuid());
			fileData.setCreateDate(getMedidas6Kv().getCreateDate());
			fileData.setModifiedDate(getMedidas6Kv().getModifiedDate());
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
			fileData.setReport(getMedidas6Kv());
			getMedidas6Kv().getListaFileData().add(fileData);
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

	public Medidas6Kv getMedidas6Kv() {
		return medidas6Kv;
	}

	public void setMedidas6Kv(Medidas6Kv medidas6Kv) {
		this.medidas6Kv = medidas6Kv;
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
