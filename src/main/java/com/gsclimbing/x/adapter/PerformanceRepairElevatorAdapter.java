package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Converte entre DTOs mobile e entidade Report + dados específicos
 */
@Component
public class PerformanceRepairElevatorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorAdapter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converter DTO mobile para entidade Report (CREATE)
     * Retorna apenas a entidade Report - use toSpecificData() para obter dados específicos
     */
    public Report toEntity(MobileReportDTO.ReportCreateUpdateDTO dto, Turbine turbine) throws Exception {
        logger.info("📋 Converting DTO to Performance Report Repair Elevator entity");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        Report report = new Report();

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
        report.setProjectoId(getIntegerValue(reportData, "projectoId"));
        report.setTurbine(turbine);

        // Additional Fields (se existirem no DTO)
        if (reportData.has("additionalFields")) {
            JsonNode additionalFields = reportData.get("additionalFields");
            if (additionalFields.isArray()) {
                int fieldIndex = 1;
                for (JsonNode field : additionalFields) {
                    if (field.has("label") && field.has("value") && fieldIndex <= 7) {
                        setAdditionalField(
                                report,
                                fieldIndex,
                                field.get("label").asText(),
                                field.get("value").asText());
                        fieldIndex++;
                    }
                }
            }
        }

        // Flags de controlo
        report.setLocked("N");
        report.setPermission2Edit("Y");
        report.setInsertImagesChk("Y");

        logger.info("✅ Entity created with UUID: {}", report.getUuid());
        return report;
    }

    /**
     * Extrair dados específicos do DTO para PerformanceReportRepairElevator
     *
     * @param dto DTO com os dados do relatório
     * @return Objeto com dados específicos do Performance Report Repair Elevator
     */
    public PerformanceReportRepairElevatorSpecificData toSpecificData(MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        logger.info("📋 Extracting specific data for Performance Report Repair Elevator");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        PerformanceReportRepairElevatorSpecificData specificData = new PerformanceReportRepairElevatorSpecificData();

        // Campos específicos do Performance Report Repair Elevator
        specificData.setReportNumber(getStringValue(reportData, "reportNumber"));
        specificData.setInpectorsWorkers(getStringValue(reportData, "inspectors")); // ou "inpectorsWorkers"
        specificData.setStatementOfwork(getStringValue(reportData, "statementOfwork"));
        specificData.setWorkCompleted(getStringValue(reportData, "workCompleted"));
        specificData.setTurbineOperable(getStringValue(reportData, "windturbineOperable")); // ou "turbineOperable"
        specificData.setPlaceDate(getStringValue(reportData, "placeDate"));
        specificData.setResponsibleTechnician(getStringValue(reportData, "responsibleTechnician"));
        specificData.setPerformanceReport(getStringValue(reportData, "performanceReport"));

        logger.info("✅ Specific data extracted");
        return specificData;
    }

    /**
     * Atualizar entidade existente com dados do DTO (UPDATE)
     */
    public void updateEntity(Report report, MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
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

        // Atualizar timestamp
        report.setModifiedDate(LocalDateTime.now());

        // Atualizar Additional Fields se existirem
        if (reportData.has("additionalFields")) {
            JsonNode additionalFields = reportData.get("additionalFields");
            if (additionalFields.isArray()) {
                int fieldIndex = 1;
                for (JsonNode field : additionalFields) {
                    if (field.has("label") && field.has("value") && fieldIndex <= 7) {
                        setAdditionalField(
                                report,
                                fieldIndex,
                                field.get("label").asText(),
                                field.get("value").asText());
                        fieldIndex++;
                    }
                }
            }
        }

        logger.info("✅ Entity updated");
    }

    /**
     * Atualizar dados específicos existentes com dados do DTO (UPDATE)
     */
    public void updateSpecificData(PerformanceReportRepairElevatorSpecificData specificData,
                                   MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        logger.info("📝 Updating specific data for Performance Report Repair Elevator");

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // Atualizar apenas campos que existem no DTO
        if (reportData.has("reportNumber")) {
            specificData.setReportNumber(getStringValue(reportData, "reportNumber"));
        }
        if (reportData.has("inspectors") || reportData.has("inpectorsWorkers")) {
            String inspectors = reportData.has("inspectors")
                    ? getStringValue(reportData, "inspectors")
                    : getStringValue(reportData, "inpectorsWorkers");
            specificData.setInpectorsWorkers(inspectors);
        }
        if (reportData.has("statementOfwork")) {
            specificData.setStatementOfwork(getStringValue(reportData, "statementOfwork"));
        }
        if (reportData.has("workCompleted")) {
            specificData.setWorkCompleted(getStringValue(reportData, "workCompleted"));
        }
        if (reportData.has("windturbineOperable") || reportData.has("turbineOperable")) {
            String operable = reportData.has("windturbineOperable")
                    ? getStringValue(reportData, "windturbineOperable")
                    : getStringValue(reportData, "turbineOperable");
            specificData.setTurbineOperable(operable);
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

    /**
     * Método auxiliar para extrair string do JSON
     */
    private String getStringValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }

    /**
     * Método auxiliar para extrair integer do JSON
     */
    private Integer getIntegerValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asInt();
        }
        return null;
    }

    /**
     * Método auxiliar para definir campos adicionais na entidade Report
     */
    private void setAdditionalField(Report report, int index, String label, String value) {
        switch (index) {
            case 1:
                report.setAdditionalField1Label(label);
                report.setAdditionalField1Text(value);
                break;
            case 2:
                report.setAdditionalField2Label(label);
                report.setAdditionalField2Text(value);
                break;
            case 3:
                report.setAdditionalField3Label(label);
                report.setAdditionalField3Text(value);
                break;
            case 4:
                report.setAdditionalField4Label(label);
                report.setAdditionalField4Text(value);
                break;
            case 5:
                report.setAdditionalField5Label(label);
                report.setAdditionalField5Text(value);
                break;
            case 6:
                report.setAdditionalField6Label(label);
                report.setAdditionalField6Text(value);
                break;
            case 7:
                report.setAdditionalField7Label(label);
                report.setAdditionalField7Text(value);
                break;
            default:
                logger.warn("⚠️ Invalid additional field index: {}", index);
                break;
        }
    }
}