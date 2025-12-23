package com.gsclimbing.x.pdf.populaters;

import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.x.pdf.common.AbstractPdfReportPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Populator para Performance Report Repair Elevator.
 * Exemplo de como usar a classe base AbstractPdfReportPopulator.
 */
@Service
public class PerformanceRepairElevatorPopulater extends AbstractPdfReportPopulator<PerformanceReportRepairElevator> {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorPopulater.class);
    
    private static final String PDF_TEMPLATE_FOLDER = "/performanceReportRepairElevator/";
    private static final String PDF_TEMPLATE_FILENAME = "Performance Report Repair Elevator.pdf";

    @Override
    protected String getPdfTemplateFolder() {
        return PDF_TEMPLATE_FOLDER;
    }

    @Override
    protected String getPdfTemplateFilename() {
        return PDF_TEMPLATE_FILENAME;
    }

    @Override
    protected void populateTextFields(PerformanceReportRepairElevator report) throws IOException {
        logger.debug("Populating text fields for Performance Report Repair Elevator");
        
        // Informações Gerais
        setTextField("site", report.getSite());
        setTextField("wtgNumber", report.getWtgNumber());
        setTextField("wtgType", report.getWtgType());
        setTextField("yearConstruction", report.getYearConstruction());
        
        // Campos Específicos do Performance Report
        setTextField("reportNumber", report.getReportNumber());
        setTextField("inpectorsWorkers", report.getInpectorsWorkers());
        setTextField("statementOfwork", report.getStatementOfwork());
        setTextField("workCompleted", report.getWorkCompleted());
        setTextField("turbineOperable", report.getTurbineOperable());
        setTextField("placeDate", report.getPlaceDate());
        setTextField("responsibleTechnician", report.getResponsibleTechnician());
        setTextField("performanceReport", report.getPerformanceReport());
        
        // Campos Adicionais
        populateAdditionalField(1, report.getAdditionalField1Label(), report.getAdditionalField1Text());
        populateAdditionalField(2, report.getAdditionalField2Label(), report.getAdditionalField2Text());
        populateAdditionalField(3, report.getAdditionalField3Label(), report.getAdditionalField3Text());
        populateAdditionalField(4, report.getAdditionalField4Label(), report.getAdditionalField4Text());
        populateAdditionalField(5, report.getAdditionalField5Label(), report.getAdditionalField5Text());
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
    protected void processAdditionalFields(PerformanceReportRepairElevator report) throws IOException {
        logger.debug("Processing additional fields for Performance Report Repair Elevator");
        
        // Qualquer processamento adicional específico pode ser implementado aqui
        // Por exemplo: validações, formatações especiais, etc.
    }

    /**
     * Validação específica para imagens do Performance Report
     */
    @Override
    protected boolean validateImage(java.awt.image.BufferedImage image, 
                                   com.gsclimbing.database.entity.FileData fileData) {
        // Validação básica
        if (!super.validateImage(image, fileData)) {
            return false;
        }
        
        // Validações específicas se necessário
        // Por exemplo: tamanho mínimo, proporções, etc.
        
        return true;
    }
}
