package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.database.service.PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adapter para Performance Report Repair Elevator
 * Estende BaseReportAdapter para reutilizar lógica comum
 */
@Component
public class PerformanceRepairElevatorAdapter extends BaseReportAdapter<PerformanceReportRepairElevator> {

    // ========================================
    // CONVERSÃO DTO -> ENTIDADE (CREATE)
    // ========================================

    @Override
    public PerformanceReportRepairElevator toEntity(
            MobileReportDTO.ReportCreateUpdateDTO dto,
            Turbine turbine) throws Exception {

        logger.info("📋 Converting DTO to Performance Report Repair Elevator entity");

        // Criar nova entidade
        PerformanceReportRepairElevator report = new PerformanceReportRepairElevator();

        // Preencher campos base usando método da classe pai
        populateBaseFields(report, dto, turbine);

        // Preencher campos específicos
        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        report.setReportNumber(getStringValue(reportData, "reportNumber"));
        report.setInpectorsWorkers(getStringValue(reportData, "inspectors"));
        report.setStatementOfwork(getStringValue(reportData, "statementOfwork"));
        report.setWorkCompleted(getStringValue(reportData, "workCompleted"));
        report.setTurbineOperable(getStringValue(reportData, "windturbineOperable"));
        report.setPlaceDate(getStringValue(reportData, "placeDate"));
        report.setResponsibleTechnician(getStringValue(reportData, "responsibleTechnician"));
        report.setPerformanceReport(getStringValue(reportData, "performanceReport"));

        // Campos adicionais (1-7)
        for (int i = 1; i <= 7; i++) {
            JsonNode fieldNode = reportData.get("additionalField" + i);
            if (fieldNode != null && fieldNode.has("label") && fieldNode.has("value")) {
                setAdditionalField(report, i,
                        fieldNode.get("label").asText(),
                        fieldNode.get("value").asText());
            }
        }

        logger.info("✅ Performance Report entity created");
        return report;
    }

    // ========================================
    // EXTRAIR DADOS ESPECÍFICOS
    // ========================================

    /**
     * Extrai dados específicos do DTO para a classe de dados específicos
     */
    public PerformanceReportRepairElevatorSpecificData toSpecificData(
            MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {

        logger.info("📋 Extracting specific data for Performance Report");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());
        PerformanceReportRepairElevatorSpecificData specificData =
                new PerformanceReportRepairElevatorSpecificData();

        specificData.setReportNumber(getStringValue(reportData, "reportNumber"));
        specificData.setInpectorsWorkers(getStringValue(reportData, "inspectors"));
        specificData.setStatementOfwork(getStringValue(reportData, "statementOfwork"));
        specificData.setWorkCompleted(getStringValue(reportData, "workCompleted"));
        specificData.setTurbineOperable(getStringValue(reportData, "windturbineOperable"));
        specificData.setPlaceDate(getStringValue(reportData, "placeDate"));
        specificData.setResponsibleTechnician(getStringValue(reportData, "responsibleTechnician"));
        specificData.setPerformanceReport(getStringValue(reportData, "performanceReport"));

        logger.info("✅ Specific data extracted");
        return specificData;
    }

    // ========================================
    // ATUALIZAÇÃO (UPDATE)
    // ========================================

    /**
     * Atualiza entidade existente com dados do DTO
     */
    public void updateEntity(
            PerformanceReportRepairElevator report,
            MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {

        logger.info("📝 Updating Performance Report entity: {}", report.getReportId());

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // Atualizar campos base
        if (reportData.has("site")) {
            report.setSite(getStringValue(reportData, "site"));
        }
        if (reportData.has("wtgNumber")) {
            report.setWtgNumber(getStringValue(reportData, "wtgNumber"));
        }
        if (reportData.has("wtgType")) {
            report.setWtgType(getStringValue(reportData, "wtgType"));
        }
        if (reportData.has("yearConstruction")) {
            report.setYearConstruction(getStringValue(reportData, "yearConstruction"));
        }

        // Atualizar data de modificação
        report.setModifiedDate(LocalDateTime.now());

        // Atualizar campos adicionais
        for (int i = 1; i <= 7; i++) {
            JsonNode fieldNode = reportData.get("additionalField" + i);
            if (fieldNode != null && fieldNode.has("label") && fieldNode.has("value")) {
                setAdditionalField(report, i,
                        fieldNode.get("label").asText(),
                        fieldNode.get("value").asText());
            }
        }

        logger.info("✅ Entity updated");
    }

    /**
     * Atualiza dados específicos
     */
    public void updateSpecificData(
            PerformanceReportRepairElevatorSpecificData specificData,
            MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {

        logger.info("📝 Updating specific data");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        if (reportData.has("reportNumber")) {
            specificData.setReportNumber(getStringValue(reportData, "reportNumber"));
        }
        if (reportData.has("inspectors")) {
            specificData.setInpectorsWorkers(getStringValue(reportData, "inspectors"));
        }
        if (reportData.has("statementOfwork")) {
            specificData.setStatementOfwork(getStringValue(reportData, "statementOfwork"));
        }
        if (reportData.has("workCompleted")) {
            specificData.setWorkCompleted(getStringValue(reportData, "workCompleted"));
        }
        if (reportData.has("windturbineOperable")) {
            specificData.setTurbineOperable(getStringValue(reportData, "windturbineOperable"));
        }
        if (reportData.has("placeDate")) {
            specificData.setPlaceDate(getStringValue(reportData, "placeDate"));
        }
        if (reportData.has("responsibleTechnician")) {
            specificData.setResponsibleTechnician(getStringValue(reportData, "responsibleTechnician"));
        }
        if (reportData.has("performanceReport")) {
            specificData.setPerformanceReport(getStringValue(reportData, "performanceReport"));
        }

        logger.info("✅ Specific data updated");
    }

    // ========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ========================================

    /**
     * Define campo adicional usando reflexão
     */
    private void setAdditionalField(Report report, int fieldNumber, String label, String text) {
        try {
            String labelMethod = "setAdditionalField" + fieldNumber + "Label";
            String textMethod = "setAdditionalField" + fieldNumber + "Text";

            Report.class.getMethod(labelMethod, String.class).invoke(report, label);
            Report.class.getMethod(textMethod, String.class).invoke(report, text);
        } catch (Exception e) {
            logger.error("Error setting additional field {}", fieldNumber, e);
        }
    }
}