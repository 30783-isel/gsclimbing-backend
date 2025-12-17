package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter para Performance Report Repair Elevator
 * Converte entre DTOs mobile e entidade Report
 */
@Component
public class PerformanceRepairElevatorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorAdapter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converter DTO mobile para entidade Report (CREATE)
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

        // IDs
        report.setProjectoId(getJsonInteger(reportData, "projectoId"));
        report.setTurbinaId(dto.getTurbineId().intValue());
        report.setTurbine(turbine);

        // Tipo de relatório (6 = Performance Report Repair Elevator)
        report.setTypeReport(6);

        // Campos específicos do Performance Report Repair Elevator
        // Página 2: Statement of Work
        if (reportData.has("workCompleted")) {
            report.setAdditionalField1Label("Work Completed");
            report.setAdditionalField1Text(getStringValue(reportData, "workCompleted"));
        }
        if (reportData.has("windturbineOperable")) {
            report.setAdditionalField2Label("Windturbine Operable");
            report.setAdditionalField2Text(getStringValue(reportData, "windturbineOperable"));
        }

        // Página 3: Performance Report (campo de texto livre)
        if (reportData.has("performanceReport")) {
            report.setAdditionalField3Label("Performance Report");
            report.setAdditionalField3Text(getStringValue(reportData, "performanceReport"));
        }

        // Inspectors/Workers
        if (reportData.has("inspectors")) {
            report.setAdditionalField4Label("Inspectors/Workers");
            report.setAdditionalField4Text(getStringValue(reportData, "inspectors"));
        }

        // Campos adicionais extras (Página 5)
        int fieldIndex = 5;
        for (int i = 1; i <= 3; i++) {
            String labelKey = "additionalField" + i;
            if (reportData.has(labelKey)) {
                JsonNode field = reportData.get(labelKey);
                if (field.has("label") && field.has("value")) {
                    setAdditionalField(report, fieldIndex,
                            field.get("label").asText(),
                            field.get("value").asText());
                    fieldIndex++;
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
     * Método auxiliar para extrair integer do JSON
     */
    private Integer getJsonInteger(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asInt();
        }
        return null;
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

        // Atualizar campos específicos
        if (reportData.has("workCompleted")) {
            report.setAdditionalField1Label("Work Completed");
            report.setAdditionalField1Text(getStringValue(reportData, "workCompleted"));
        }
        if (reportData.has("windturbineOperable")) {
            report.setAdditionalField2Label("Windturbine Operable");
            report.setAdditionalField2Text(getStringValue(reportData, "windturbineOperable"));
        }
        if (reportData.has("performanceReport")) {
            report.setAdditionalField3Label("Performance Report");
            report.setAdditionalField3Text(getStringValue(reportData, "performanceReport"));
        }
        if (reportData.has("inspectors")) {
            report.setAdditionalField4Label("Inspectors/Workers");
            report.setAdditionalField4Text(getStringValue(reportData, "inspectors"));
        }

        // Atualizar campos adicionais extras
        int fieldIndex = 5;
        for (int i = 1; i <= 3; i++) {
            String labelKey = "additionalField" + i;
            if (reportData.has(labelKey)) {
                JsonNode field = reportData.get(labelKey);
                if (field.has("label") && field.has("value")) {
                    setAdditionalField(report, fieldIndex,
                            field.get("label").asText(),
                            field.get("value").asText());
                    fieldIndex++;
                }
            }
        }

        report.setModifiedDate(LocalDateTime.now());
        logger.info("✅ Entity updated");
    }

    // Métodos auxiliares
    private String getStringValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull()
                ? node.get(fieldName).asText()
                : "";
    }

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
        }
    }
}