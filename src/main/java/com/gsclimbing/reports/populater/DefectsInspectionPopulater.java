package com.gsclimbing.reports.populater;

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
public class DefectsInspectionPopulater {

	@Autowired
	private FileService fileService;

	private PDDocument _pdfDocument;

	public byte[] generatePDF(Report report) {

		byte[] bytes = null;
		try {
			bytes = populateAndCopy(report);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytes;
	}

	private byte[] populateAndCopy(Report report) throws IOException {

		String filename = "Defect Inspection Report.pdf";

		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/defectInspectionReport/", "Defect Inspection Report.pdf");

		_pdfDocument = PDDocument.load(inputStream);

		_pdfDocument.getNumberOfPages();

		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("wtgType", report.getWtgType());
		setField("yearConstruction", report.getYearConstruction());


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
			field.setValue(value);
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

			PDImageXObject pdImageXObject = PDImageXObject.createFromByteArray(_pdfDocument, bytes, "Defect Inspection Report");
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
