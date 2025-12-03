package com.gsclimbing.x.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para receber dados do Defect Inspection Report da aplicação mobile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectInspectionReportDTO {

    // Informações básicas (obrigatórias)
    private String site;

    private String wtgNumber;

    private String wtgType;

    private String yearConstruction;

    private Integer projectoId;

    private Integer turbinaId;

    // User ID para tracking
    private String userId;

    // Lista de IDs de fotos já carregadas
    private List<String> photoFileIds;

    // Campos adicionais (7 pares label-value)
    private AdditionalFieldDTO additionalField1;
    private AdditionalFieldDTO additionalField2;
    private AdditionalFieldDTO additionalField3;
    private AdditionalFieldDTO additionalField4;
    private AdditionalFieldDTO additionalField5;
    private AdditionalFieldDTO additionalField6;
    private AdditionalFieldDTO additionalField7;

    /**
     * Inner class para campos adicionais
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalFieldDTO {
        private String label;
        private String value;
    }
}