package com.gsclimbing.x.database.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service para validar relatórios antes de submeter
 */
@Service
public class ReportValidationService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validar relatório Defect Inspection
     */
    public MobileReportDTO.ValidationResponseDTO validateDefectInspectionReport(
            String reportDataJson,
            Integer photoCount
    ) {
        List<MobileReportDTO.ValidationErrorDTO> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            JsonNode reportData = objectMapper.readTree(reportDataJson);

            // Campos obrigatórios
            validateRequiredField(reportData, "site", "Site", errors);
            validateRequiredField(reportData, "wtgNumber", "WTG Number", errors);
            validateRequiredField(reportData, "wtgType", "WTG Type", errors);
            validateRequiredField(reportData, "yearConstruction", "Year of Construction", errors);
            validateRequiredField(reportData, "dateInspection", "Date of Inspection", errors);
            //validateRequiredField(reportData, "inspectedBy", "Inspected By", errors);

            // Validar fotos
            if (photoCount == null || photoCount < 1) {
                errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                        .field("photos")
                        .message("Pelo menos 1 fotografia é obrigatória")
                        .type("required")
                        .build());
            }

            // Avisos (não bloqueantes)
            if (reportData.has("observations") && 
                reportData.get("observations").asText().length() < 10) {
                warnings.add("As observações são muito curtas. Considere adicionar mais detalhes.");
            }

        } catch (Exception e) {
            errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                    .field("reportData")
                    .message("Formato de dados inválido")
                    .type("invalid")
                    .build());
        }

        return MobileReportDTO.ValidationResponseDTO.builder()
                .isValid(errors.isEmpty())
                .errors(errors)
                .warnings(warnings)
                .build();
    }

    /**
     * Validar relatório Examination Transformer
     */
    public MobileReportDTO.ValidationResponseDTO validateExaminationTransformer(
            String reportDataJson,
            Integer photoCount
    ) {
        List<MobileReportDTO.ValidationErrorDTO> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            JsonNode reportData = objectMapper.readTree(reportDataJson);

            // Campos obrigatórios específicos deste tipo
            validateRequiredField(reportData, "site", "Site", errors);
            validateRequiredField(reportData, "wtgNumber", "WTG Number", errors);
            validateRequiredField(reportData, "transformerType", "Transformer Type", errors);
            validateRequiredField(reportData, "dateExamination", "Date of Examination", errors);
            validateRequiredField(reportData, "examinedBy", "Examined By", errors);

            // Validar campos técnicos
            if (reportData.has("voltage") && reportData.get("voltage").asDouble() <= 0) {
                errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                        .field("voltage")
                        .message("A voltagem deve ser maior que 0")
                        .type("invalid")
                        .build());
            }

        } catch (Exception e) {
            errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                    .field("reportData")
                    .message("Formato de dados inválido")
                    .type("invalid")
                    .build());
        }

        return MobileReportDTO.ValidationResponseDTO.builder()
                .isValid(errors.isEmpty())
                .errors(errors)
                .warnings(warnings)
                .build();
    }

    /**
     * Validar relatório genérico baseado no tipo
     */
    public MobileReportDTO.ValidationResponseDTO validateReport(
            Integer reportType,
            String reportDataJson,
            Integer photoCount
    ) {
        switch (reportType) {
            case 0: // Defect Inspection Report
                return validateDefectInspectionReport(reportDataJson, photoCount);
            case 1: // Examination Transformer
                return validateExaminationTransformer(reportDataJson, photoCount);
            case 2: // Measurements MV Switchgear
            case 3: // Measurements 6KV
            case 4: // Measurements 690V/400V
            case 5: // Onboard Crane Inspection
            case 6: // Performance Report Repair Elevator
            case 7: // Statutory Inspection Report
                return validateGenericReport(reportDataJson, photoCount);
            default:
                List<MobileReportDTO.ValidationErrorDTO> errors = new ArrayList<>();
                errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                        .field("reportType")
                        .message("Tipo de relatório desconhecido")
                        .type("invalid")
                        .build());
                return MobileReportDTO.ValidationResponseDTO.builder()
                        .isValid(false)
                        .errors(errors)
                        .warnings(new ArrayList<>())
                        .build();
        }
    }

    /**
     * Validação genérica para relatórios não implementados especificamente
     */
    private MobileReportDTO.ValidationResponseDTO validateGenericReport(
            String reportDataJson,
            Integer photoCount
    ) {
        List<MobileReportDTO.ValidationErrorDTO> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            JsonNode reportData = objectMapper.readTree(reportDataJson);

            // Validações básicas
            validateRequiredField(reportData, "site", "Site", errors);
            validateRequiredField(reportData, "date", "Date", errors);

            if (photoCount == null || photoCount < 1) {
                warnings.add("Recomenda-se adicionar pelo menos 1 fotografia");
            }

        } catch (Exception e) {
            errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                    .field("reportData")
                    .message("Formato de dados inválido")
                    .type("invalid")
                    .build());
        }

        return MobileReportDTO.ValidationResponseDTO.builder()
                .isValid(errors.isEmpty())
                .errors(errors)
                .warnings(warnings)
                .build();
    }

    /**
     * Método auxiliar para validar campo obrigatório
     */
    private void validateRequiredField(
            JsonNode data,
            String fieldName,
            String fieldLabel,
            List<MobileReportDTO.ValidationErrorDTO> errors
    ) {
        if (!data.has(fieldName) || 
            data.get(fieldName).isNull() || 
            data.get(fieldName).asText().trim().isEmpty()) {
            
            errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                    .field(fieldName)
                    .message(fieldLabel + " é obrigatório")
                    .type("required")
                    .build());
        }
    }













    /**
     * Validar Performance Report Repair Elevator
     */
    public MobileReportDTO.ValidationResponseDTO validatePerformanceRepairElevator(
            String reportDataJson,
            Integer photoCount
    ) {
        List<MobileReportDTO.ValidationErrorDTO> errors = new ArrayList<>();
        List<MobileReportDTO.ValidationErrorDTO> warnings = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode reportData = mapper.readTree(reportDataJson);

            // Validar campos obrigatórios
            validateRequiredField(reportData, "site", "Site é obrigatório", errors);
            validateRequiredField(reportData, "wtgNumber", "WTG Number é obrigatório", errors);
            validateRequiredField(reportData, "wtgType", "WTG Type é obrigatório", errors);
            validateRequiredField(reportData, "yearConstruction", "Year of Construction é obrigatório", errors);

            // Validar campos específicos
            if (reportData.has("workCompleted")) {
                String value = reportData.get("workCompleted").asText().toLowerCase();
                if (!value.equals("yes") && !value.equals("no")) {
                    warnings.add(MobileReportDTO.ValidationErrorDTO.builder()
                            .field("workCompleted")
                            .message("Deve ser 'yes' ou 'no'")
                            .type("warning")
                            .build());
                }
            }

            if (reportData.has("windturbineOperable")) {
                String value = reportData.get("windturbineOperable").asText().toLowerCase();
                if (!value.equals("yes") && !value.equals("no") && !value.equals("limited")) {
                    warnings.add(MobileReportDTO.ValidationErrorDTO.builder()
                            .field("windturbineOperable")
                            .message("Deve ser 'yes', 'no' ou 'limited'")
                            .type("warning")
                            .build());
                }
            }

            // Validar fotos
            if (photoCount == null || photoCount == 0) {
                warnings.add(MobileReportDTO.ValidationErrorDTO.builder()
                        .field("photos")
                        .message("Recomenda-se adicionar fotos ao relatório")
                        .type("warning")
                        .build());
            }

        } catch (Exception e) {
            errors.add(MobileReportDTO.ValidationErrorDTO.builder()
                    .field("reportData")
                    .message("Formato de dados inválido")
                    .type("invalid")
                    .build());
        }

        return MobileReportDTO.ValidationResponseDTO.builder()
                .isValid(errors.isEmpty())
                .errors(errors)
                .warnings(warnings)
                .build();
    }
}
