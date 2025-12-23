package com.gsclimbing.reports.populater;

import com.gsclimbing.database.entity.ExaminationTransformer;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.service.FileService;
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
public class ExaminationTransformerPopulater {
	@Autowired
	private FileService fileService;
	private PDDocument _pdfDocument;

	public byte[] generatePDF(Report report) {
		byte[] bytes = null;
		try {
			bytes = populateAndCopy((ExaminationTransformer)report);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private byte[] populateAndCopy(ExaminationTransformer report) throws IOException {
		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/examinationTransformer/", "Examination Transformer.pdf");
		_pdfDocument = PDDocument.load(inputStream);
		_pdfDocument.getNumberOfPages();
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("site", report.getSite());
		setField("dateOfMeasurement", report.getDateOfMeasurement());
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("manufacturer", report.getManufacturer());
		setField("type", report.getType());
		setField("equipamentSerialNumber", report.getEquipamentSerialNumber());
		setField("ratioIdentified", report.getRatioIdentified());
		setField("visualInspectionTransformer", report.getVisualInspectionTransformer());
		setField("equipamentType1", report.getEquipamentType1());
		setField("serialNumber1", report.getSerialNumber1());
		setField("calibrationDate1", report.getCalibrationDate1());
		setField("nextCalibrationDate1", report.getNextCalibrationDate1());
		setField("terminals1_1", report.getTerminals1_1());
		setField("terminals1_2", report.getTerminals1_2());
		setField("terminals1_3", report.getTerminals1_3());
		setField("terminals2_1", report.getTerminals2_1());
		setField("terminals2_2", report.getTerminals2_2());
		setField("terminals2_3", report.getTerminals2_3());
		setField("terminals3_1", report.getTerminals3_1());
		setField("terminals3_2", report.getTerminals3_2());
		setField("terminals3_3", report.getTerminals3_3());
		setField("tolerancia1", report.getTolerancia1());
		setField("tolerancia2", report.getTolerancia2());
		setField("tolerancia3", report.getTolerancia3());
		setField("tolerancia4", report.getTolerancia4());
		setField("tolerancia5", report.getTolerancia5());
		setField("tolerancia6", report.getTolerancia6());
		setField("equipamentType2", report.getEquipamentType2());
		setField("serialNumber2", report.getSerialNumber2());
		setField("calibrationDate2", report.getCalibrationDate2());
		setField("nextCalibrationDate2", report.getNextCalibrationDate2());
		setField("voltage1", report.getVoltage1());
		setField("voltage2", report.getVoltage2());
		setField("voltage3", report.getVoltage3());
		setField("voltage4", report.getVoltage4());
		setField("voltage5", report.getVoltage5());
		setField("voltage6", report.getVoltage6());
		setField("resistencia1", report.getResistencia1());
		setField("resistencia2", report.getResistencia2());
		setField("resistencia3", report.getResistencia3());
		setField("resistencia4", report.getResistencia4());
		setField("resistencia5", report.getResistencia5());
		setField("resistencia6", report.getResistencia6());
		setField("medida1", report.getMedida1());
		setField("medida2", report.getMedida2());
		setField("medida3", report.getMedida3());
		setField("medida4", report.getMedida4());
		setField("medida5", report.getMedida5());
		setField("medida6", report.getMedida6());
		setField("equipamentType3", report.getEquipamentType3());
		setField("serialNumber3", report.getSerialNumber3());
		setField("calibrationDate3", report.getCalibrationDate3());
		setField("nextCalibrationDate3", report.getNextCalibrationDate3());
		setField("voltage", report.getVoltage());
		setField("corrent1", report.getCorrent1());
		setField("corrent2", report.getCorrent2());
		setField("equipamentType4", report.getEquipamentType4());
		setField("serialNumber4", report.getSerialNumber4());
		setField("calibrationDate4", report.getCalibrationDate4());
		setField("nextCalibrationDate4", report.getNextCalibrationDate4());
		setField("insulationResistance", report.getInsulationResistance());
		setField("ratioTest", report.getRatioTest());
		setField("conclusion", report.getConclusion());
		setField("performedBy", report.getPerformedBy());
		setField("date", report.getDate());

		String insertImagesValue = "Off"; // default
		if (report.getInsertImagesChk() != null) {
			if (report.getInsertImagesChk().equalsIgnoreCase("Y") ||
					report.getInsertImagesChk().equalsIgnoreCase("YES")) {
				insertImagesValue = "Yes";
			}
		}
		setField("insertImagesChk", insertImagesValue);

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
			if (fileData != null) {
				bytes = FTPDownloadFiles.downloadFile2FTPServer(fileData.getHash());
			}
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