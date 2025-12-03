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
}