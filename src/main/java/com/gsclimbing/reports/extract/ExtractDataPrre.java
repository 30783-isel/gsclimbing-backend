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
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.PerformanceReportRepairElevatorService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataPrre {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	private PerformanceReportRepairElevator performanceReportRepairElevator;
	List<String> listPhotoNames = new ArrayList<String>();

	@Autowired
	private PerformanceReportRepairElevatorService performanceReportRepairElevatorService;
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

	public PerformanceReportRepairElevator readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		PerformanceReportRepairElevator oldPerformanceReportRepairElevator = null;
		PerformanceReportRepairElevator performanceReportRepairElevatorReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldPerformanceReportRepairElevator = performanceReportRepairElevatorService.readPerformanceReportRepairElevator(idReport);
			if (oldPerformanceReportRepairElevator != null) {
				try {
					setPerformanceReportRepairElevator((PerformanceReportRepairElevator) oldPerformanceReportRepairElevator.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getPerformanceReportRepairElevator().setModifiedDate(LocalDateTime.now());
				getPerformanceReportRepairElevator().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.PRRE.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = performanceReportRepairElevatorService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getPerformanceReportRepairElevator());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setPerformanceReportRepairElevator(new PerformanceReportRepairElevator());
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getPerformanceReportRepairElevator().setUuid(uuid);
			getPerformanceReportRepairElevator().setCreateDate(LocalDateTime.now());
			getPerformanceReportRepairElevator().setModifiedDate(LocalDateTime.now());
			Turbine turbine = turbineService.getTurbine(turbineId);
			getPerformanceReportRepairElevator().setTurbine(turbine);
			getPerformanceReportRepairElevator().setProjectoId(turbine.getProject().getIdProject());
			getPerformanceReportRepairElevator().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getPerformanceReportRepairElevator());
		}
		getPerformanceReportRepairElevator().setLocked("true");
		getPerformanceReportRepairElevator().setPermission2Edit("false");
		setPerformanceReportRepairElevator(getPerformanceReportRepairElevator());
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
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldPerformanceReportRepairElevator, getPerformanceReportRepairElevator(), historicReport);
			historicReport.setListAlternation(listaAlternation);
			getPerformanceReportRepairElevator().getListHistoric().add(historicReport);
			performanceReportRepairElevatorReturned = performanceReportRepairElevatorService.updatePerformanceReportRepairElevator(getPerformanceReportRepairElevator());
		} else {
			performanceReportRepairElevatorReturned = performanceReportRepairElevatorService.createPerformanceReportRepairElevator(getPerformanceReportRepairElevator());
		}
		return performanceReportRepairElevatorReturned;
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
					if (typeReport == Integer.valueOf(valueField)) {
						getPerformanceReportRepairElevator().setTypeReport(Integer.valueOf(valueField));
					} else {
						return false;
					}
				}
				if (nameField.equals("reportNumber"))
					getPerformanceReportRepairElevator().setReportNumber(valueField);
				if (nameField.equals("site"))
					getPerformanceReportRepairElevator().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getPerformanceReportRepairElevator().setWtgNumber(valueField);
				if (nameField.equals("wtgType"))
					getPerformanceReportRepairElevator().setWtgType(valueField);
				if (nameField.equals("performanceReport"))
					getPerformanceReportRepairElevator().setPerformanceReport(valueField);
				if (nameField.equals("inpectorsWorkers"))
					getPerformanceReportRepairElevator().setInpectorsWorkers(valueField);
				if (nameField.equals("statementOfwork"))
					getPerformanceReportRepairElevator().setStatementOfwork(valueField);
				if (nameField.equals("placeDate"))
					getPerformanceReportRepairElevator().setPlaceDate(valueField);
				if (nameField.equals("responsibleTechnician"))
					getPerformanceReportRepairElevator().setResponsibleTechnician(valueField);
			} else if (field instanceof PDCheckBox) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();
				if (nameField.equals("workCompletedYes"))
					getPerformanceReportRepairElevator().setWorkCompletedYes(valueField == "Yes" ? true : false);
				if (nameField.equals("workCompletedNo"))
					getPerformanceReportRepairElevator().setWorkCompletedNo(valueField == "Yes" ? true : false);
				if (nameField.equals("turbineOperableYes"))
					getPerformanceReportRepairElevator().setTurbineOperableYes(valueField == "Yes" ? true : false);
				if (nameField.equals("turbineOperableNo"))
					getPerformanceReportRepairElevator().setTurbineOperableNo(valueField == "Yes" ? true : false);
				if (nameField.equals("turbineOperableLimited"))
					getPerformanceReportRepairElevator().setTurbineOperableLimited(valueField == "Yes" ? true : false);
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
							performanceReportRepairElevator.addImgOnListImages(fileData);
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
		List<FileData> listFileData = fileService.readFile(getPerformanceReportRepairElevator().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getPerformanceReportRepairElevator().getUuid());
			fileData.setCreateDate(getPerformanceReportRepairElevator().getCreateDate());
			fileData.setModifiedDate(getPerformanceReportRepairElevator().getModifiedDate());
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
			fileData.setReport(getPerformanceReportRepairElevator());
			getPerformanceReportRepairElevator().getListaFileData().add(fileData);
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

	public PerformanceReportRepairElevator getPerformanceReportRepairElevator() {
		return performanceReportRepairElevator;
	}

	public void setPerformanceReportRepairElevator(PerformanceReportRepairElevator performanceReportRepairElevator) {
		this.performanceReportRepairElevator = performanceReportRepairElevator;
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
