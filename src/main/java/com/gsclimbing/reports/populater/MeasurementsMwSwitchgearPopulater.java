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
import com.gsclimbing.database.entity.MeasurementsMwSwitchgear;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.MeasurementsMwSwitchgearService;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class MeasurementsMwSwitchgearPopulater {
	
	@Autowired
	private FileService fileService;
	
	private PDDocument _pdfDocument;
	public byte[] generatePDF(Report report) {
		byte[] bytes = null;
		try {
			bytes = populateAndCopy((MeasurementsMwSwitchgear)report);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private byte[] populateAndCopy(MeasurementsMwSwitchgear report) throws IOException {
		InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile("/measurementsMVSwitchgearStatorCabinet/", "Measurements of MV Switchgear and Stator Cabinet.pdf");
		_pdfDocument = PDDocument.load(inputStream);
		_pdfDocument.getNumberOfPages();
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("site", report.getSite());
		setField("manufacturerDate", report.getManufacturerDate());
		setField("dateMeasurement", report.getDateMeasurement());
		setField("site", report.getSite());
		setField("wtgNumber", report.getWtgNumber());
		setField("type", report.getType());
		setField("serialNumber", report.getSerialNumber());
		setField("equipamentType1", report.getEquipamentType1());
		setField("serialNumber1", report.getSerialNumber1());
		setField("calibrationDate1", report.getCalibrationDate1());
		setField("nextCalibrationDate1", report.getNextCalibrationDate1());
		setField("equipamentType2", report.getEquipamentType2());
		setField("serialNumber2", report.getSerialNumber2());
		setField("calibrationDate2", report.getCalibrationDate2());
		setField("nextCalibrationDate2", report.getNextCalibrationDate2());
		setField("equipamentType3", report.getEquipamentType3());
		setField("serialNumber3", report.getSerialNumber3());
		setField("calibrationDate3", report.getCalibrationDate3());
		setField("nextCalibrationDate3", report.getNextCalibrationDate3());
		setField("equipamentType4", report.getEquipamentType4());
		setField("serialNumber4", report.getSerialNumber4());
		setField("calibrationDate4", report.getCalibrationDate4());
		setField("nextCalibrationDate4", report.getNextCalibrationDate4());
		setField("pongo1", report.getPongo1());
		setField("pongo2", report.getPongo2());
		setField("pongo3", report.getPongo3());
		setField("pongo4", report.getPongo4());
		setField("pongo5", report.getPongo5());
		setField("pongo6", report.getPongo6());
		setField("pongo7", report.getPongo7());
		setField("pruebo1", report.getPruebo1());
		setField("pruebo2", report.getPruebo2());
		setField("pruebo3", report.getPruebo3());
		setField("pruebo4", report.getPruebo4());
		setField("pruebo5", report.getPruebo5());
		setField("pruebo6", report.getPruebo6());
		setField("pruebo7", report.getPruebo7());
		setField("conjuno1", report.getConjuno1());
		setField("conjuno2", report.getConjuno2());
		setField("conjuno3", report.getConjuno3());
		setField("conjuno4", report.getConjuno4());
		setField("conjuno5", report.getConjuno5());
		setField("conjuno6", report.getConjuno6());
		setField("conjuno7", report.getConjuno7());
		setField("prueba1", report.getPrueba1());
		setField("prueba2", report.getPrueba2());
		setField("prueba3", report.getPrueba3());
		setField("prueba4", report.getPrueba4());
		setField("prueba5", report.getPrueba5());
		setField("prueba6", report.getPrueba6());
		setField("prueba7", report.getPrueba7());
		setField("resultado1", report.getResultado1());
		setField("resultado2", report.getResultado2());
		setField("resultado3", report.getResultado3());
		setField("resultado4", report.getResultado4());
		setField("resultado5", report.getResultado5());
		setField("resultado6", report.getResultado6());
		setField("resultado7", report.getResultado7());
		setField("voltage1", report.getVoltage1());
		setField("voltage2", report.getVoltage2());
		setField("voltage3", report.getVoltage3());
		setField("voltage4", report.getVoltage4());
		setField("voltage5", report.getVoltage5());
		setField("voltage6", report.getVoltage6());
		setField("voltage7", report.getVoltage7());
		setField("voltage8", report.getVoltage8());
		setField("voltage9", report.getVoltage9());
		setField("voltage10", report.getVoltage10());
		setField("voltage11", report.getVoltage11());
		setField("voltage12", report.getVoltage12());
		setField("resistencia1", report.getResistencia1());
		setField("resistencia2", report.getResistencia2());
		setField("resistencia3", report.getResistencia3());
		setField("resistencia4", report.getResistencia4());
		setField("resistencia5", report.getResistencia5());
		setField("resistencia6", report.getResistencia6());
		setField("resistencia7", report.getResistencia7());
		setField("resistencia8", report.getResistencia8());
		setField("resistencia9", report.getResistencia9());
		setField("resistencia10", report.getResistencia10());
		setField("resistencia11", report.getResistencia11());
		setField("resistencia12", report.getResistencia12());
		setField("resistenciaPermisible1", report.getResistenciaPermisible1());
		setField("resistenciaPermisible2", report.getResistenciaPermisible2());
		setField("resistenciaPermisible3", report.getResistenciaPermisible3());
		setField("resistenciaPermisible4", report.getResistenciaPermisible4());
		setField("resistenciaPermisible5", report.getResistenciaPermisible5());
		setField("resistenciaPermisible6", report.getResistenciaPermisible6());
		setField("resistenciaPermisible7", report.getResistenciaPermisible7());
		setField("resistenciaPermisible8", report.getResistenciaPermisible8());
		setField("resistenciaPermisible9", report.getResistenciaPermisible9());
		setField("resistenciaPermisible10", report.getResistenciaPermisible10());
		setField("resistenciaPermisible11", report.getResistenciaPermisible11());
		setField("resistenciaPermisible12", report.getResistenciaPermisible12());
		setField("resultadoMili1", report.getResultadoMili1());
		setField("resultadoMili2", report.getResultadoMili2());
		setField("resultadoMili3", report.getResultadoMili3());
		setField("resultadoMili4", report.getResultadoMili4());
		setField("resultadoMili5", report.getResultadoMili5());
		setField("valorPermisibleMili1", report.getValorPermisibleMili1());
		setField("valorPermisibleMili2", report.getValorPermisibleMili2());
		setField("valorPermisibleMili3", report.getValorPermisibleMili3());
		setField("valorPermisibleMili4", report.getValorPermisibleMili4());
		setField("valorPermisibleMili5", report.getValorPermisibleMili5());
		setField("resultadoPosNeg1", report.getResultadoPosNeg1());
		setField("resultadoPosNeg2", report.getResultadoPosNeg2());
		setField("resultadoPosNeg3", report.getResultadoPosNeg3());
		setField("resultadoPosNeg4", report.getResultadoPosNeg4());
		setField("resultadoPosNeg5", report.getResultadoPosNeg5());
		setField("testPerformedBy", report.getTestPerformedBy());
		setField("closedDate", report.getClosedDate());
		//TODO
//		setField("mvsgCorrect", report.getMvsgCorrect());
//		setField("mvsgNotCorrect", report.getMvsgNotCorrect());
//		setField("sf6Correct", report.getSf6Correct());
//		setField("sf6NotCorrect", report.getSf6NotCorrect());
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