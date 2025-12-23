package com.gsclimbing.x.pdf.populaters;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.x.pdf.common.AbstractPdfReportPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Populator refatorizado para Defect Inspection Reports.
 * Estende AbstractPdfReportPopulator para reutilizar funcionalidades comuns.
 *
 * CORRIGIDO: Adiciona conversão automática de valores de checkbox
 */
@Service
public class DefectsInspectionPopulaterR extends AbstractPdfReportPopulator<DefectsInspectionReport> {

    private static final Logger logger = LoggerFactory.getLogger(DefectsInspectionPopulaterR.class);

    private static final String PDF_TEMPLATE_FOLDER = "/defectInspectionReport/";
    private static final String PDF_TEMPLATE_FILENAME = "Defect Inspection Report.pdf";

    @Override
    protected String getPdfTemplateFolder() {
        return PDF_TEMPLATE_FOLDER;
    }

    @Override
    protected String getPdfTemplateFilename() {
        return PDF_TEMPLATE_FILENAME;
    }

    @Override
    protected void populateTextFields(DefectsInspectionReport report) throws IOException {
        logger.debug("Populating text fields for Defect Inspection Report");

        // Página 1 - Informações Gerais
        setTextField("site", report.getSite());
        setTextField("wtgNumber", report.getWtgNumber());
        setTextField("wtgType", report.getWtgType());
        setTextField("yearConstruction", report.getYearConstruction());

        // Checkbox de inserção de imagens - CONVERSÃO NECESSÁRIA
        // O campo aceita apenas "Yes" ou "Off", mas o relatório pode ter "Y" ou outras variações
        String insertImagesValue = convertToCheckboxValue(report.getInsertImagesChk());
        setTextField("insertImagesChk", insertImagesValue);

        logger.debug("Checkbox insertImagesChk: input='{}' -> converted='{}'",
                report.getInsertImagesChk(), insertImagesValue);

        // Página 4 - Campos Adicionais
        populateAdditionalField(1, report.getAdditionalField1Label(), report.getAdditionalField1Text());
        populateAdditionalField(2, report.getAdditionalField2Label(), report.getAdditionalField2Text());
        populateAdditionalField(3, report.getAdditionalField3Label(), report.getAdditionalField3Text());
        populateAdditionalField(4, report.getAdditionalField4Label(), report.getAdditionalField4Text());
        populateAdditionalField(5, report.getAdditionalField5Label(), report.getAdditionalField5Text());
        populateAdditionalField(6, report.getAdditionalField6Label(), report.getAdditionalField6Text());
        populateAdditionalField(7, report.getAdditionalField7Label(), report.getAdditionalField7Text());
    }

    /**
     * Converte valores de checkbox para o formato aceite pelo PDF
     *
     * O PDFBox aceita apenas "Yes" ou "Off" para checkboxes.
     * Esta função converte variações comuns:
     * - "Y", "y", "YES", "yes", "Yes" -> "Yes"
     * - "N", "n", "NO", "no", "No", "OFF", "off" -> "Off"
     * - null ou vazio -> "Off"
     *
     * @param value Valor original do relatório
     * @return "Yes" ou "Off"
     */
    private String convertToCheckboxValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Off";
        }

        String normalized = value.trim().toUpperCase();

        // Valores que significam "checked" / "yes"
        if (normalized.equals("Y") ||
                normalized.equals("YES") ||
                normalized.equals("TRUE") ||
                normalized.equals("1")) {
            return "Yes";
        }

        // Valores que significam "unchecked" / "no"
        if (normalized.equals("N") ||
                normalized.equals("NO") ||
                normalized.equals("OFF") ||
                normalized.equals("FALSE") ||
                normalized.equals("0")) {
            return "Off";
        }

        // Se já está no formato correcto, manter
        if (value.equals("Yes") || value.equals("Off")) {
            return value;
        }

        // Default para "Off" se não reconhecer o valor
        logger.warn("Unrecognized checkbox value '{}', defaulting to 'Off'", value);
        return "Off";
    }

    /**
     * Popular um campo adicional (label + text)
     */
    private void populateAdditionalField(int fieldNumber, String label, String text) throws IOException {
        String labelFieldName = "additionalField" + fieldNumber + "Label";
        String textFieldName = "additionalField" + fieldNumber + "Text";

        if (label != null && !label.trim().isEmpty()) {
            setTextField(labelFieldName, label);
        }

        if (text != null && !text.trim().isEmpty()) {
            setTextField(textFieldName, text);
        }
    }

    @Override
    protected void processAdditionalFields(DefectsInspectionReport report) throws IOException {
        // Qualquer processamento adicional específico para Defect Inspection Report
        // pode ser implementado aqui

        logger.debug("Processing additional fields for Defect Inspection Report");

        // Por enquanto, não há processamento adicional necessário
    }

    /**
     * Validação específica para imagens de Defect Inspection Report
     */
    @Override
    protected boolean validateImage(java.awt.image.BufferedImage image,
                                    com.gsclimbing.database.entity.FileData fileData) {
        // Validação básica da classe pai
        if (!super.validateImage(image, fileData)) {
            return false;
        }

        // Validações específicas do Defect Inspection Report
        // Por exemplo: tamanho mínimo de imagem
        final int MIN_WIDTH = 100;
        final int MIN_HEIGHT = 100;

        if (image.getWidth() < MIN_WIDTH || image.getHeight() < MIN_HEIGHT) {
            logger.warn("Image too small: {}x{} (min: {}x{})",
                    image.getWidth(), image.getHeight(), MIN_WIDTH, MIN_HEIGHT);
            return false;
        }

        return true;
    }

    /**
     * Redimensionamento específico para Defect Inspection Report
     */
    @Override
    protected java.awt.image.BufferedImage resizeImageIfNeeded(java.awt.image.BufferedImage image) {
        // Limites máximos para imagens no relatório
        final int MAX_WIDTH = 2048;
        final int MAX_HEIGHT = 2048;

        if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
            logger.debug("Resizing image from {}x{}", image.getWidth(), image.getHeight());
            return pdfImageService.resizeImage(image, MAX_WIDTH, MAX_HEIGHT);
        }

        return image;
    }
}