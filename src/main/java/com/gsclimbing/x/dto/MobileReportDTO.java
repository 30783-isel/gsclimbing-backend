package com.gsclimbing.x.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs para comunicação com a app mobile
 */
public class MobileReportDTO {

    /**
     * DTO para criar/atualizar relatório
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReportCreateUpdateDTO {
        private Long turbineId;
        private Integer reportType;
        private String language; // "EN" ou "ES"
        private Boolean offlineCreated;
        
        // Campos do relatório em JSON
        private String reportData; // JSON com todos os campos do relatório
        
        // TODO Verificar todos o JsonAlias
        @JsonAlias({"photoFileIds"})
        private List<Long> photoIds;
        private LocalDateTime inspectedBy;

    }

    /**
     * DTO para submeter relatório
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSubmitDTO {
        private Long reportId;
        private String language;
        private String reportData; // JSON completo
        private List<Long> photoIds;
    }

    /**
     * DTO de resposta do relatório
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponseDTO {
        private Long id;
        private Long turbineId;
        private String turbineName;
        private Integer reportType;
        private String status;
        private String language;
        private Boolean offlineCreated;
        private String reportData;
        private List<FileDataDTO> files;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime submittedAt;
        private String submittedBy;
        private Boolean unlockRequested;
        private LocalDateTime unlockRequestedAt;
        private Boolean canEdit;
        private Boolean isLocked;
        private List<ValidationErrorDTO> validationErrors;
    }

    /**
     * DTO de erro de validação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDTO {
        private String field;
        private String message;
        private String type; // "required", "invalid", etc
    }

    /**
     * DTO de ficheiro/foto
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDataDTO {
        private Long id;
        private String filename;
        private String url;
        private String mimeType;
        private Long size;
        private LocalDateTime uploadedAt;
    }

    /**
     * DTO de pedido de validação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationRequestDTO {
        private Integer reportType;
        private String reportData; // JSON
        private Integer photoCount;
    }

    /**
     * DTO de resposta de validação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResponseDTO {
        private Boolean isValid;
        private List<ValidationErrorDTO> errors;
        private List<String> warnings; // Avisos não bloqueantes
    }

    /**
     * DTO de pedido de desbloqueio
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnlockRequestDTO {
        private Long reportId;
        private String reason; // Motivo para editar
    }

    /**
     * DTO de histórico
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportHistoryDTO {
        private Long id;
        private String changedBy;
        private LocalDateTime changedAt;
        private String action;
        private String fieldName;
        private String oldValue;
        private String newValue;
        private String description;
    }

    /**
     * DTO de sincronização offline
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfflineSyncDTO {
        private String tempId; // ID temporário criado no mobile
        private Long turbineId;
        private Integer reportType;
        private String language;
        private String reportData;
        private List<PhotoUploadDTO> photos; // Fotos em base64
        private LocalDateTime createdAtDevice;
        private LocalDateTime inspectedBy;
    }

    /**
     * DTO de upload de foto
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoUploadDTO {
        private String base64Data;
        private String filename;
        private String mimeType;
    }

    /**
     * DTO de resposta de sincronização
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncResponseDTO {
        private Boolean success;
        private Long reportId; // ID real no servidor
        private String tempId; // ID temporário do mobile
        private String message;
        private List<ValidationErrorDTO> errors;
    }


    public static ReportCreateUpdateDTO mapToReportCreateUpdateDTO(OfflineSyncDTO offlineSync, List<Long> uploadedPhotoIds) {
        ReportCreateUpdateDTO dto = new ReportCreateUpdateDTO();

        dto.setTurbineId(offlineSync.getTurbineId());
        dto.setReportType(offlineSync.getReportType());
        dto.setLanguage(offlineSync.getLanguage());
        dto.setReportData(offlineSync.getReportData());
        dto.setOfflineCreated(true); // Marca como criado offline
        dto.setPhotoIds(uploadedPhotoIds); // IDs das fotos já carregadas previamente
        dto.setInspectedBy(offlineSync.getInspectedBy());
        return dto;
    }
}
