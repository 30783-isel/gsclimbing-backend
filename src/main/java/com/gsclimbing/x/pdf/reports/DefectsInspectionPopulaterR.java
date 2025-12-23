package com.gsclimbing.x.pdf.reports;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.x.pdf.common.AbstractPdfReportPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Populator refatorizado para Defect Inspection Reports.
 * Estende AbstractPdfReportPopulator para reutilizar funcionalidades comuns.
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
        
        // Checkbox de inserção de imagens
        setTextField("insertImagesChk", report.getInsertImagesChk());
        
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
        
        // Exemplo: validações, cálculos, etc.
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
