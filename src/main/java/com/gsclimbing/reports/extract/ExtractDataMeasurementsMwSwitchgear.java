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
import com.gsclimbing.database.entity.MeasurementsMwSwitchgear;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.AlterationService;
import com.gsclimbing.database.service.HistoricReportService;
import com.gsclimbing.database.service.MeasurementsMwSwitchgearService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.ftp.FTPUploadFile;

import lombok.Data;

@Data
@Service
public class ExtractDataMeasurementsMwSwitchgear {

	private String uuidStr;
	private String description = null;
	private String nameField = null;
	private String photo = null;
	private String idHistoric = null;
	private HistoricReport historicReport = null;
	List<String> listPhotoNames = new ArrayList<String>();
	private MeasurementsMwSwitchgear measurementsMwSwitchgear;

	@Autowired
	private UserRepository userService;
	@Autowired
	private TurbineService turbineService;
	@Autowired
	private AlterationService alterationService;
	@Autowired
	private HistoricReportService historicReportService;
	@Autowired
	private MeasurementsMwSwitchgearService measurementsMwSwitchgearService;
	@Autowired
	private com.gsclimbing.database.service.FileService fileService;

	public MeasurementsMwSwitchgear readPDF(MultipartFile file, String projectId, Integer turbineId, Integer typeReport, Integer idReport, String operacao) throws IOException {
		MeasurementsMwSwitchgear oldMeasurementsMwSwitchgear = null;
		MeasurementsMwSwitchgear measurementsMwSwitchgearReturned = null;
		if ("UPDATE".equals(operacao)) {
			oldMeasurementsMwSwitchgear = measurementsMwSwitchgearService.readMeasurementsMwSwitchgear(idReport);
			if (oldMeasurementsMwSwitchgear != null) {
				try {
					setMeasurementsMwSwitchgear((MeasurementsMwSwitchgear) oldMeasurementsMwSwitchgear.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				getMeasurementsMwSwitchgear().setModifiedDate(LocalDateTime.now());
				getMeasurementsMwSwitchgear().setLocked("true");
				historicReport = new HistoricReport();
				historicReport.setTypeReport(ReportEnum.MMSSC.ordinal());
				historicReport.setLocalDateTime(LocalDateTime.now());
				historicReport.setNumAlterations(0);
				String username = measurementsMwSwitchgearService.getCurrentLoggedUser();
				Optional<User> user = userService.findByUsername(username);
				historicReport.setIdUser(user.get().getUsername());
				historicReport.setUser(user.get().getUsername());
				historicReport.setReport(getMeasurementsMwSwitchgear());
			}
		} else if ("UPLOAD".equals(operacao)) {
			setMeasurementsMwSwitchgear(new MeasurementsMwSwitchgear());
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			setUuidStr(uuid);
			getMeasurementsMwSwitchgear().setUuid(uuid);
			getMeasurementsMwSwitchgear().setCreateDate(LocalDateTime.now());
			getMeasurementsMwSwitchgear().setModifiedDate(LocalDateTime.now());

			Turbine turbine = turbineService.getTurbine(turbineId);
			getMeasurementsMwSwitchgear().setTurbine(turbine);
			getMeasurementsMwSwitchgear().setProjectoId(turbine.getProject().getIdProject());
			getMeasurementsMwSwitchgear().setTurbinaId(turbine.getId());
			turbine.getListReports().add(getMeasurementsMwSwitchgear());
		}

		getMeasurementsMwSwitchgear().setLocked("true");
		getMeasurementsMwSwitchgear().setPermission2Edit("false");
		setMeasurementsMwSwitchgear(getMeasurementsMwSwitchgear());
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
			List<Alteration> listaAlternation = alterationService.saveAlterationReport(oldMeasurementsMwSwitchgear, getMeasurementsMwSwitchgear(), historicReport);
			historicReport.setListAlternation(listaAlternation);
			getMeasurementsMwSwitchgear().getListHistoric().add(historicReport);
			measurementsMwSwitchgearReturned = measurementsMwSwitchgearService.updateMeasurementsMwSwitchgear(getMeasurementsMwSwitchgear());
		} else {
			measurementsMwSwitchgearReturned = measurementsMwSwitchgearService.createMeasurementsMwSwitchgear(getMeasurementsMwSwitchgear());
		}
		return measurementsMwSwitchgearReturned;
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
						getMeasurementsMwSwitchgear().setTypeReport(Integer.valueOf(valueField));
					} else {
						return false;
					}
				}
				
				if (nameField.equals("additionalField1Label"))
					getMeasurementsMwSwitchgear().setAdditionalField1Label(valueField);
				if (nameField.equals("additionalField1Text"))
					getMeasurementsMwSwitchgear().setAdditionalField1Text(valueField);
				if (nameField.equals("additionalField2Label"))
					getMeasurementsMwSwitchgear().setAdditionalField2Label(valueField);
				if (nameField.equals("additionalField2Text"))
					getMeasurementsMwSwitchgear().setAdditionalField2Text(valueField);
				if (nameField.equals("additionalField3Label"))
					getMeasurementsMwSwitchgear().setAdditionalField3Label(valueField);
				if (nameField.equals("additionalField3Text"))
					getMeasurementsMwSwitchgear().setAdditionalField3Text(valueField);
				if (nameField.equals("additionalField4Label"))
					getMeasurementsMwSwitchgear().setAdditionalField4Label(valueField);
				if (nameField.equals("additionalField4Text"))
					getMeasurementsMwSwitchgear().setAdditionalField4Text(valueField);
				if (nameField.equals("additionalField5Label"))
					getMeasurementsMwSwitchgear().setAdditionalField5Label(valueField);
				if (nameField.equals("additionalField5Text"))
					getMeasurementsMwSwitchgear().setAdditionalField5Text(valueField);
				if (nameField.equals("additionalField6Label"))
					getMeasurementsMwSwitchgear().setAdditionalField6Label(valueField);
				if (nameField.equals("additionalField6Text"))
					getMeasurementsMwSwitchgear().setAdditionalField6Text(valueField);
				if (nameField.equals("additionalField7Label"))
					getMeasurementsMwSwitchgear().setAdditionalField7Label(valueField);
				if (nameField.equals("additionalField7Text"))
					getMeasurementsMwSwitchgear().setAdditionalField7Text(valueField);
				
				if (nameField.equals("manufacturerDate"))
					getMeasurementsMwSwitchgear().setManufacturerDate(valueField);
				if (nameField.equals("dateMeasurement"))
					getMeasurementsMwSwitchgear().setDateMeasurement(valueField);
				if (nameField.equals("site"))
					getMeasurementsMwSwitchgear().setSite(valueField);
				if (nameField.equals("wtgNumber"))
					getMeasurementsMwSwitchgear().setWtgNumber(valueField);
				if (nameField.equals("type"))
					getMeasurementsMwSwitchgear().setType(valueField);
				if (nameField.equals("serialNumber"))
					getMeasurementsMwSwitchgear().setSerialNumber(valueField);
				if (nameField.equals("equipamentType1"))
					getMeasurementsMwSwitchgear().setEquipamentType1(valueField);
				if (nameField.equals("serialNumber1"))
					getMeasurementsMwSwitchgear().setSerialNumber1(valueField);
				if (nameField.equals("calibrationDate1"))
					getMeasurementsMwSwitchgear().setCalibrationDate1(valueField);
				if (nameField.equals("nextCalibrationDate1"))
					getMeasurementsMwSwitchgear().setNextCalibrationDate1(valueField);
				if (nameField.equals("equipamentType2"))
					getMeasurementsMwSwitchgear().setEquipamentType2(valueField);
				if (nameField.equals("serialNumber2"))
					getMeasurementsMwSwitchgear().setSerialNumber2(valueField);
				if (nameField.equals("calibrationDate2"))
					getMeasurementsMwSwitchgear().setCalibrationDate2(valueField);
				if (nameField.equals("nextCalibrationDate2"))
					getMeasurementsMwSwitchgear().setNextCalibrationDate2(valueField);
				if (nameField.equals("equipamentType3"))
					getMeasurementsMwSwitchgear().setEquipamentType3(valueField);
				if (nameField.equals("serialNumber3"))
					getMeasurementsMwSwitchgear().setSerialNumber3(valueField);
				if (nameField.equals("calibrationDate3"))
					getMeasurementsMwSwitchgear().setCalibrationDate3(valueField);
				if (nameField.equals("nextCalibrationDate3"))
					getMeasurementsMwSwitchgear().setNextCalibrationDate3(valueField);
				if (nameField.equals("equipamentType4"))
					getMeasurementsMwSwitchgear().setEquipamentType4(valueField);
				if (nameField.equals("serialNumber4"))
					getMeasurementsMwSwitchgear().setSerialNumber4(valueField);
				if (nameField.equals("calibrationDate4"))
					getMeasurementsMwSwitchgear().setCalibrationDate4(valueField);
				if (nameField.equals("nextCalibrationDate4"))
					getMeasurementsMwSwitchgear().setNextCalibrationDate4(valueField);
				if (nameField.equals("pongo1"))
					getMeasurementsMwSwitchgear().setPongo1(valueField);
				if (nameField.equals("pongo2"))
					getMeasurementsMwSwitchgear().setPongo2(valueField);
				if (nameField.equals("pongo3"))
					getMeasurementsMwSwitchgear().setPongo3(valueField);
				if (nameField.equals("pongo4"))
					getMeasurementsMwSwitchgear().setPongo4(valueField);
				if (nameField.equals("pongo5"))
					getMeasurementsMwSwitchgear().setPongo5(valueField);
				if (nameField.equals("pongo6"))
					getMeasurementsMwSwitchgear().setPongo6(valueField);
				if (nameField.equals("pongo7"))
					getMeasurementsMwSwitchgear().setPongo7(valueField);
				if (nameField.equals("pruebo1"))
					getMeasurementsMwSwitchgear().setPruebo1(valueField);
				if (nameField.equals("pruebo2"))
					getMeasurementsMwSwitchgear().setPruebo2(valueField);
				if (nameField.equals("pruebo3"))
					getMeasurementsMwSwitchgear().setPruebo3(valueField);
				if (nameField.equals("pruebo4"))
					getMeasurementsMwSwitchgear().setPruebo4(valueField);
				if (nameField.equals("pruebo5"))
					getMeasurementsMwSwitchgear().setPruebo5(valueField);
				if (nameField.equals("pruebo6"))
					getMeasurementsMwSwitchgear().setPruebo6(valueField);
				if (nameField.equals("pruebo7"))
					getMeasurementsMwSwitchgear().setPruebo7(valueField);
				if (nameField.equals("conjuno1"))
					getMeasurementsMwSwitchgear().setConjuno1(valueField);
				if (nameField.equals("conjuno2"))
					getMeasurementsMwSwitchgear().setConjuno2(valueField);
				if (nameField.equals("conjuno3"))
					getMeasurementsMwSwitchgear().setConjuno3(valueField);
				if (nameField.equals("conjuno4"))
					getMeasurementsMwSwitchgear().setConjuno4(valueField);
				if (nameField.equals("conjuno5"))
					getMeasurementsMwSwitchgear().setConjuno5(valueField);
				if (nameField.equals("conjuno6"))
					getMeasurementsMwSwitchgear().setConjuno6(valueField);
				if (nameField.equals("conjuno7"))
					getMeasurementsMwSwitchgear().setConjuno7(valueField);
				if (nameField.equals("prueba1"))
					getMeasurementsMwSwitchgear().setPrueba1(valueField);
				if (nameField.equals("prueba2"))
					getMeasurementsMwSwitchgear().setPrueba2(valueField);
				if (nameField.equals("prueba3"))
					getMeasurementsMwSwitchgear().setPrueba3(valueField);
				if (nameField.equals("prueba4"))
					getMeasurementsMwSwitchgear().setPrueba4(valueField);
				if (nameField.equals("prueba5"))
					getMeasurementsMwSwitchgear().setPrueba5(valueField);
				if (nameField.equals("prueba6"))
					getMeasurementsMwSwitchgear().setPrueba6(valueField);
				if (nameField.equals("prueba7"))
					getMeasurementsMwSwitchgear().setPrueba7(valueField);
				if (nameField.equals("resultado1"))
					getMeasurementsMwSwitchgear().setResultado1(valueField);
				if (nameField.equals("resultado2"))
					getMeasurementsMwSwitchgear().setResultado2(valueField);
				if (nameField.equals("resultado3"))
					getMeasurementsMwSwitchgear().setResultado3(valueField);
				if (nameField.equals("resultado4"))
					getMeasurementsMwSwitchgear().setResultado4(valueField);
				if (nameField.equals("resultado5"))
					getMeasurementsMwSwitchgear().setResultado5(valueField);
				if (nameField.equals("resultado6"))
					getMeasurementsMwSwitchgear().setResultado6(valueField);
				if (nameField.equals("resultado7"))
					getMeasurementsMwSwitchgear().setResultado7(valueField);
				if (nameField.equals("voltage1"))
					getMeasurementsMwSwitchgear().setVoltage1(valueField);
				if (nameField.equals("voltage2"))
					getMeasurementsMwSwitchgear().setVoltage2(valueField);
				if (nameField.equals("voltage3"))
					getMeasurementsMwSwitchgear().setVoltage3(valueField);
				if (nameField.equals("voltage4"))
					getMeasurementsMwSwitchgear().setVoltage4(valueField);
				if (nameField.equals("voltage5"))
					getMeasurementsMwSwitchgear().setVoltage5(valueField);
				if (nameField.equals("voltage6"))
					getMeasurementsMwSwitchgear().setVoltage6(valueField);
				if (nameField.equals("voltage7"))
					getMeasurementsMwSwitchgear().setVoltage7(valueField);
				if (nameField.equals("voltage8"))
					getMeasurementsMwSwitchgear().setVoltage8(valueField);
				if (nameField.equals("voltage9"))
					getMeasurementsMwSwitchgear().setVoltage9(valueField);
				if (nameField.equals("voltage10"))
					getMeasurementsMwSwitchgear().setVoltage10(valueField);
				if (nameField.equals("voltage11"))
					getMeasurementsMwSwitchgear().setVoltage11(valueField);
				if (nameField.equals("voltage12"))
					getMeasurementsMwSwitchgear().setVoltage12(valueField);
				if (nameField.equals("resistencia1"))
					getMeasurementsMwSwitchgear().setResistencia1(valueField);
				if (nameField.equals("resistencia2"))
					getMeasurementsMwSwitchgear().setResistencia2(valueField);
				if (nameField.equals("resistencia3"))
					getMeasurementsMwSwitchgear().setResistencia3(valueField);
				if (nameField.equals("resistencia4"))
					getMeasurementsMwSwitchgear().setResistencia4(valueField);
				if (nameField.equals("resistencia5"))
					getMeasurementsMwSwitchgear().setResistencia5(valueField);
				if (nameField.equals("resistencia6"))
					getMeasurementsMwSwitchgear().setResistencia6(valueField);
				if (nameField.equals("resistencia7"))
					getMeasurementsMwSwitchgear().setResistencia7(valueField);
				if (nameField.equals("resistencia8"))
					getMeasurementsMwSwitchgear().setResistencia8(valueField);
				if (nameField.equals("resistencia9"))
					getMeasurementsMwSwitchgear().setResistencia9(valueField);
				if (nameField.equals("resistencia10"))
					getMeasurementsMwSwitchgear().setResistencia10(valueField);
				if (nameField.equals("resistencia11"))
					getMeasurementsMwSwitchgear().setResistencia11(valueField);
				if (nameField.equals("resistencia12"))
					getMeasurementsMwSwitchgear().setResistencia12(valueField);
				if (nameField.equals("resistenciaPermisible1"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible1(valueField);
				if (nameField.equals("resistenciaPermisible2"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible2(valueField);
				if (nameField.equals("resistenciaPermisible3"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible3(valueField);
				if (nameField.equals("resistenciaPermisible4"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible4(valueField);
				if (nameField.equals("resistenciaPermisible5"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible5(valueField);
				if (nameField.equals("resistenciaPermisible6"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible6(valueField);
				if (nameField.equals("resistenciaPermisible7"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible7(valueField);
				if (nameField.equals("resistenciaPermisible8"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible8(valueField);
				if (nameField.equals("resistenciaPermisible9"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible9(valueField);
				if (nameField.equals("resistenciaPermisible10"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible10(valueField);
				if (nameField.equals("resistenciaPermisible11"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible11(valueField);
				if (nameField.equals("resistenciaPermisible12"))
					getMeasurementsMwSwitchgear().setResistenciaPermisible12(valueField);
				if (nameField.equals("resultadoMili1"))
					getMeasurementsMwSwitchgear().setResultadoMili1(valueField);
				if (nameField.equals("resultadoMili2"))
					getMeasurementsMwSwitchgear().setResultadoMili2(valueField);
				if (nameField.equals("resultadoMili3"))
					getMeasurementsMwSwitchgear().setResultadoMili3(valueField);
				if (nameField.equals("resultadoMili4"))
					getMeasurementsMwSwitchgear().setResultadoMili4(valueField);
				if (nameField.equals("resultadoMili5"))
					getMeasurementsMwSwitchgear().setResultadoMili5(valueField);
				if (nameField.equals("valorPermisibleMili1"))
					getMeasurementsMwSwitchgear().setValorPermisibleMili1(valueField);
				if (nameField.equals("valorPermisibleMili2"))
					getMeasurementsMwSwitchgear().setValorPermisibleMili2(valueField);
				if (nameField.equals("valorPermisibleMili3"))
					getMeasurementsMwSwitchgear().setValorPermisibleMili3(valueField);
				if (nameField.equals("valorPermisibleMili4"))
					getMeasurementsMwSwitchgear().setValorPermisibleMili4(valueField);
				if (nameField.equals("valorPermisibleMili5"))
					getMeasurementsMwSwitchgear().setValorPermisibleMili5(valueField);
				if (nameField.equals("resultadoPosNeg1"))
					getMeasurementsMwSwitchgear().setResultadoPosNeg1(valueField);
				if (nameField.equals("resultadoPosNeg2"))
					getMeasurementsMwSwitchgear().setResultadoPosNeg2(valueField);
				if (nameField.equals("resultadoPosNeg3"))
					getMeasurementsMwSwitchgear().setResultadoPosNeg3(valueField);
				if (nameField.equals("resultadoPosNeg4"))
					getMeasurementsMwSwitchgear().setResultadoPosNeg4(valueField);
				if (nameField.equals("resultadoPosNeg5"))
					getMeasurementsMwSwitchgear().setResultadoPosNeg5(valueField);
				if (nameField.equals("testPerformedBy"))
					getMeasurementsMwSwitchgear().setTestPerformedBy(valueField);
				if (nameField.equals("closedDate"))
					getMeasurementsMwSwitchgear().setClosedDate(valueField);
				if (nameField.contains("description") && imageInsertion) {
					setNameField(nameField);
					setDescription(valueField);
				}
			} else if (field instanceof PDCheckBox) {
				String nameField = field.getFullyQualifiedName();
				String valueField = ((PDCheckBox) field).getValue();
				if (nameField.equals("mvsgCorrect"))
					getMeasurementsMwSwitchgear().setMvsgCorrect(valueField);
				if (nameField.equals("mvsgNotCorrect"))
					getMeasurementsMwSwitchgear().setMvsgNotCorrect(valueField);
				if (nameField.equals("sf6Correct"))
					getMeasurementsMwSwitchgear().setSf6Correct(valueField);
				if (nameField.equals("sf6NotCorrect"))
					getMeasurementsMwSwitchgear().setSf6NotCorrect(valueField);
				if (nameField.equals("insertImagesChk")) {
					getMeasurementsMwSwitchgear().setInsertImagesChk(valueField);
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
								getMeasurementsMwSwitchgear().addImgOnListImages(fileData);
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
		List<FileData> listFileData = fileService.readFile(getMeasurementsMwSwitchgear().getUuid()).stream().filter(filex -> filex.getMimeType().equals("JPG")).collect(Collectors.toList());
		Optional<FileData> fileDataFiltered = listFileData.stream().filter(fileD -> nameFile.equals(fileD.getName())).findAny();
		if (!fileDataFiltered.isPresent()) {
			fileData.setUuid(getMeasurementsMwSwitchgear().getUuid());
			fileData.setCreateDate(getMeasurementsMwSwitchgear().getCreateDate());
			fileData.setModifiedDate(getMeasurementsMwSwitchgear().getModifiedDate());
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
			fileData.setReport(getMeasurementsMwSwitchgear());
			getMeasurementsMwSwitchgear().getListaFileData().add(fileData);
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

	public MeasurementsMwSwitchgear getMeasurementsMwSwitchgear() {
		return this.measurementsMwSwitchgear;
	}

	public void setMeasurementsMwSwitchgear(MeasurementsMwSwitchgear measurementsMwSwitchgear) {
		this.measurementsMwSwitchgear = measurementsMwSwitchgear;
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
