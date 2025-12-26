package com.gsclimbing.x.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta após criar o relatório
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectInspectionReportResponseDTO {

    private Integer reportId;
    private String uuid;
    private LocalDateTime createDate;
    private String site;
    private String wtgNumber;
    private String wtgType;
    private String yearConstruction;
    private Integer projectoId;
    private Integer turbinaId;
    private Integer numberPictures;
    private String message;
    private boolean success;

    // Campos adicionais
    private String additionalField1Label;
    private String additionalField1Text;
    private String additionalField2Label;
    private String additionalField2Text;
    private String additionalField3Label;
    private String additionalField3Text;
    private String additionalField4Label;
    private String additionalField4Text;
    private String additionalField5Label;
    private String additionalField5Text;
    private String additionalField6Label;
    private String additionalField6Text;
    private String additionalField7Label;
    private String additionalField7Text;
}