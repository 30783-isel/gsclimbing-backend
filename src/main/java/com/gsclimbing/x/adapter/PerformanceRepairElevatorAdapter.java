package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.database.service.PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter para Performance Report Repair Elevator
 * ✅ CORRIGIDO: Retorna PerformanceReportRepairElevator em vez de Report
 */
@Component
public class PerformanceRepairElevatorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorAdapter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ✅ CORRIGIDO: Converter DTO mobile para entidade PerformanceReportRepairElevator (CREATE)
     * MUDANÇA: Return type agora é PerformanceReportRepairElevator em vez de Report
     */
    public PerformanceReportRepairElevator toEntity(MobileReportDTO.ReportCreateUpdateDTO dto, Turbine turbine) throws Exception {
        logger.info("📋 Converting DTO to Performance Report Repair Elevator entity");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // ✅ CORREÇÃO: Criar diretamente PerformanceReportRepairElevator!
        PerformanceReportRepairElevator report = new PerformanceReportRepairElevator();

        // UUID e timestamps
        report.setUuid(UUID.randomUUID().toString());
        report.setCreateDate(LocalDateTime.now());
        report.setModifiedDate(LocalDateTime.now());

        // Informações básicas do relatório
        report.setSite(getStringValue(reportData, "site"));
        report.setWtgNumber(getStringValue(reportData, "wtgNumber"));
        report.setWtgType(getStringValue(reportData, "wtgType"));
        report.setYearConstruction(getStringValue(reportData, "yearConstruction"));

        // IDs - turbineId vem do DTO, projectoId vem do reportData JSON
        report.setTurbinaId(dto.getTurbineId() != null ? dto.getTurbineId().intValue() : null);
        report.setProjectoId(reportData.has("projectoId") ? reportData.get("projectoId").asInt() : null);
        report.setTurbine(turbine);

        // Status e permissões
        report.setLocked("N");
        report.setPermission2Edit("Y");
        report.setInsertImagesChk("N");

        // Campos adicionais (se existirem no DTO)
        for (int i = 1; i <= 7; i++) {
            JsonNode fieldNode = reportData.get("additionalField" + i);
            if (fieldNode != null && fieldNode.has("label") && fieldNode.has("value")) {
                setAdditionalField(report, i,
                        fieldNode.get("label").asText(),
                        fieldNode.get("value").asText());
            }
        }

        logger.info("✅ Entity created with UUID: {}", report.getUuid());
        return report;
    }

    /**
     * Extrair dados específicos do DTO para PerformanceReportRepairElevator
     */
    public PerformanceReportRepairElevatorSpecificData toSpecificData(MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        logger.info("📋 Extracting specific data for Performance Report Repair Elevator");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        PerformanceReportRepairElevatorSpecificData specificData =
                new PerformanceReportRepairElevatorSpecificData();

        // Campos específicos do Performance Report Repair Elevator
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

    /**
     * ✅ CORRIGIDO: Atualizar entidade existente com dados do DTO (UPDATE)
     * MUDANÇA: Aceita PerformanceReportRepairElevator em vez de Report
     */
    public void updateEntity(PerformanceReportRepairElevator report, MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        logger.info("📝 Updating Performance Report Repair Elevator entity: {}", report.getReportId());

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // Atualizar campos básicos
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

        // Atualizar modified date
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
     * Atualizar dados específicos
     */
    public void updateSpecificData(PerformanceReportRepairElevatorSpecificData specificData,
                                   MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        logger.info("📝 Updating specific data for Performance Report Repair Elevator");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // Atualizar campos específicos
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

    // ====================================================================
    // MÉTODOS AUXILIARES
    // ====================================================================

    /**
     * Obter valor string de um JsonNode
     */
    private String getStringValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }

    /**
     * Definir campo adicional
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