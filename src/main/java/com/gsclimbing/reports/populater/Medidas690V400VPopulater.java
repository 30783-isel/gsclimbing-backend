package com.gsclimbing.reports.populater;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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

//import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
//import org.apache.pdfbox.exceptions.COSVisitorException;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Medidas690V400V;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.Medidas690V400VService;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class Medidas690V400VPopulater {

	@Autowired
	private Medidas690V400VService medidas690V400VService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private FileService fileService;

	private PDDocument _pdfDocument;

	public byte[] generatePDF(Medidas690V400V report) {

		byte[] bytes = null;
		try {
			bytes = populateAndCopy(report);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytes;
	}

	private byte[] populateAndCopy(Medidas690V400V report) throws IOException {

		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/medidas690V400V/", "Medidas 690V400V.pdf");

		_pdfDocument = PDDocument.load(inputStream);

		_pdfDocument.getNumberOfPages();

		setField("dateOfMeasurement", report.getDateOfMeasurement());
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());

		setField("type1", report.getType1());
		setField("voltage1", report.getVoltage1());
		setField("length1", report.getLength1());
		setField("visual1", report.getVisual1());

		setField("box1_1", report.getBox1_1());
		setField("box1_2", report.getBox1_2());
		setField("box1_3", report.getBox1_3());
		setField("box1_4", report.getBox1_4());
		setField("box1_5", report.getBox1_5());
		setField("box1_6", report.getBox1_6());
		setField("box1_7", report.getBox1_7());
		setField("box1_8", report.getBox1_8());
		setField("box1_9", report.getBox1_9());

		setField("type2", report.getType2());
		setField("voltage2", report.getBox1_1());
		setField("length2", report.getLength2());
		setField("visual2", report.getVisual2());

		setField("box2_1", report.getBox2_1());
		setField("box2_2", report.getBox2_2());
		setField("box2_3", report.getBox2_3());
		setField("box2_4", report.getBox2_4());
		setField("box2_5", report.getBox2_5());
		setField("box2_6", report.getBox2_6());
		setField("box2_7", report.getBox2_7());
		setField("box2_8", report.getBox2_8());
		setField("box2_9", report.getBox2_9());

		setField("type3", report.getType3());
		setField("voltage3", report.getVoltage3());
		setField("length3", report.getLength3());
		setField("visual3", report.getVisual3());

		setField("box3_1", report.getBox3_1());
		setField("box3_2", report.getBox3_2());
		setField("box3_3", report.getBox3_3());
		setField("box3_4", report.getBox3_4());
		setField("box3_5", report.getBox3_5());
		setField("box3_6", report.getBox3_6());
		setField("box3_7", report.getBox3_7());
		setField("box3_8", report.getBox3_8());
		setField("box3_9", report.getBox3_9());

		setField("type4", report.getType4());
		setField("voltage4", report.getVoltage4());
		setField("length4", report.getLength4());
		setField("visual4", report.getVisual4());

		setField("box4_1", report.getBox4_1());
		setField("box4_2", report.getBox4_2());
		setField("box4_3", report.getBox4_3());
		setField("box4_4", report.getBox4_4());
		setField("box4_5", report.getBox4_5());
		setField("box4_6", report.getBox4_6());
		setField("box4_7", report.getBox4_7());
		setField("box4_8", report.getBox4_8());
		setField("box4_9", report.getBox4_9());


		setField("type5", report.getType5());
		setField("voltage5", report.getVoltage5());
		setField("length5", report.getLength5());
		setField("visual5", report.getVisual5());

		setField("box5_1",  report.getBox5_1());
		setField("box5_2",  report.getBox5_2());
		setField("box5_3",  report.getBox5_3());
		setField("box5_4",  report.getBox5_4());
		setField("box5_5",  report.getBox5_5());
		setField("box5_6",  report.getBox5_6());
		setField("box5_7",  report.getBox5_7());
		setField("box5_8",  report.getBox5_8());
		setField("box5_9",  report.getBox5_9());
		setField("box5_10", report.getBox5_10());
		setField("box5_11", report.getBox5_11());
		setField("box5_12", report.getBox5_12());

		setField("box6_1",  report.getBox6_1());
		setField("box6_2",  report.getBox6_2());
		setField("box6_3",  report.getBox6_3());
		setField("box6_4",  report.getBox6_4());
		setField("box6_5",  report.getBox6_5());
		setField("box6_6",  report.getBox6_6());
		setField("box6_7",  report.getBox6_7());
		setField("box6_8",  report.getBox6_8());
		setField("box6_9",  report.getBox6_9());
		setField("box6_10", report.getBox6_10());
		setField("box6_11", report.getBox6_11());
		setField("box6_12", report.getBox6_12());
		setField("box6_13", report.getBox6_13());
		setField("box6_14", report.getBox6_14());
		setField("box6_15", report.getBox6_15());
		setField("box6_16", report.getBox6_16());
		setField("box6_17", report.getBox6_17());
		setField("box6_18", report.getBox6_18());
		setField("box6_19", report.getBox6_19());
		setField("box6_20", report.getBox6_20());
		setField("box6_21", report.getBox6_21());
		setField("box6_22", report.getBox6_22());
		setField("box6_23", report.getBox6_23());
		setField("box6_24", report.getBox6_24());

		setField("type6", report.getType6());
		setField("voltage6", report.getVoltage6());
		setField("visual6", report.getVisual6());

		setField("box7_1",  report.getBox7_1());
		setField("box7_2",  report.getBox7_2());
		setField("box7_3",  report.getBox7_3());
		setField("box7_4",  report.getBox7_4());
		setField("box7_5",  report.getBox7_5());
		setField("box7_6",  report.getBox7_6());
		setField("box7_7",  report.getBox7_7());
		setField("box7_8",  report.getBox7_8());
		setField("box7_9",  report.getBox7_9());
		setField("box7_10", report.getBox7_10());
		setField("box7_11", report.getBox7_11());
		setField("box7_12", report.getBox7_12());
		setField("box7_13", report.getBox7_13());
		setField("box7_14", report.getBox7_14());
		setField("box7_15", report.getBox7_15());
		setField("box7_16", report.getBox7_16());
		setField("box7_17", report.getBox7_17());
		setField("box7_18", report.getBox7_18());
		setField("box7_19", report.getBox7_19());
		setField("box7_20", report.getBox7_20());
		setField("box7_21", report.getBox7_21());
		setField("box7_22", report.getBox7_22());
		setField("box7_23", report.getBox7_23());
		setField("box7_24", report.getBox7_24());
		setField("box7_25", report.getBox7_25());
		setField("box7_26", report.getBox7_26());
		setField("box7_27", report.getBox7_27());

		setField("equipmentType", report.getEquipmentType());
		setField("serialNumber", report.getSerialNumber());
		setField("calibrationType", report.getCalibrationDate());
		setField("nextCalibrationType", report.getNextCalibrationDate());

		setField("conclusion", report.getConclusion());
		setField("performedBy", report.getPerformedBy());
		setField("closedDate", report.getClosedDate());

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

			PDImageXObject pdImageXObject = PDImageXObject.createFromByteArray(_pdfDocument, bytes, "Medidas 690V400V");
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