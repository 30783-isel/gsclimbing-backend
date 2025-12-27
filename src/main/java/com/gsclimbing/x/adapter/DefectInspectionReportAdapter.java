package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adapter para Defect Inspection Reports
 * Estende BaseReportAdapter para reutilizar lógica comum
 */
@Component
public class DefectInspectionReportAdapter extends BaseReportAdapter<DefectsInspectionReport> {

    // ========================================
    // CONVERSÃO DTO -> ENTIDADE (CREATE)
    // ========================================

    @Override
    public DefectsInspectionReport toEntity(
            MobileReportDTO.ReportCreateUpdateDTO dto,
            Turbine turbine) throws Exception {

        logger.info("📋 Converting DTO to Defects Inspection Report entity");

        // Criar nova entidade
        DefectsInspectionReport report = new DefectsInspectionReport();
        report.setTypeReport(0); // Defect Inspection Report
        // Preencher campos base usando método da classe pai
        populateBaseFields(report, dto, turbine);

        // Preencher campos específicos (se houver)
        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // Defect Inspection não tem muitos campos específicos além dos base
        // Adicionar aqui se necessário no futuro

        // Campos adicionais (1-7)
        for (int i = 1; i <= 7; i++) {
            JsonNode fieldNode = reportData.get("additionalField" + i);
            if (fieldNode != null && fieldNode.has("label") && fieldNode.has("value")) {
                setAdditionalField(report, i,
                        fieldNode.get("label").asText(),
                        fieldNode.get("value").asText());
            }
        }

        logger.info("✅ Defect Inspection entity created");
        return report;
    }

    // ========================================
    // CONVERSÃO ENTIDADE -> DTO RESPONSE
    // ========================================

    /**
     * Converte entidade para DTO de resposta
     */
    public DefectInspectionReportResponseDTO toResponseDTO(
            DefectsInspectionReport report,
            int numberOfPhotos) {

        logger.info("📤 Converting entity to response DTO");

        return DefectInspectionReportResponseDTO.builder()
                .reportId(report.getReportId())
                .uuid(report.getUuid())
                .createDate(report.getCreateDate() != null ? LocalDateTime.parse(report.getCreateDate().toString()) : null)
                .site(report.getSite())
                .wtgNumber(report.getWtgNumber())
                .message("Report created successfully")
                .success(true)
                .numberPictures(numberOfPhotos)
                .build();
    }

    // ========================================
    // ATUALIZAÇÃO (UPDATE)
    // ========================================

    /**
     * Atualiza entidade existente com dados do DTO
     */
    public void updateEntity(
            DefectsInspectionReport report,
            MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {

        logger.info("📝 Updating Defects Inspection Report entity: {}", report.getReportId());

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