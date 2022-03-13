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
import com.gsclimbing.database.entity.Medidas690V400V;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.Medidas690V400VService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataMedidas690V400V {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	List<String> listPhotoNames = new ArrayList<String>();
	
	private Medidas690V400V medidas690V400V;
	 
	@Autowired
	private UserRepository userService;
	@Autowired
	private TurbineService turbineService;
	@Autowired
	private AlterationService alterationService;
	@Autowired
	private HistoricReportService historicReportService;
	@Autowired
	private Medidas690V400VService medidas690V400VService;
	@Autowired
	private com.gsclimbing.database.service.FileService fileService;

	public Medidas690V400V readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport,  String operacao) throws IOException {
		Medidas690V400V oldMedidas690V400 = null;
		Medidas690V400V medidas690V400Returned = null;
		if ("UPDATE".equals(operacao)) {
			oldMedidas690V400 = medidas690V400VService.readMedidas690V400V(idReport);
			if (oldMedidas690V400 != null) {
				try {
					setMedidas690V400V((Medidas690V400V) oldMedidas690V400.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getMedidas690V400V().setModifiedDate(LocalDateTime.now());
				getMedidas690V400V().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.M690V400V.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = medidas690V400VService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getMedidas690V400V());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setMedidas690V400V(new Medidas690V400V());
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getMedidas690V400V().setUuid(uuid);
			getMedidas690V400V().setCreateDate(LocalDateTime.now());
			getMedidas690V400V().setModifiedDate(LocalDateTime.now());
			
			Turbine turbine = turbineService.getTurbine(turbineId);
			getMedidas690V400V().setTurbine(turbine);
			getMedidas690V400V().setProjectoId(turbine.getProject().getIdProject());
			getMedidas690V400V().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getMedidas690V400V());
		}

		getMedidas690V400V().setLocked("true");
		getMedidas690V400V().setPermission2Edit("false");
		setMedidas690V400V(getMedidas690V400V());
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
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldMedidas690V400, medidas690V400V, historicReport);
			historicReport.setListAlternation(listaAlternation);
			getMedidas690V400V().getListHistoric().add(historicReport);
			medidas690V400Returned = medidas690V400VService.updateMedidas690V400V(getMedidas690V400V());
		} else {
			medidas690V400Returned = medidas690V400VService.createMedidas690V400V(getMedidas690V400V());
		}
		return medidas690V400Returned;
	}

	private boolean populateAndCopy(PDDocument document, Integer typeReport) throws IOException {
		getListPhotoNames().clear();
		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		List<PDField> fields = acroForm.getFields();
		for (PDField field : fields) {
			if (field instanceof PDTextField) {
				String valueField = ((PDTextField) field).getValue();
				String nameField = field.getFullyQualifiedName();
				if (nameField.equals("typeReport")) {
					if(typeReport == Integer.valueOf(valueField)) {
						getMedidas690V400V().setTypeReport(Integer.valueOf(valueField));
					}else {
						return false;
					}
				}
				if (nameField.equals("dateOfMeasurement"))getMedidas690V400V().setDateOfMeasurement(valueField);;
				if (nameField.equals("site"))getMedidas690V400V().setSite(valueField);
				if (nameField.equals("wtgNumber"))getMedidas690V400V().setWtgNumber(valueField);
				if (nameField.equals("type1"))getMedidas690V400V().setType1(valueField);
				if (nameField.equals("voltage1"))getMedidas690V400V().setVoltage1(valueField);
				if (nameField.equals("length1"))getMedidas690V400V().setLength1(valueField);
				if (nameField.equals("visual1"))getMedidas690V400V().setVisual1(valueField);
				if (nameField.equals("box1_1"))getMedidas690V400V().setBox1_1(valueField);
				if (nameField.equals("box1_2"))getMedidas690V400V().setBox1_2(valueField);
				if (nameField.equals("box1_3"))getMedidas690V400V().setBox1_3(valueField);
				if (nameField.equals("box1_4"))getMedidas690V400V().setBox1_4(valueField);
				if (nameField.equals("box1_5"))getMedidas690V400V().setBox1_5(valueField);
				if (nameField.equals("box1_6"))getMedidas690V400V().setBox1_6(valueField);
				if (nameField.equals("box1_7"))getMedidas690V400V().setBox1_7(valueField);
				if (nameField.equals("box1_8"))getMedidas690V400V().setBox1_8(valueField);
				if (nameField.equals("box1_9"))getMedidas690V400V().setBox1_9(valueField);
				if (nameField.equals("type2"))getMedidas690V400V().setType2(valueField);
				if (nameField.equals("voltage2"))getMedidas690V400V().setVoltage2(valueField);
				if (nameField.equals("length2"))getMedidas690V400V().setLength2(valueField);
				if (nameField.equals("visual2"))getMedidas690V400V().setVisual2(valueField);
				if (nameField.equals("box2_1"))getMedidas690V400V().setBox2_1(valueField);
				if (nameField.equals("box2_2"))getMedidas690V400V().setBox2_2(valueField);
				if (nameField.equals("box2_3"))getMedidas690V400V().setBox2_3(valueField);
				if (nameField.equals("box2_4"))getMedidas690V400V().setBox2_4(valueField);
				if (nameField.equals("box2_5"))getMedidas690V400V().setBox2_5(valueField);
				if (nameField.equals("box2_6"))getMedidas690V400V().setBox2_6(valueField);
				if (nameField.equals("box2_7"))getMedidas690V400V().setBox2_7(valueField);
				if (nameField.equals("box2_8"))getMedidas690V400V().setBox2_8(valueField);
				if (nameField.equals("box2_9"))getMedidas690V400V().setBox2_9(valueField);
				if (nameField.equals("type3"))getMedidas690V400V().setType3(valueField);
				if (nameField.equals("voltage3"))getMedidas690V400V().setVoltage3(valueField);
				if (nameField.equals("length3"))getMedidas690V400V().setLength3(valueField);
				if (nameField.equals("visual3"))getMedidas690V400V().setVisual3(valueField);
				if (nameField.equals("box3_1"))getMedidas690V400V().setBox3_1(valueField);
				if (nameField.equals("box3_2"))getMedidas690V400V().setBox3_2(valueField);
				if (nameField.equals("box3_3"))getMedidas690V400V().setBox3_3(valueField);
				if (nameField.equals("box3_4"))getMedidas690V400V().setBox3_4(valueField);
				if (nameField.equals("box3_5"))getMedidas690V400V().setBox3_5(valueField);
				if (nameField.equals("box3_6"))getMedidas690V400V().setBox3_6(valueField);
				if (nameField.equals("box3_7"))getMedidas690V400V().setBox3_7(valueField);
				if (nameField.equals("box3_8"))getMedidas690V400V().setBox3_8(valueField);
				if (nameField.equals("box3_9"))getMedidas690V400V().setBox3_9(valueField);
				if (nameField.equals("type4"))getMedidas690V400V().setType4(valueField);
				if (nameField.equals("voltage4"))getMedidas690V400V().setVoltage4(valueField);
				if (nameField.equals("length4"))getMedidas690V400V().setLength4(valueField);
				if (nameField.equals("visual4"))getMedidas690V400V().setVisual4(valueField);
				if (nameField.equals("box4_1"))getMedidas690V400V().setBox4_1(valueField);
				if (nameField.equals("box4_2"))getMedidas690V400V().setBox4_2(valueField);
				if (nameField.equals("box4_3"))getMedidas690V400V().setBox4_3(valueField);
				if (nameField.equals("box4_4"))getMedidas690V400V().setBox4_4(valueField);
				if (nameField.equals("box4_5"))getMedidas690V400V().setBox4_5(valueField);
				if (nameField.equals("box4_6"))getMedidas690V400V().setBox4_6(valueField);
				if (nameField.equals("box4_7"))getMedidas690V400V().setBox4_7(valueField);
				if (nameField.equals("box4_8"))getMedidas690V400V().setBox4_8(valueField);
				if (nameField.equals("box4_9"))getMedidas690V400V().setBox4_9(valueField);
				if (nameField.equals("type5"))getMedidas690V400V().setType5(valueField);
				if (nameField.equals("voltage5"))getMedidas690V400V().setVoltage5(valueField);
				if (nameField.equals("length5"))getMedidas690V400V().setLength5(valueField);
				if (nameField.equals("visual5"))getMedidas690V400V().setVisual5(valueField);
				if (nameField.equals("box5_1"))getMedidas690V400V().setBox5_1(valueField);
				if (nameField.equals("box5_2"))getMedidas690V400V().setBox5_2(valueField);
				if (nameField.equals("box5_3"))getMedidas690V400V().setBox5_3(valueField);
				if (nameField.equals("box5_4"))getMedidas690V400V().setBox5_4(valueField);
				if (nameField.equals("box5_5"))getMedidas690V400V().setBox5_5(valueField);
				if (nameField.equals("box5_6"))getMedidas690V400V().setBox5_6(valueField);
				if (nameField.equals("box5_7"))getMedidas690V400V().setBox5_7(valueField);
				if (nameField.equals("box5_8"))getMedidas690V400V().setBox5_8(valueField);
				if (nameField.equals("box5_9"))getMedidas690V400V().setBox5_9(valueField);
				if (nameField.equals("box5_10"))getMedidas690V400V().setBox5_10(valueField);
				if (nameField.equals("box5_11"))getMedidas690V400V().setBox5_11(valueField);
				if (nameField.equals("box5_12"))getMedidas690V400V().setBox5_12(valueField);
				if (nameField.equals("box6_1"))getMedidas690V400V().setBox6_1(valueField);
				if (nameField.equals("box6_2"))getMedidas690V400V().setBox6_2(valueField);
				if (nameField.equals("box6_3"))getMedidas690V400V().setBox6_3(valueField);
				if (nameField.equals("box6_4"))getMedidas690V400V().setBox6_4(valueField);
				if (nameField.equals("box6_5"))getMedidas690V400V().setBox6_5(valueField);
				if (nameField.equals("box6_6"))getMedidas690V400V().setBox6_6(valueField);
				if (nameField.equals("box6_7"))getMedidas690V400V().setBox6_7(valueField);
				if (nameField.equals("box6_8"))getMedidas690V400V().setBox6_8(valueField);
				if (nameField.equals("box6_9"))getMedidas690V400V().setBox6_9(valueField);
				if (nameField.equals("box6_10"))getMedidas690V400V().setBox6_10(valueField);
				if (nameField.equals("box6_11"))getMedidas690V400V().setBox6_11(valueField);
				if (nameField.equals("box6_12"))getMedidas690V400V().setBox6_12(valueField);
				if (nameField.equals("box6_13"))getMedidas690V400V().setBox6_13(valueField);
				if (nameField.equals("box6_14"))getMedidas690V400V().setBox6_14(valueField);
				if (nameField.equals("box6_15"))getMedidas690V400V().setBox6_15(valueField);
				if (nameField.equals("box6_16"))getMedidas690V400V().setBox6_16(valueField);
				if (nameField.equals("box6_17"))getMedidas690V400V().setBox6_17(valueField);
				if (nameField.equals("box6_18"))getMedidas690V400V().setBox6_18(valueField);
				if (nameField.equals("box6_19"))getMedidas690V400V().setBox6_19(valueField);
				if (nameField.equals("box6_20"))getMedidas690V400V().setBox6_20(valueField);
				if (nameField.equals("box6_21"))getMedidas690V400V().setBox6_21(valueField);
				if (nameField.equals("box6_22"))getMedidas690V400V().setBox6_22(valueField);
				if (nameField.equals("box6_23"))getMedidas690V400V().setBox6_23(valueField);
				if (nameField.equals("box6_24"))getMedidas690V400V().setBox6_24(valueField);
				if (nameField.equals("type6"))getMedidas690V400V().setType6(valueField);
				if (nameField.equals("voltage6"))getMedidas690V400V().setVoltage6(valueField);
				if (nameField.equals("visual6"))getMedidas690V400V().setVisual6(valueField);
				if (nameField.equals("box7_1"))getMedidas690V400V().setBox7_1(valueField);
				if (nameField.equals("box7_2"))getMedidas690V400V().setBox7_2(valueField);
				if (nameField.equals("box7_3"))getMedidas690V400V().setBox7_3(valueField);
				if (nameField.equals("box7_4"))getMedidas690V400V().setBox7_4(valueField);
				if (nameField.equals("box7_5"))getMedidas690V400V().setBox7_5(valueField);
				if (nameField.equals("box7_6"))getMedidas690V400V().setBox7_6(valueField);
				if (nameField.equals("box7_7"))getMedidas690V400V().setBox7_7(valueField);
				if (nameField.equals("box7_8"))getMedidas690V400V().setBox7_8(valueField);
				if (nameField.equals("box7_9"))getMedidas690V400V().setBox7_9(valueField);
				if (nameField.equals("box7_10"))getMedidas690V400V().setBox7_10(valueField);
				if (nameField.equals("box7_11"))getMedidas690V400V().setBox7_11(valueField);
				if (nameField.equals("box7_12"))getMedidas690V400V().setBox7_12(valueField);
				if (nameField.equals("box7_13"))getMedidas690V400V().setBox7_13(valueField);
				if (nameField.equals("box7_14"))getMedidas690V400V().setBox7_14(valueField);
				if (nameField.equals("box7_15"))getMedidas690V400V().setBox7_15(valueField);
				if (nameField.equals("box7_16"))getMedidas690V400V().setBox7_16(valueField);
				if (nameField.equals("box7_17"))getMedidas690V400V().setBox7_17(valueField);
				if (nameField.equals("box7_18"))getMedidas690V400V().setBox7_18(valueField);
				if (nameField.equals("box7_19"))getMedidas690V400V().setBox7_19(valueField);
				if (nameField.equals("box7_20"))getMedidas690V400V().setBox7_20(valueField);
				if (nameField.equals("box7_21"))getMedidas690V400V().setBox7_21(valueField);
				if (nameField.equals("box7_22"))getMedidas690V400V().setBox7_22(valueField);
				if (nameField.equals("box7_23"))getMedidas690V400V().setBox7_23(valueField);
				if (nameField.equals("box7_24"))getMedidas690V400V().setBox7_24(valueField);
				if (nameField.equals("box7_25"))getMedidas690V400V().setBox7_25(valueField);
				if (nameField.equals("box7_26"))getMedidas690V400V().setBox7_26(valueField);
				if (nameField.equals("box7_27"))getMedidas690V400V().setBox7_27(valueField);
				if (nameField.equals("equipmentType"))getMedidas690V400V().setEquipmentType(valueField);
				if (nameField.equals("serialNumber"))getMedidas690V400V().setSerialNumber(valueField);
				if (nameField.equals("calibrationDate"))getMedidas690V400V().setCalibrationDate(valueField);
				if (nameField.equals("nextCalibrationDate"))getMedidas690V400V().setNextCalibrationDate(valueField);
				if (nameField.equals("conclusion"))getMedidas690V400V().setConclusion(valueField);
				if (nameField.equals("performedBy"))getMedidas690V400V().setPerformedBy(valueField);
				if (nameField.equals("closedDate"))getMedidas690V400V().setClosedDate(valueField);
			} else if (field instanceof PDCheckBox) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();
			} else if (field instanceof PDRadioButton) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDRadioButton) field).getValue();
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
							medidas690V400V.addImgOnListImages(fileData);
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
		List<FileData> listFileData = fileService.readFile(getMedidas690V400V().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getMedidas690V400V().getUuid());
			fileData.setCreateDate(getMedidas690V400V().getCreateDate());
			fileData.setModifiedDate(getMedidas690V400V().getModifiedDate());
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
			fileData.setReport(getMedidas690V400V());
			getMedidas690V400V().getListaFileData().add(fileData);
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

	public Medidas690V400V getMedidas690V400V() {
		return medidas690V400V;
	}

	public void setMedidas690V400V(Medidas690V400V medidas690V400V) {
		this.medidas690V400V = medidas690V400V;
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
