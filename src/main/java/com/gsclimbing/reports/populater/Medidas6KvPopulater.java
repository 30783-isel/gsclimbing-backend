package com.gsclimbing.reports.populater;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.Medidas6KvService;
import com.gsclimbing.ftp.FTPDownloadFiles;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Medidas6KvPopulater {

	@Autowired
	private Medidas6KvService medidas6KvService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private FileService fileService;

	private PDDocument _pdfDocument;

	public byte[] generatePDF(Report report) {

		byte[] bytes = null;
		try {
			bytes = populateAndCopy((Medidas6Kv)report);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bytes;
	}

	private byte[] populateAndCopy(Medidas6Kv report) throws IOException {

		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/medidas6Kv/", "Medidas 6Kv.pdf");

		_pdfDocument = PDDocument.load(inputStream);

		_pdfDocument.getNumberOfPages();

		setField("dateOfMeasurement", report.getDateOfMeasurement());
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());

		setField("chk1", report.isChk1() ? "Yes" : "Off");
		setField("chk2", report.isChk2() ? "Yes" : "Off");
		setField("chk3", report.isChk3() ? "Yes" : "Off");

		setField("voltage1", report.getVoltage1());
		setField("length1", report.getLength1());
		setField("visualInspection1", report.getVisualInspection1());

		setField("equipmentType1", report.getEquipmentType1());
		setField("serialNumber1", report.getSerialNumber1());
		setField("calibrationDate1", report.getCalibrationDate1());
		setField("nextCalibrationDate1", report.getNextCalibrationDate1());

		setField("box1_1", report.getBox1_1());
		setField("box1_2", report.getBox1_2());
		setField("box1_3", report.getBox1_3());
		setField("box1_4", report.getBox1_4());
		setField("box1_5", report.getBox1_5());
		setField("box1_6", report.getBox1_6());
		setField("box1_7", report.getBox1_7());
		setField("box1_8", report.getBox1_8());
		setField("box1_9", report.getBox1_9());
		setField("box1_10", report.getBox1_10());
		setField("box1_11", report.getBox1_11());
		setField("box1_12", report.getBox1_12());
		setField("box1_13", report.getBox1_13());
		setField("box1_14", report.getBox1_14());
		setField("box1_15", report.getBox1_15());
		setField("box1_16", report.getBox1_16());
		setField("box1_17", report.getBox1_17());
		setField("box1_18", report.getBox1_18());

		setField("equipmentType2", report.getEquipmentType2());
		setField("serialNumber2", report.getSerialNumber2());
		setField("calibrationDate2", report.getCalibrationDate2());
		setField("nextCalibrationDate2", report.getNextCalibrationDate2());

		setField("box2_1", report.getBox2_1());
		setField("box2_2", report.getBox2_2());
		setField("box2_3", report.getBox2_3());
		setField("box2_4", report.getBox2_4());
		setField("box2_5", report.getBox2_5());
		setField("box2_6", report.getBox2_6());

		setField("box3_1", report.getBox3_1());
		setField("box3_2", report.getBox3_2());

		setField("type2", report.getType2());
		setField("voltage2", report.getVoltage2());
		setField("length2", report.getLength2());
		setField("visualInspection2", report.getVisualInspection2());

		setField("equipmentType3", report.getEquipmentType3());
		setField("serialNumber3", report.getSerialNumber3());
		setField("calibrationDate3", report.getCalibrationDate3());
		setField("nextCalibrationDate3", report.getNextCalibrationDate3());

		setField("box4_1", report.getBox4_1());
		setField("box4_2", report.getBox4_2());
		setField("box4_3", report.getBox4_3());
		setField("box4_4", report.getBox4_4());
		setField("box4_5", report.getBox4_5());
		setField("box4_6", report.getBox4_6());
		setField("box4_7", report.getBox4_7());
		setField("box4_8", report.getBox4_8());
		setField("box4_9", report.getBox4_9());
		setField("box4_10", report.getBox4_10());
		setField("box4_11", report.getBox4_11());
		setField("box4_12", report.getBox4_12());
		setField("box4_13", report.getBox4_13());
		setField("box4_14", report.getBox4_14());
		setField("box4_15", report.getBox4_15());
		setField("box4_16", report.getBox4_16());
		setField("box4_17", report.getBox4_17());
		setField("box4_18", report.getBox4_18());

		setField("equipmentType4", report.getEquipmentType4());
		setField("serialNumber4", report.getSerialNumber4());
		setField("calibrationDate4", report.getCalibrationDate4());
		setField("nextCalibrationDate4", report.getNextCalibrationDate4());

		setField("box5_1", report.getBox5_1());
		setField("box5_2", report.getBox5_2());
		setField("box5_3", report.getBox5_3());
		setField("box5_4", report.getBox5_4());
		setField("box5_5", report.getBox5_5());
		setField("box5_6", report.getBox5_6());

		setField("box6_1", report.getBox6_1());
		setField("box6_2", report.getBox6_2());

		setField("type3", report.getType3());
		setField("voltage3", report.getVoltage3());
		setField("length3", report.getLength3());
		setField("visualInspection3", report.getVisualInspection3());

		setField("equipmentType5", report.getEquipmentType5());
		setField("serialNumber5", report.getSerialNumber5());
		setField("calibrationDate5", report.getCalibrationDate5());
		setField("nextCalibrationDate5", report.getNextCalibrationDate5());

		setField("box7_1", report.getBox7_1());
		setField("box7_2", report.getBox7_2());
		setField("box7_3", report.getBox7_3());
		setField("box7_4", report.getBox7_4());
		setField("box7_5", report.getBox7_5());
		setField("box7_6", report.getBox7_6());
		setField("box7_7", report.getBox7_7());
		setField("box7_8", report.getBox7_8());
		setField("box7_9", report.getBox7_9());
		setField("box7_10", report.getBox7_10());
		setField("box7_11", report.getBox7_11());
		setField("box7_12", report.getBox7_12());
		setField("box7_13", report.getBox7_13());
		setField("box7_14", report.getBox7_14());
		setField("box7_15", report.getBox7_15());
		setField("box7_16", report.getBox7_16());
		setField("box7_17", report.getBox7_17());
		setField("box7_18", report.getBox7_18());

		setField("equipmentType6", report.getEquipmentType6());
		setField("serialNumber6", report.getSerialNumber6());
		setField("calibrationDate6", report.getCalibrationDate6());
		setField("nextCalibrationDate6", report.getNextCalibrationDate6());

		setField("box8_1", report.getBox8_1());
		setField("box8_2", report.getBox8_2());
		setField("box8_3", report.getBox8_3());
		setField("box8_4", report.getBox8_4());
		setField("box8_5", report.getBox8_5());
		setField("box8_6", report.getBox8_6());

		setField("box9_1", report.getBox9_1());
		setField("box9_2", report.getBox9_2());

		setField("conclusion", report.getConclusion());
		setField("performedBy", report.getPerformedBy());
		setField("closedDate", report.getClosedDate());
		
		setField("insertImagesChk", report.getInsertImagesChk());
		setField("additionalField1Label", report.getAdditionalField1Label());
		setField("additionalField1Text", report.getAdditionalField1Text());
		setField("additionalField2Label", report.getAdditionalField2Label());
		setField("additionalField2Text", report.getAdditionalField2Text());
		setField("additionalField3Label", report.getAdditionalField3Label());
		setField("additionalField3Text", report.getAdditionalField3Text());
		setField("additionalField4Label", report.getAdditionalField4Label());
		setField("additionalField4Text", report.getAdditionalField4Text());
		setField("additionalField5Label", report.getAdditionalField5Label());
		setField("additionalField5Text", report.getAdditionalField5Text());
		setField("additionalField6Label", report.getAdditionalField6Label());
		setField("additionalField6Text", report.getAdditionalField6Text());
		setField("additionalField7Label", report.getAdditionalField7Label());
		setField("additionalField7Text", report.getAdditionalField7Text());
		

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
			String valor;
			if (value == null || value.length() == 0 || value.chars().allMatch(Character::isWhitespace)) {
				valor = "xxx";
			} else {
				valor = value;
			}

			field.setValue(valor);
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

			PDImageXObject pdImageXObject = PDImageXObject.createFromByteArray(_pdfDocument, bytes, "Measurements of MV Switchgear and Stator Cabinet");
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