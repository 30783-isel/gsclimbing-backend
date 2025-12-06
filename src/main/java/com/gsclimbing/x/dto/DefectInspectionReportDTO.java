package com.gsclimbing.x.dto;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Report.ReportStatus status = Report.ReportStatus.DRAFT;

    private Integer reportType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<FileData> listaFileData = new ArrayList<>();

    /**
     * Erros de validação em formato JSON
     * Exemplo: [{"field":"site","message":"Campo obrigatório"}]
     */
    private String validationErrors;

    /**
     * Idioma do relatório (EN ou ES)
     */
    private String language = "EN";

    /**
     * Se o relatório foi criado offline
     */
    private Boolean offlineCreated = false;

    /**
     * Estado de sincronização
     */
    private Report.SyncStatus syncStatus = Report.SyncStatus.SYNCED;

    /**
     * Data e hora de submissão
     */
    private LocalDateTime submittedAt;

    /**
     * Username de quem submeteu
     */
    private String submittedBy;

    /**
     * Se foi pedido desbloqueio
     */
    private Boolean unlockRequested = false;

    /**
     * Data do pedido de desbloqueio
     */
    private LocalDateTime unlockRequestedAt;

    /**
     * Username de quem desbloqueou (admin)
     */
    private String unlockedBy;

    /**
     * Data de desbloqueio
     */
    private LocalDateTime unlockedAt;

    // ========== RELACIONAMENTOS ==========

    /**
     * Histórico de alterações
     */
    private List<MobileReportDTO.ReportHistoryDTO> historyList = new ArrayList<>();


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