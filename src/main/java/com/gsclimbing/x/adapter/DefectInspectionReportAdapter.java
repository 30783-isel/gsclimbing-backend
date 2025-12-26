package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter para converter DTOs em entidades e vice-versa
 */
@Component
public class DefectInspectionReportAdapter {



    /**
     * Converte MobileReportDTO do mobile para entidade DefectsInspectionReport
     *
     * @param dto Dados vindos do mobile
     * @param turbine Turbina associada
     * @return Entidade pronta para salvar
     */
    public DefectsInspectionReport toEntity(MobileReportDTO.ReportCreateUpdateDTO dto, Turbine turbine) {
        DefectsInspectionReport report = new DefectsInspectionReport();

        // Gerar UUID único
        report.setUuid(UUID.randomUUID().toString());

        // Datas
        LocalDateTime now = LocalDateTime.now();
        report.setCreateDate(now);
        report.setModifiedDate(now);

        try {
            // Parse do JSON reportData
            ObjectMapper mapper = new ObjectMapper();
            JsonNode reportData = mapper.readTree(dto.getReportData());

            // Informações básicas do relatório
            report.setSite(getJsonString(reportData, "site"));
            report.setWtgNumber(getJsonString(reportData, "wtgNumber"));
            report.setWtgType(getJsonString(reportData, "wtgType"));
            report.setYearConstruction(getJsonString(reportData, "yearConstruction"));

            // IDs
            report.setProjectoId(getJsonInteger(reportData, "projectoId"));
            report.setTurbinaId(dto.getTurbineId().intValue());
            report.setTurbine(turbine);

            // Tipo de relatório (0 = Defect Inspection Report)
            report.setTypeReport(dto.getReportType() != null ? dto.getReportType() : 0);
            report.setReportType(dto.getReportType() != null ? dto.getReportType() : 0);

            // Flags de controlo
            report.setLocked("N"); // Não bloqueado por padrão
            report.setPermission2Edit("Y"); // Pode editar por padrão
            report.setInsertImagesChk("Y"); // Tem imagens

            // Campos adicionais (additionalField1-7)
            for (int i = 1; i <= 7; i++) {
                String fieldKey = "additionalField" + i;
                if (reportData.has(fieldKey)) {
                    JsonNode field = reportData.get(fieldKey);
                    String label = getJsonString(field, "label");
                    String value = getJsonString(field, "value");

                    setAdditionalField(report, i, label, value);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error parsing MobileReportDTO reportData JSON", e);
        }

        return report;
    }

    /**
     * Método auxiliar para extrair string do JSON
     */
    private String getJsonString(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
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
     * Método auxiliar para setar campos adicionais dinamicamente
     */
    private void setAdditionalField(DefectsInspectionReport report, int index, String label, String value) {
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


    /**
     * Converte DTO do mobile para entidade DefectsInspectionReport
     *
     * @param dto Dados vindos do mobile
     * @param turbine Turbina associada
     * @return Entidade pronta para salvar
     */
    public DefectsInspectionReport toEntity(DefectInspectionReportDTO dto, Turbine turbine) {
        DefectsInspectionReport report = new DefectsInspectionReport();

        // Gerar UUID único
        report.setUuid(UUID.randomUUID().toString());

        // Datas
        LocalDateTime now = LocalDateTime.now();
        report.setCreateDate(now);
        report.setModifiedDate(now);

        // Informações básicas do relatório
        report.setSite(dto.getSite());
        report.setWtgNumber(dto.getWtgNumber());
        report.setWtgType(dto.getWtgType());
        report.setYearConstruction(dto.getYearConstruction());

        // IDs
        report.setProjectoId(dto.getProjectoId());
        report.setTurbinaId(dto.getTurbinaId());
        report.setTurbine(turbine);

        // Tipo de relatório (0 = Defect Inspection Report)
        report.setTypeReport(dto.getReportType());
        report.setReportType(dto.getReportType());

        // Flags de controlo
        report.setLocked("N"); // Não bloqueado por padrão
        report.setPermission2Edit("Y"); // Pode editar por padrão
        report.setInsertImagesChk("Y"); // Tem imagens

        // Campos adicionais
        if (dto.getAdditionalField1() != null) {
            report.setAdditionalField1Label(dto.getAdditionalField1().getLabel());
            report.setAdditionalField1Text(dto.getAdditionalField1().getValue());
        }

        if (dto.getAdditionalField2() != null) {
            report.setAdditionalField2Label(dto.getAdditionalField2().getLabel());
            report.setAdditionalField2Text(dto.getAdditionalField2().getValue());
        }

        if (dto.getAdditionalField3() != null) {
            report.setAdditionalField3Label(dto.getAdditionalField3().getLabel());
            report.setAdditionalField3Text(dto.getAdditionalField3().getValue());
        }

        if (dto.getAdditionalField4() != null) {
            report.setAdditionalField4Label(dto.getAdditionalField4().getLabel());
            report.setAdditionalField4Text(dto.getAdditionalField4().getValue());
        }

        if (dto.getAdditionalField5() != null) {
            report.setAdditionalField5Label(dto.getAdditionalField5().getLabel());
            report.setAdditionalField5Text(dto.getAdditionalField5().getValue());
        }

        if (dto.getAdditionalField6() != null) {
            report.setAdditionalField6Label(dto.getAdditionalField6().getLabel());
            report.setAdditionalField6Text(dto.getAdditionalField6().getValue());
        }

        if (dto.getAdditionalField7() != null) {
            report.setAdditionalField7Label(dto.getAdditionalField7().getLabel());
            report.setAdditionalField7Text(dto.getAdditionalField7().getValue());
        }

        return report;
    }

    /**
     * Converte entidade para DTO de resposta
     *
     * @param report Entidade salva
     * @param numberPictures Número de fotos associadas
     * @return DTO de resposta para o mobile
     */
    public DefectInspectionReportResponseDTO toResponseDTO(
            DefectsInspectionReport report,
            Integer numberPictures) {

        return DefectInspectionReportResponseDTO.builder()
                .reportId(report.getReportId())
                .uuid(report.getUuid())
                .createDate(report.getCreateDate())
                .site(report.getSite())
                .wtgNumber(report.getWtgNumber())
                .wtgType(report.getWtgType())
                .yearConstruction(report.getYearConstruction())
                .projectoId(report.getProjectoId())
                .turbinaId(report.getTurbinaId())
                .numberPictures(numberPictures)
                .additionalField1Label(report.getAdditionalField1Label())
                .additionalField1Text(report.getAdditionalField1Text())
                .additionalField2Label(report.getAdditionalField2Label())
                .additionalField2Text(report.getAdditionalField2Text())
                .additionalField3Label(report.getAdditionalField3Label())
                .additionalField3Text(report.getAdditionalField3Text())
                .additionalField4Label(report.getAdditionalField4Label())
                .additionalField4Text(report.getAdditionalField4Text())
                .additionalField5Label(report.getAdditionalField5Label())
                .additionalField5Text(report.getAdditionalField5Text())
                .additionalField6Label(report.getAdditionalField6Label())
                .additionalField6Text(report.getAdditionalField6Text())
                .additionalField7Label(report.getAdditionalField7Label())
                .additionalField7Text(report.getAdditionalField7Text())
                .success(true)
                .message("Report created successfully")
                .build();
    }
}