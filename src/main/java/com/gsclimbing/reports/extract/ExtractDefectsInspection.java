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
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.AlterationRepository;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDefectsInspection {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;

	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;
	
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

	@Autowired
	private AlterationRepository alterationRepository;

	private DefectsInspectionReport defectsInspectionReport;

	List<String> listPhotoNames = new ArrayList<String>();
	
	

	public DefectsInspectionReport readPDF(MultipartFile file, String projectId, Integer turbineId, Integer idReport, String operacao) throws IOException {
		DefectsInspectionReport oldDefectsInspectionReport = null;
		DefectsInspectionReport defectsInspectionReportReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldDefectsInspectionReport = defectsInspectionReportService.readDefectsInspectionReport(idReport);
			if (oldDefectsInspectionReport != null) {
				try {
					defectsInspectionReport = (DefectsInspectionReport) oldDefectsInspectionReport.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				defectsInspectionReport.setModifiedDate(LocalDateTime.now());
				defectsInspectionReport.setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(1);
				historicReport.setLocalDateTime(LocalDateTime.now());
	     		historicReport.setIdProject(String.valueOf(defectsInspectionReport.getReportId()));
				historicReport.setNumAlterations(0);
				String username = defectsInspectionReportService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setDefectInspectionReport(defectsInspectionReport);
			}
		} else if ("UPLOAD".equals(operacao)) {
			defectsInspectionReport = new DefectsInspectionReport();
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			defectsInspectionReport.setUuid(uuid);
			defectsInspectionReport.setCreateDate(LocalDateTime.now());
			defectsInspectionReport.setModifiedDate(LocalDateTime.now());
			Turbine turbine = turbineService.getTurbine(turbineId);
			defectsInspectionReport.setTurbine( turbine );
			defectsInspectionReport.setProjectozinhoId(turbine.getProject().getIdProject());
			defectsInspectionReport.setTurbinazinhaId(turbine.getId());
		}

		defectsInspectionReport.setLocked("true");
		defectsInspectionReport.setPermission2Edit("false");
		setDefectsInspectionReport(defectsInspectionReport);
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
			List<Alteration> listaAlternation = alterationService.saveAlterationDefectsInspectionReport(oldDefectsInspectionReport, defectsInspectionReport, historicReport);
			historicReport.setListAlternation(listaAlternation);
			defectsInspectionReport.getListHistoric().add(historicReport);
			defectsInspectionReportReturned = defectsInspectionReportService.updateDefectsInspectionReport(defectsInspectionReport);
		} else {
			defectsInspectionReportReturned = defectsInspectionReportService.createDefectsInspectionReport(getDefectsInspectionReport());
		}
		return defectsInspectionReportReturned;
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
					getDefectsInspectionReport().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getDefectsInspectionReport().setWtgNumber(valueField);
				if (nameField.equals("wtgType"))
					getDefectsInspectionReport().setWtgType(valueField);
				if (nameField.equals("yearConstruction"))
					getDefectsInspectionReport().setYearConstruction(valueField);
				if (nameField.contains("Description")) {
					setNameField(nameField);
					setDescription(valueField);
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
							defectsInspectionReport.addImgOnListImages(fileData);
							defectsInspectionReport.addOneMorePicture();
							extractAnnotationImages(pDimage, nameField, fileData);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void extractAnnotationImages(PDImage image, String nameFile, FileData fileData) throws IOException {

		List<FileData> listFileData = fileService.readFile(defectsInspectionReport.getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();

		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getDefectsInspectionReport().getUuid());
			fileData.setCreateDate(getDefectsInspectionReport().getCreateDate());
			fileData.setModifiedDate(getDefectsInspectionReport().getModifiedDate());
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

			fileService.createFile(fileData);

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


	public long fileSize(File file) {
		long bytes = file.length();
		long kilobytes = (bytes / 1024);
		return kilobytes;
	}

}
